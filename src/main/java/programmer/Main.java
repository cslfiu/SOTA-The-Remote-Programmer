package programmer;

import programmer.device.AtmelMicroController;

import java.util.Random;


/**
 * Created by Burak on 10/23/17.
 */
public class Main {
//    static Logger LOGGER = LogManager.getLogger(Main.class.getName());

    byte[] authenticationPreSharedToken = new byte[] {0x53, (byte)0xef, 0x34,0x23};
    byte[] authenticationSecretKey = new byte[] {0x45, (byte)0xaf, 0x32,0x21};


    Random random = new Random();
    private void getMenu()
    {
        System.out.println("### SOTA Menu ###");
        System.out.println("1 - Device Add");
        System.out.println("2 - List Devices");
        System.out.println("3 - Send Firmware");


    }


    public void addDevice(String deviceName,String ipAdress, SOTAGlobals.OTA_MODE ota_mode, SOTAGlobals.AUTH_MODE authMode, String uploadedFilePath)
    {
//        AtmelMicroController atmelMicroController = new AtmelMicroController(SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY, SOTAGlobals.AUTH_MODE.ON, Main.class.getResource("/Blink.ino.hex").getPath());
        AtmelMicroController atmelMicroController = new AtmelMicroController(ota_mode, authMode, Main.class.getResource(uploadedFilePath).getPath());
        atmelMicroController.setAuthenticated(false);
        atmelMicroController.setDeviceName(deviceName);
        atmelMicroController.setAuthenticationNumber(random.nextInt(16777215));
        atmelMicroController.setAuthenticationSecretKey(authenticationSecretKey);
        atmelMicroController.setAuthenticationPreSharedToken(authenticationPreSharedToken);
        WiFiConnection wiFiConnection = new WiFiConnection(atmelMicroController);
        wiFiConnection.setIPAdress("");
        atmelMicroController.setBaseConnection(wiFiConnection);
        atmelMicroController.setBaseProgrammingProtocol(new SOTAProtocol(atmelMicroController));
        ((WiFiConnection)atmelMicroController.getBaseConnection()).setIPAdress(ipAdress);
        DeviceManager.getInstance().addDevice(atmelMicroController);

    }


    public static void main(String[] args) {
        long programmingStartingTime = System.currentTimeMillis();
        Main main = new Main();
//        main.addDevice("A421","10.109.137.121",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A422","10.109.79.221",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A423","10.109.60.29",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
        main.addDevice("A424","10.109.247.39",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A425","10.109.140.14",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A426","10.109.212.242",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A427","10.109.189.0",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A428","10.109.217.208",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A429","10.109.108.209",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A431","10.109.91.61",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A432","10.109.9.62",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");
//        main.addDevice("A433","10.109.92.116",SOTAGlobals.OTA_MODE.FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,SOTAGlobals.AUTH_MODE.ON,"/Blink.ino.hex");

        TCPServer tcpServer = new TCPServer(programmingStartingTime);
        tcpServer.initializeServer();





    }
}
