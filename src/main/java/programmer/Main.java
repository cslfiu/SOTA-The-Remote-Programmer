package programmer;

import programmer.device.AtmelMicroController;

import java.util.Random;


/**
 * Created by Burak on 10/23/17.
 */
public class Main {
//    static Logger LOGGER = LogManager.getLogger(Main.class.getName());


    private void getMenu()
    {
        System.out.println("### SOTA Menu ###");
        System.out.println("1 - Device Add");
        System.out.println("2 - List Devices");
        System.out.println("3 - Send Firmware");


    }

    public static void main(String[] args) {



        AtmelMicroController atmelMicroController = new AtmelMicroController(SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY, SOTAGlobals.AUTH_MODE.ON, Main.class.getResource("/Blink.ino.hex").getPath());
        atmelMicroController.setAuthenticated(false);
        byte[] authenticationToken = new byte[] {0x53, (byte)0xef, 0x34,0x23};
        Random random = new Random();
        int authenticationNumber = random.nextInt(16777215);
        atmelMicroController.setAuthenticationNumber(authenticationNumber);
        atmelMicroController.setAuthenticationToken(authenticationToken);
        WiFiConnection wiFiConnection = new WiFiConnection(atmelMicroController);

        wiFiConnection.setIPAdress("");

        atmelMicroController.setBaseConnection(wiFiConnection);
        atmelMicroController.setBaseProgrammingProtocol(new SOTAProtocol(atmelMicroController));
        ((WiFiConnection)atmelMicroController.getBaseConnection()).setIPAdress("10.109.74.18");
        DeviceManager.getInstance().addDevice(atmelMicroController);
        TCPServer tcpServer = new TCPServer();
        tcpServer.initializeServer();





    }
}
