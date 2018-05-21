package programmer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programmer.device.BaseMicroController;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Created by Burak on 10/23/17.
 */
public class STK500Protocol {

    private byte sequenceNumber = 0x00;
    private Random random;
    private Logger wifiLogger = LogManager.getLogger("TCPActivityLogger");
    private BaseMicroController baseMicroController;
    private int dataPacketSize;

    public int getCommandTimeout() {
        return commandTimeout;
    }

    public void setCommandTimeout(int commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    private int commandTimeout;

    enum PACKET_PARSER_STATE
    {
        MESSAGE_START,
        MESSAGE_SIZE,
        TOKEN,
        DATARECEIVING,
        CHECKSUM
    }

    public STK500Protocol(BaseMicroController baseMicroController)
    {
        this.commandTimeout = 500;
        this.dataPacketSize = 256;
        this.baseMicroController = baseMicroController;
    }


    public void IncreaseSequenceNumber()
    {
        if(sequenceNumber == 0xFF)
        {
            sequenceNumber = 0x00;
        }
        sequenceNumber++;

    }

    public byte GetSequenceNumber()
    {
        return sequenceNumber;
    }

    private byte[] WrapWithMessageFormat(byte[] message)
    {
        int packet_size = message.length;
        byte[]packet  = new byte[6+packet_size];
        packet[0] = STK500Commands.MESSAGE_START;
        packet[1] = GetSequenceNumber();
        packet[2] = (byte)(packet_size / 256);
        packet[3] = (byte)(packet_size % 256);
        packet[4] = STK500Commands.TOKEN;
        for(int i = 0; i< message.length; i++)
            packet[5+i] = message[i];
        packet[5+message.length] = Checksum(packet,4+message.length);
        return packet;
    }



    private byte Checksum(byte[] packet, int endIndex)
    {
        byte result = 0x00;
        for(int i=0; i<(endIndex+1); i++)
        {
            result ^= packet[i];
        }
        return result;
    }

    public byte[] GetCMD_SIGN_ONAnswer(){
        byte[] receivingArray = new byte[2];
        receivingArray[0] = STK500Commands.CMD_SIGN_ON;
        receivingArray[1] = STK500Commands.STATUS_CMD_OK;
        return receivingArray;
    }


    public byte[] GetCMD_SIGN_ON()
    {
        byte[] sendingArray = new byte[1];
        sendingArray[0] = STK500Commands.CMD_SIGN_ON;
        return WrapWithMessageFormat(sendingArray);
    }

    public byte[] GetCM_ENTER_PROGMODE_ISPAnswer()
    {
        byte[] receivingArray = new byte[2];
        receivingArray[0] = STK500Commands.CMD_ENTER_PROGMODE_ISP;
        receivingArray[1] = STK500Commands.STATUS_CMD_OK;
        return receivingArray;
    }

    public byte[] GetCMD_ENTER_PROGMODE_ISP()
    {
        byte[] sendingArray = new byte[12];
        sendingArray[0] = STK500Commands.CMD_ENTER_PROGMODE_ISP;
        sendingArray[1] = (byte)200;
        sendingArray[2] = (byte)100;
        sendingArray[3] = (byte)25;
        sendingArray[4] = (byte)32;
        sendingArray[5] = (byte)0;
        sendingArray[6] =(byte) 0x53;
        sendingArray[7] =(byte) 3;
        sendingArray[8] = (byte)0xAC;
        sendingArray[9] =(byte) 0x53;
        sendingArray[10] =(byte) 0;
        sendingArray [11] = (byte)0;
        return WrapWithMessageFormat(sendingArray);
    }

    public byte[] GetCMD_LEAVE_PROGMODE_ISPAnswer()
    {
        byte[]  receivingArray = new byte[2];
        receivingArray[0] = 0x11;
        receivingArray[1] = 0x00;
        return receivingArray;
    }
    public byte[] GetCMD_LEAVE_PROGMODE_ISP()
    {
            byte[]  sendingArray = new byte[3];
            sendingArray[0] = 0x11;
            sendingArray[1] = 0x1;
            sendingArray[2] = 0x1;
            return WrapWithMessageFormat(sendingArray);

    }

    public byte[] GetCMD_LOAD_ADDRESS(byte[] address)
    {
        try {

            if (address.length != 4) {
                throw new Exception("Address byte array must contain 4 bytes.");
            }
            byte[] sendingArray = new byte[5];
            sendingArray[0] = STK500Commands.CMD_LOAD_ADDRESS;
            sendingArray[1] = address[0];
            sendingArray[2] = address[1];
            sendingArray[3] = address[2];
            sendingArray[4] = address[3];
            return WrapWithMessageFormat(sendingArray);
        }
        catch (Exception ex)
        {
            // todo handle exception and time
        }
        return null;
    }

   public byte[] GetCMD_LOAD_ADDRESSAnswer()
   {
       byte[] receivingArray = new byte[2];
       receivingArray[0] = 0x06;
       receivingArray[1] = 0x00;
       return receivingArray;
   }

    public byte[] AuthenticateFirstPhaseRequest()
    {
        byte[] authenticationPacket = new byte[9];
        byte[] intByteArray = ByteBuffer.allocate(4).putInt(baseMicroController.getAuthenticationNumber()).array();
        authenticationPacket[0] = 0x67;
        authenticationPacket[1] = intByteArray[0];
        authenticationPacket[2] = intByteArray[1];
        authenticationPacket[3] = intByteArray[2];
        authenticationPacket[4] = intByteArray[3];

        wifiLogger.info("Beklenen normal num = "+bytesToHex(intByteArray));
        authenticationPacket[5] =  baseMicroController.getAuthenticationPreSharedToken()[0];
        authenticationPacket[6] =  baseMicroController.getAuthenticationPreSharedToken()[1];
        authenticationPacket[7] =  baseMicroController.getAuthenticationPreSharedToken()[2];
        authenticationPacket[8] =  baseMicroController.getAuthenticationPreSharedToken()[3];

        return WrapWithMessageFormat(authenticationPacket);
    }

    public byte[] AuthenticateSecondPhaseRequest(byte[] receivedRandomNumber)
    {

        int randomReceivedNumber = java.nio.ByteBuffer.wrap(receivedRandomNumber).getInt();
        int secretKey = java.nio.ByteBuffer.wrap(baseMicroController.getGeneratedRandomNumber()).getInt();

        randomReceivedNumber += secretKey;



        byte[] authenticationPacket = new byte[5];
        byte[] intByteArray = ByteBuffer.allocate(4).putInt(randomReceivedNumber).array();
        authenticationPacket[0] = 0x68;

        authenticationPacket[1] = intByteArray[0];
        authenticationPacket[2] = intByteArray[1];
        authenticationPacket[3] = intByteArray[2];
        authenticationPacket[4] = intByteArray[3];

        wifiLogger.info("Beklenen normal num = "+bytesToHex(intByteArray));
        authenticationPacket[5] =  baseMicroController.getAuthenticationPreSharedToken()[0];
        authenticationPacket[6] =  baseMicroController.getAuthenticationPreSharedToken()[1];
        authenticationPacket[7] =  baseMicroController.getAuthenticationPreSharedToken()[2];
        authenticationPacket[8] =  baseMicroController.getAuthenticationPreSharedToken()[3];

        return WrapWithMessageFormat(authenticationPacket);
    }

    public byte[] AuthenticationAnswerFirstPhase()
    {
        byte[] authenticationResponsePacket = new byte[5];
        authenticationResponsePacket[0] = 0x00;
        int authNum = baseMicroController.getAuthenticationNumber();
        int secretKey = java.nio.ByteBuffer.wrap(baseMicroController.getAuthenticationSecretKey()).getInt();

        authNum += secretKey;

//        byte[] intByteArray = ByteBuffer.allocate(4).putInt(baseMicroController.getAuthenticationNumber()).array();
//        byte lastByte = intByteArray[3];
//        authNum += (int) lastByte<<24;


        byte[]  incrementedNumber = ByteBuffer.allocate(4).putInt(authNum).array();


        authenticationResponsePacket[1] = incrementedNumber[0];
        authenticationResponsePacket[2] = incrementedNumber[1];
        authenticationResponsePacket[3] = incrementedNumber[2];
        authenticationResponsePacket[4] = incrementedNumber[3];




        wifiLogger.info("Beklenen inc num = "+bytesToHex(incrementedNumber));


        return authenticationResponsePacket;

    }

    public byte[] AuthenticationAnswerSecondPhase()
    {
        byte[] authenticationResponsePacket = new byte[1];
        authenticationResponsePacket[0] = 0x00;
        return authenticationResponsePacket;

    }


    public byte[] GetCMD_PROGRAM_FLASH_ISP(byte[] firmwareChuck)
    {
        int size = firmwareChuck.length;
        byte[] flash_program = new byte[size+10];
        flash_program[0] = 0x13;
        flash_program[1] = (byte)((size) >>8);
        flash_program[2] = (byte)((size) & 0xff);
        flash_program[3] = (byte)0xc1;
        flash_program[4] = 0x0a;
        flash_program[5] = 0x40;
        flash_program[6] = 0x4c;
        flash_program[7] = 0x20;
        flash_program[8] = 0x00;
        flash_program[9] = 0x00;

        for(int i=0;i<firmwareChuck.length; i++)
        {
            flash_program[i+10] = firmwareChuck[i];
        }

        return WrapWithMessageFormat(flash_program);

    }


    public byte[] GetCMD_PROGRAM_FLASH_ISPAnswer()
    {
        byte[] receivingArray = new byte[2];
        receivingArray[0] = 0x13;
        receivingArray[1] = 0x00;
        return receivingArray;
    }


    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private  String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b) +" ");
        }
        return builder.toString();
    }


}


