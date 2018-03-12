package programmer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programmer.device.AtmelMicroController;
import programmer.device.BaseMicroController;
import programmer.model.ProgrammerTaskResult;
import programmer.security.AESCBCEncryption;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Burak on 10/23/17.
 */
public class SOTAProtocol extends BaseProgrammingProtocol {

    // todo Logger and timer
    private STK500Protocol stk500Protocol;
    private AtmelMicroController atmelMicroController;
    private BaseConnection baseConnection;
    private ArrayList<byte[]> lastSentPackets;
    private Logger sotaErrorAppender = LogManager.getLogger("SOTAErrorLogger");
    private Logger resultLogger = LogManager.getLogger(("SOTAResultLogger"));
    private int maximumAllowedTry = 10;



    public STK500Protocol getStk500Protocol() {
        return stk500Protocol;
    }

    public void setStk500Protocol(STK500Protocol stk500Protocol) {
        this.stk500Protocol = stk500Protocol;
    }

    public SOTAProtocol(BaseMicroController baseMicroController) {
        super(baseMicroController);
        try {
            stk500Protocol = new STK500Protocol(baseMicroController);
            if (baseMicroController instanceof AtmelMicroController) {
                this.atmelMicroController = (AtmelMicroController) baseMicroController;
            } else {
                throw new Exception("SOTA Protocol can only work with Atmel Micro-controllers.");
            }

            this.baseConnection = atmelMicroController.getBaseConnection();
            setSecurityManager(new AESCBCEncryption(baseMicroController));



        } catch (Exception ex)
        {
            sotaErrorAppender.error("SOTA Protocol constructor: ",ex);
        }



    }


    public void startAuthenticationTask() throws InterruptedException
    {
        switch (atmelMicroController.getAuthMode())
        {
            case ON:
            {
                long startingTime = System.currentTimeMillis();
                while (!AuthenticateMicroController())
                {
                    Thread.sleep(50);
                }
                long finishingTime = System.currentTimeMillis();

                resultLogger.trace(atmelMicroController.getDeviceName()+" - Authentication Part took: "+ (finishingTime - startingTime) + " ms");
                break;
            }
            case OFF:{
                break;
            }
        }
    }
    @Override
    public boolean  startFirmwareUploading(byte[] firmware)
    {

        try {
            long startingTime = System.currentTimeMillis();
            while (!sendRebootCMD()) {
                Thread.sleep(50);
            }
            ;
            long finishingTime = System.currentTimeMillis();
            resultLogger.trace(atmelMicroController.getDeviceName() + " - Reboot Producure took: " + (finishingTime - startingTime) + " ms");

            startAuthenticationTask();
        } catch (Exception ex) {
            sotaErrorAppender.error("An Error occured in authenticatation part", ex);
        }
        Future<ProgrammerTaskResult> programmerTaskResultFuture = TaskManager.getInstance().addTask(new SOTAFirmwareTransfer(baseMicroController));
        try {
            ProgrammerTaskResult programmerTaskResult = programmerTaskResultFuture.get();
            if (programmerTaskResult.isSuccessed() == false) {
                System.out.println("Result for " + atmelMicroController.getDeviceId() +" - "+atmelMicroController.getDeviceName()+" = " + programmerTaskResult.isSuccessed());
                return false;
            }
            System.out.println("Result for " + atmelMicroController.getDeviceId() + " = " + programmerTaskResult.isSuccessed());
        } catch (InterruptedException e) {
            sotaErrorAppender.error(e);
        } catch (ExecutionException e) {
            sotaErrorAppender.error(e);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    return true;



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
        switch(baseMicroController.getOtaMode())
        {
            case ECHO_INTEGRITY:{
                if(PacketIntegrityCheck(SOTAGlobals.OTA_MODE.ECHO_INTEGRITY,encryptedPacket) == false)
                    return false;
                break;
            }

        }
        return Acknowledgement(stk500Protocol.AuthenticationAnswer(),true);
    }

    public boolean SendFirmwareChunk(byte[] firmwareChunk)
    {
        byte[] encryptedPacket = ArrayUtils.toPrimitive(securityManager.encrypt(stk500Protocol.GetCMD_PROGRAM_FLASH_ISP(firmwareChunk)).getData());

        baseConnection.sendDataToMicroController(WrapWithMessageFormat(encryptedPacket));
        switch(baseMicroController.getOtaMode())
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
        baseConnection.sendDataToMicroController(byteArray);
        byte[] desiredInput  = {0x52,0x53,0x54,0x2b,0x49,0x4e,0x46,0x4f,0x52,0x4d,0x0d,0x0a};
        return Acknowledgement(desiredInput,false);
    }


    public boolean GetSync()
    {
        byte[] encryptedPacket = ArrayUtils.toPrimitive(securityManager.encrypt(stk500Protocol.GetCMD_SIGN_ON()).getData());

        baseConnection.sendDataToMicroController(WrapWithMessageFormat(encryptedPacket));

        switch(baseMicroController.getOtaMode())
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
        switch(baseMicroController.getOtaMode())
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
        switch(baseMicroController.getOtaMode())
        {
            case ECHO_INTEGRITY:{
                if(PacketIntegrityCheck(SOTAGlobals.OTA_MODE.ECHO_INTEGRITY,encryptedPacket) == false)
                    return false;
                break;
            }

        }
        return Acknowledgement(stk500Protocol.GetCM_ENTER_PROGMODE_ISPAnswer(),true);
    }

    public boolean CloseProgramMode()
    {

            byte[] encryptedPacket = ArrayUtils.toPrimitive(securityManager.encrypt(stk500Protocol.GetCMD_LEAVE_PROGMODE_ISP()).getData());

            baseConnection.sendDataToMicroController(WrapWithMessageFormat(encryptedPacket));
        switch(baseMicroController.getOtaMode())
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

    public boolean sendFirmware(byte[] firmware) throws InterruptedException
    {
        Integer topAddress = Integer.valueOf(-2147483648);

        int totalChunkNumber = firmware.length/ baseMicroController.getMaximumFirmwareTransferPacketSize();
        if(firmware.length % baseMicroController.getMaximumFirmwareTransferPacketSize() != 0)
            totalChunkNumber++;
        for(int i=0; i<totalChunkNumber; i++)
        {
            String loadedAddress = Integer.toBinaryString(topAddress);
            Long binaryFormattedAddress = Long.parseLong(loadedAddress, 2);
            int parts = Long.toHexString(binaryFormattedAddress).length()/4;
            byte[] address = new byte[4];
            String[] hexArray = Long.toHexString(binaryFormattedAddress).split("(?<=\\G.{"+parts+"})");

            address[0] = (byte) Integer.parseInt(hexArray[0],16);
            address[1] = (byte) Integer.parseInt(hexArray[1],16);
            address[2] = (byte) Integer.parseInt(hexArray[2],16);
            address[3] = (byte) Integer.parseInt(hexArray[3],16);

            while(!LoadAddress(address)){Thread.sleep(50);}

            byte[] firmwareChunk;

            if(i != (totalChunkNumber-1))
            {
                firmwareChunk = new byte[baseMicroController.getMaximumFirmwareTransferPacketSize()];

            }
            else
            {
                firmwareChunk = new byte[firmware.length% baseMicroController.getMaximumFirmwareTransferPacketSize()];
            }

            for(int index = 0; index < firmwareChunk.length; index++)
            {
                firmwareChunk[index] =  firmware[i*256+index];
            }
            // firmware eklenmeli..
            int counter = 0;

            while (!SendFirmwareChunk(firmwareChunk)){
                Thread.sleep(150);
                counter++;
                if(counter == maximumAllowedTry)
                {
                    return false;
                }

            }
            topAddress += 128;
        }

//        while (!CloseProgramMode()) {
//            Thread.sleep(1000);
//        }

        return true;

    }


    public boolean Acknowledgement(byte[] desiredInput, boolean isEncrypted) {

        int index = 0;
        //todo time and tcp activity logger
        byte[] inputData = new byte[atmelMicroController.getMaximumPacketSize()];

        String desiredPacket = new String(desiredInput);
        long timestart = System.currentTimeMillis();
        while((timestart + baseConnection.getTimeout()) > System.currentTimeMillis()) {
            // System.out.println((timestart + Constants.TIMEOUT) +" - "+System.currentTimeMillis());
            int b;

            inputData = ArrayUtils.toPrimitive(baseConnection.getBytesFromMicroController().getReceivedData());
            if (inputData != null && inputData.length > 0)
            {
            if (!isEncrypted) {
                String str = new String(inputData);
                if (str.indexOf(desiredPacket) >= 0) {
                    return true;
                }
            } else {
                if (inputData.length > 2) {

                    ArrayList<Integer> occurances = findOccurence(inputData, (byte) 0x58);

                    for (int occurenceIndex = 0; occurenceIndex < occurances.size(); occurenceIndex++) {
                        if (isHaveDesiredResponse(inputData, desiredInput, occurances.get(occurenceIndex))) {
                            return true;
                        }
                    }

                    return false;


                }
            }
        }
        }
        return false;
    }


    private boolean isHaveDesiredResponse(byte[] receivedPacket, byte[] desiredInput, int startingIndex)
    {
        if(receivedPacket[startingIndex] == 0x58) {
            int size = (((receivedPacket[startingIndex+1] & 0xff) << 8) | (receivedPacket[startingIndex+2] & 0xff));
            startingIndex += 3;
            byte[] encryptedPortion = null;
            if(size>(receivedPacket.length-startingIndex))
                return false;
            encryptedPortion = new byte[size];
            for (int packetFillerIndex = 0; packetFillerIndex < size; packetFillerIndex++) {
                encryptedPortion[packetFillerIndex] = receivedPacket[startingIndex];
                startingIndex++;
            }


            byte[] receivedData = ArrayUtils.toPrimitive(securityManager.decrypt(encryptedPortion).getData());



            STK500PacketParser stk500PacketParser = new STK500PacketParser(receivedData);
            if (stk500PacketParser.Parse()) {



            if (Collections.indexOfSubList(Arrays.asList(ArrayUtils.toObject(stk500PacketParser.getData())), Arrays.asList(ArrayUtils.toObject(desiredInput))) >= 0) {
                stk500Protocol.IncreaseSequenceNumber();
                return true;
            } else {
                return false;
            }
            }
            else
            {
                return false;
            }
        }
        else
            return false;

    }

    private ArrayList<Integer> findOccurence(byte[] packet, byte packetStart)
    {
        ArrayList<Integer> foundIndex = new ArrayList<>();
        for(int i=0; i<packet.length; i++)
        {
            if(packet[i] == packetStart)
                foundIndex.add(i);
        }
        return foundIndex;

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
