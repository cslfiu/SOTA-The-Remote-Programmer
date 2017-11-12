package programmer;

/**
 * Created by Burak on 11/8/17.
 */
public class STK500PacketParser
{

    private int size;
    private byte[] data;
    private byte checksum;
    private byte sequenceNumber;
    private byte[] receivedPacket;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getChecksum() {
        return checksum;
    }

    public void setChecksum(byte checksum) {
        this.checksum = checksum;
    }

    public byte getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(byte sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public byte[] getReceivedPacket() {
        return receivedPacket;
    }

    public void setReceivedPacket(byte[] receivedPacket) {
        this.receivedPacket = receivedPacket;
    }

    public STK500PacketParser(byte[] receivedPacket)
    {
        this.receivedPacket = receivedPacket;
    }

    public boolean Parse()
    {
        for(int i=0; i< receivedPacket.length; i++)
        {
            if(receivedPacket[i] == STK500Commands.MESSAGE_START)
            {
                checksum = (byte) (checksum ^ 0);
                int a = i;
                sequenceNumber = receivedPacket[a+1];
                checksum ^= sequenceNumber;
                size  = (receivedPacket[a+2] << 8) | ( receivedPacket[a+3]);
                data = new byte[size];
                checksum ^= receivedPacket[a+2];
                checksum ^= receivedPacket[a+3];

                if(receivedPacket[a+4] != 0x0e)
                {
return  false;
                }

                a = a+4;
                for(int b= 0; b<size; b++)
                {
                    a++;
                    data[b] = receivedPacket[a];
                    checksum ^= receivedPacket[a];
                }
                a++;
                checksum = receivedPacket[a];

                if(checksum == receivedPacket[a])
                {


                    return true;
                }
                else
                {
return false;
                }


            }
        }
        return false;
    }

}
