package programmer;

import org.apache.commons.lang3.ArrayUtils;
import programmer.device.AtmelMicroController;
import programmer.device.MicroController;
import programmer.security.AESCBCEncryption;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Burak on 10/23/17.
 */
public class SOTAProtocol extends BaseProgrammingProtocol {

    // todo Logger and timer
    private STK500Protocol stk500Protocol;
    private AtmelMicroController atmelMicroController;
    private BaseConnection baseConnection;
    private ArrayList<byte[]> lastSentPackets;



    public STK500Protocol getStk500Protocol() {
        return stk500Protocol;
    }

    public void setStk500Protocol(STK500Protocol stk500Protocol) {
        this.stk500Protocol = stk500Protocol;
    }

    public SOTAProtocol(MicroController microController) {
        super(microController);
        try {
            stk500Protocol = new STK500Protocol(microController);
            if (microController instanceof AtmelMicroController) {
                this.atmelMicroController = (AtmelMicroController) microController;
            } else {
                throw new Exception("SOTA Protocol can only work with Atmel Micro-controllers.");
            }

            this.baseConnection = atmelMicroController.getBaseConnection();
            setSecurityManager(new AESCBCEncryption(microController));



        } catch (Exception ex)
        {

        }



    }


    public void startAuthenticationTask() throws InterruptedException
    {
        switch (atmelMicroController.getAuthMode())
        {
            case ON:
            {
                while (!AuthenticateMicroController())
                {
                    Thread.sleep(500);
                }

                break;
            }
            case OFF:{
                break;
            }
        }
    }
    @Override
    public void startFirmwareUploading(byte[] firmware)
    {
       TaskManager.getInstance().addTask(new SOTAFirmwareTransfer(microController));

    }

    private byte[] WrapWithMessageFormat(byte[] packet)
    {
     int innerPacketSize = packet.length;
     byte[] newPacket = new byte[innerPacketSize+3];
     newPacket[0] = 0x58;
     byte[] size = ByteBuffer.allocate(4).putInt(innerPacketSize).array();
     newPacket[1] = size[2];
     newPacket[2] = size[3];
     for(int i=0;i< innerPacketSize; i++)
         newPacket[i+3] = packet[i];
     return newPacket;
    }

    private boolean AuthenticateMicroController()
    {
        byte[] encryptedPacket = ArrayUtils.toPrimitive(securityManager.encrypt(stk500Protocol.Authenticate()).getData());
        baseConnection.sendDataToMicroController(WrapWithMessageFormat(encryptedPacket));
        switch(microController.getOtaMode())
        {
            case ECHO_INTEGRITY:{
                if(PacketIntegrityCheck(SOTAGlobals.OTA_MODE.ECHO_INTEGRITY,encryptedPacket) == false)
                    return false;
                break;
            }

        }
        return Acknowledgement(stk500Protocol.AuthenticationAnswer(),true);
    }

    private boolean SendFirmwareChunk(byte[] firmwareChunk)
    {
        byte[] encryptedPacket = ArrayUtils.toPrimitive(securityManager.encrypt(stk500Protocol.GetCMD_PROGRAM_FLASH_ISP(firmwareChunk)).getData());

        baseConnection.sendDataToMicroController(WrapWithMessageFormat(encryptedPacket));
        switch(microController.getOtaMode())
        {
            case ECHO_INTEGRITY:{
                if(PacketIntegrityCheck(SOTAGlobals.OTA_MODE.ECHO_INTEGRITY,encryptedPacket) == false)
                    return false;
                break;
            }
            case CONSERVATIVE_ECHO_INTEGRITY:
            {
                lastSentPackets.add(encryptedPacket);
                if(Acknowledgement(stk500Protocol.GetCMD_PROGRAM_FLASH_ISPAnswer(),false))
                {

                }
            }

        }
        return Acknowledgement(stk500Protocol.GetCMD_PROGRAM_FLASH_ISPAnswer(),true);

    }

    private boolean sendRebootCMD(){
        String message = "reboot\r\n";
        byte[] byteArray = message.getBytes();
        byte[] desiredInput  = {0x52,0x53,0x54,0x2b,0x49,0x4e,0x46,0x4f,0x52,0x4d,0x0d,0x0a};
        return Acknowledgement(desiredInput,false);
    }


    public boolean GetSync()
    {
        byte[] encryptedPacket = ArrayUtils.toPrimitive(securityManager.encrypt(stk500Protocol.GetCMD_SIGN_ON()).getData());

        baseConnection.sendDataToMicroController(WrapWithMessageFormat(encryptedPacket));

        switch(microController.getOtaMode())
        {
            case ECHO_INTEGRITY:{
                if(PacketIntegrityCheck(SOTAGlobals.OTA_MODE.ECHO_INTEGRITY,encryptedPacket) == false)
                    return false;
            break;
            }

        }
        return Acknowledgement(stk500Protocol.GetCMD_SIGN_ONAnswer(),true);

    }

    private boolean LoadAddress(byte[] address)
    {
        byte[] encryptedPacket = ArrayUtils.toPrimitive(securityManager.encrypt(stk500Protocol.GetCMD_LOAD_ADDRESS(address)).getData());

        baseConnection.sendDataToMicroController(WrapWithMessageFormat(encryptedPacket));
        switch(microController.getOtaMode())
        {
            case ECHO_INTEGRITY:{
                if(PacketIntegrityCheck(SOTAGlobals.OTA_MODE.ECHO_INTEGRITY,encryptedPacket) == false)
                    return false;
                break;
            }

        }
        return Acknowledgement(stk500Protocol.GetCMD_LOAD_ADDRESSAnswer(),true);
    }

    public boolean EnableProgramMode()
    {
        byte[] encryptedPacket = ArrayUtils.toPrimitive(securityManager.encrypt(stk500Protocol.GetCMD_ENTER_PROGMODE_ISP()).getData());

        baseConnection.sendDataToMicroController(WrapWithMessageFormat(encryptedPacket));
        switch(microController.getOtaMode())
        {
            case ECHO_INTEGRITY:{
                if(PacketIntegrityCheck(SOTAGlobals.OTA_MODE.ECHO_INTEGRITY,encryptedPacket) == false)
                    return false;
                break;
            }

        }
        return Acknowledgement(stk500Protocol.GetCM_ENTER_PROGMODE_ISPAnswer(),true);
    }

    private boolean CloseProgramMode()
    {

            byte[] encryptedPacket = ArrayUtils.toPrimitive(securityManager.encrypt(stk500Protocol.GetCMD_LEAVE_PROGMODE_ISP()).getData());

            baseConnection.sendDataToMicroController(WrapWithMessageFormat(encryptedPacket));
        switch(microController.getOtaMode())
        {
            case ECHO_INTEGRITY:{
                if(PacketIntegrityCheck(SOTAGlobals.OTA_MODE.ECHO_INTEGRITY,encryptedPacket) == false)
                    return false;
                break;
            }

        }
            return Acknowledgement(stk500Protocol.GetCMD_LEAVE_PROGMODE_ISPAnswer(),true);

    }

    private boolean PacketIntegrityCheck(SOTAGlobals.OTA_MODE ota_mode, byte[] packet)
    {
        if(ota_mode== SOTAGlobals.OTA_MODE.ECHO_INTEGRITY)
        {
            return (Acknowledgement(packet,false));
        }
        else if(ota_mode == SOTAGlobals.OTA_MODE.BASIC_CONFIDENTIALITY)
        {}
        else if(ota_mode == SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY)
        {

        }
        else if(ota_mode == SOTAGlobals.OTA_MODE.CONSERVATIVE_ECHO_INTEGRITY)
        {
            return (Acknowledgement(packet,false));
        }
        else if (ota_mode == SOTAGlobals.OTA_MODE.CHECKSUM_CHECK)
        {
            byte checksumVariable = (byte) (packet[0] ^ 0);

            for(int i =1; i<packet.length; i++)
            {
                if(i == packet.length)
                {
                    if(packet[i] == checksumVariable)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else {
                    checksumVariable ^= packet[i];
                }
            }

            return false;
        }

        return false;
    }

    public boolean sendFirmware(byte[] firmware)
    {
        Integer topAddress = new Integer(-2147483648);

        int totalChunkNumber = firmware.length/microController.getMaximumFirmwareTransferPacketSize();
        if(firmware.length % microController.getMaximumFirmwareTransferPacketSize() != 0)
            totalChunkNumber++;
        for(int i=0; i< totalChunkNumber; i++)
        {
            String loadedAddress = Integer.toBinaryString(topAddress);
            Long binaryFormattedAddress = Long.parseLong(loadedAddress, 2);
            int parts = Long.toHexString(topAddress).length()/4;
            byte[] address = new byte[4];
            String[] hexArray = Long.toHexString(topAddress).split("(?<=\\G.{"+parts+"})");

            address[0] = (byte) Integer.parseInt(hexArray[0],16);
            address[1] = (byte) Integer.parseInt(hexArray[1],16);
            address[2] = (byte) Integer.parseInt(hexArray[2],16);
            address[3] = (byte) Integer.parseInt(hexArray[3],16);

            while(LoadAddress(address)){};

            byte[] firmwareChunk;

            if(i != totalChunkNumber)
            {
                firmwareChunk = new byte[microController.getMaximumFirmwareTransferPacketSize()];
            }
            else
            {
                firmwareChunk = new byte[firmware.length% microController.getMaximumFirmwareTransferPacketSize()];
            }

            while (SendFirmwareChunk(firmwareChunk)){}

        }
        return true;

    }


    public boolean Acknowledgement(byte[] desiredInput, boolean isEncrypted) {

        long timestart = System.currentTimeMillis();
        int index = 0;
        //todo time and tcp activity logger
        byte[] inputData = new byte[atmelMicroController.getMaximumPacketSize()];

        String desiredPacket = new String(desiredInput);
        while((timestart + baseConnection.getTimeout()) > System.currentTimeMillis())
        {
            // System.out.println((timestart + Constants.TIMEOUT) +" - "+System.currentTimeMillis());
            int b;

                inputData = ArrayUtils.toPrimitive(baseConnection.getBytesFromMicroController().getReceivedData());
                if(inputData == null || inputData.length == 0)
                    return false;
                if(!isEncrypted) {
                    String str = new String(inputData);
                    if (str.indexOf(desiredPacket) >= 0) {
                        return true;
                    }
                }
                else
                {
                    if(inputData.length>2) {
                        if (inputData[0] == 0x58) {
                            int size = (((inputData[1] & 0xff) << 8) | (inputData[2] & 0xff));
                            byte[] encryptedPortion = null;
                            if (size == inputData.length - 3) {
                               encryptedPortion = createSubByteArray(3, inputData);
                            }
                            String str="";
//                            if(encryptedPortion != null)

                            byte[] receivedData = ArrayUtils.toPrimitive(securityManager.decrypt(encryptedPortion).getData());

                            STK500Protocol.PACKET_PARSER_STATE packet_parser_state = STK500Protocol.PACKET_PARSER_STATE.MESSAGE_START;


                            for(int i=0; i< receivedData.length; i++)
                            {
                                /*
                                  MESSAGE_START,
                                  MESSAGE_SIZE,
                                  TOKEN,
                                  DATARECEIVING,
                                  CHECKSUM

                                 */
                                STK500PacketParser stk500PacketParser = new STK500PacketParser(receivedData);
                                if(stk500PacketParser.Parse())
                                {
                                    byte[] data = stk500PacketParser.getData();
                                    str = data.toString();
                                }

                            }

                            if (str.indexOf(desiredPacket) >= 0) {
                                stk500Protocol.IncreaseSequenceNumber();
                                return true;
                            }
                        } else {
                            //todo log
                            // 12 57 e6 d6 5b bc 6e 73 4f 83 62 5f 68 46 79 0b 9f c0 4f 89 e7 83 57 75 4e a1 9f 2b
                        }
                    }
                }
        }
        return false;
    }




    private byte[] createSubByteArray(int cutoffSize, byte[] array) {
        byte[] newArray = new byte[array.length - cutoffSize];
        for (int i = (cutoffSize); i < array.length; i++)
        {
            newArray[i-cutoffSize] = array[i];
        }
        return newArray;

    }



}
