package programmer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programmer.device.MicroController;
import programmer.model.DataGetResult;
import programmer.model.DataSendResult;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by Burak on 10/23/17.
 */
public class WiFiConnection extends BaseConnection {

    private Logger wifiLogger = LogManager.getLogger("TCPActivityLogger");
    private Logger sotaErrorAppender = LogManager.getLogger("SOTAErrorLogger");
    private Logger sotaLogger = LogManager.getRootLogger();




    private String IPAdress;
    private int port;

    private BufferedInputStream bufferedInputStream;
    private DataOutputStream dataOutputStream;

    public WiFiConnection(MicroController microController)
    {
        super(microController);
        timeout = 4000;

    }

    public String getIPAdress() {
        return IPAdress;
    }

    public void setIPAdress(String IPAdress) {
        this.IPAdress = IPAdress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }



    public BufferedInputStream getBufferedInputStream() {
        return bufferedInputStream;
    }

    public void setBufferedInputStream(BufferedInputStream bufferedInputStream) {
        this.bufferedInputStream = bufferedInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public void setDataOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }




    @Override
    public DataSendResult sendDataToMicroController(byte[] packet) {
        DataSendResult dataSendResult = new DataSendResult();
        // todo timer should be added.
        try{
            dataOutputStream.write(packet);
            String packetString = bytesToHex(packet);
            if(packetString.length()>0)
            wifiLogger.info("Sent packet: " + packetString);


        }
        catch (Exception exception)
        {
            wifiLogger.error("Exception occured while sending data to target microcontroller.",exception);
        }
        return dataSendResult;
    }

    @Override
    public DataGetResult<Byte> getBytesFromMicroController() {
        try {
            DataGetResult<Byte> dataGetResult = new DataGetResult<>();
            if(bufferedInputStream.available() >0) {
                byte[] receivedData = new byte[bufferedInputStream.available()];
                // todo timer should be added.

                bufferedInputStream.read(receivedData, 0, bufferedInputStream.available());
                dataGetResult.setReceivedData(ArrayUtils.toObject(receivedData));
                String receivedPacket = bytesToHex(receivedData);
                if (receivedPacket.length() > 0)
                    wifiLogger.info("Receiving packet: " + receivedPacket);
            }
            return dataGetResult;
        } catch (IOException e) {
            wifiLogger.error("Exception occured while receiving data from target microcontroller.",e);
        }
return null;

    }

    @Override
    public DataGetResult<String> getStringFromMicroController() {
        DataGetResult<String>  dataGetResult = new DataGetResult<>();
        // todo timer should be added.

        try {
            String[] stringArray = new String[1];
            stringArray[0] = IOUtils.toString(bufferedInputStream, StandardCharsets.UTF_8);
            dataGetResult.setReceivedData(stringArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataGetResult;
    }


    private  String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b) +" ");
        }
        return builder.toString();
    }
}
