package programmer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programmer.device.MicroController;
import programmer.model.ProgrammingResult;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Burak on 10/23/17.
 */
public class TCPServer {

    private DeviceManager deviceManager;
    private ServerSocket socket;
    private Timer timer;
    private TimerTask timerTask;
    private AtomicBoolean isAnotherTaskRunning = new AtomicBoolean(false);
    private AtomicBoolean isProgrammingTaskFinished = new AtomicBoolean(false);
    private volatile boolean isActiveOperationOngoing = false;
    private int finishedDevice=0;
    private long programStartingTime;

    private Logger sotaErrorAppender = LogManager.getLogger("SOTAErrorLogger");
    private Logger resultLogger = LogManager.getLogger(("SOTAResultLogger"));


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    private long startTime;



    /*
    *   Wh
    *
    */

    public  TCPServer(long programStartingTime) {
        this.programStartingTime = programStartingTime;
        deviceManager = DeviceManager.getInstance();

    }


    public void initializeServer() {
        try {
            socket = new ServerSocket(5867);
            SOTAGlobals.getInstance().setServerIpAdress(socket.getLocalSocketAddress().toString().replace("/",""));
            startServer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private void startServer() throws IOException {
while(true) {

    Socket clientConnection = socket.accept();
    if (clientConnection.isConnected()) {

        String remoteDeviceIpAdress = clientConnection.getRemoteSocketAddress().toString().replace("/", "").split(":")[0];
        for (int i = 0; i < deviceManager.getMicroControllers().size(); i++) {
            MicroController microController = deviceManager.getMicroControllers().get(i);
            if (microController.getBaseConnection() instanceof WiFiConnection) {
                if (remoteDeviceIpAdress.compareTo(((WiFiConnection) microController.getBaseConnection()).getIPAdress()) == 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println(" Device IP: " + remoteDeviceIpAdress);
                                // todo I will might add extra checking mechnaism in here. e.g. device UUID check.
                                WiFiConnection wiFiConnection = (WiFiConnection) microController.getBaseConnection();
                                wiFiConnection.setBufferedInputStream(new BufferedInputStream(clientConnection.getInputStream()));
                                wiFiConnection.setDataOutputStream(new DataOutputStream(clientConnection.getOutputStream()));
                                microController.setHasActiveConnection(true);
                                ProgrammingResult programmingResult =  microController.sendFirmware();
                                if(programmingResult.isStatus() == true) {


                                    resultLogger.trace(microController.getDeviceName() + " - OTA  took: " + (programmingResult.getDuration() + " ms"));
                                    deviceManager.getMicroControllers().remove(microController);
                                    finishedDevice++;
                                }
                                if(  deviceManager.getMicroControllers().size() == 0)
                                {
                                    resultLogger.trace(" Programming took: "+ (programStartingTime-System.currentTimeMillis()) + " ms");


                                    return;
                                }

                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                    }).start();

                } else {

                }

            }
        }
    }

}

//            BufferedInputStream bufferedInputStream = new BufferedInputStream((clientConnection.getInputStream()));
//            DataOutputStream outToClient = new DataOutputStream((clientConnection.getOutputStream()));
//            ArduinoBoard arduinoBoard = new ArduinoBoard(bufferedInputStream,outToClient);
//            Scanner sc = new Scanner(System.in);
//            String sentence;
//            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
//            startTime = System.currentTimeMillis();
//            timer = new Timer();
//            scheduleTaskAgain(arduinoBoard);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }



        //    }
//    public AtomicBoolean getIsProgrammingTaskFinished() {
//        return isProgrammingTaskFinished;
//    }
//
//    private void startFirmwareUpload(ArduinoBoard arduinoBoard,String filePath)
//    {
//        arduinoBoard.startSketchUpload(filePath,arduinoBoard,this);
//        System.out.println("Firmware Upload Started");
//        timerTask.cancel();
//        timer.cancel();
//        timer.purge();
//        timer = null;
//
//    }
//    public void scheduleTaskAgain(ArduinoBoard arduinoBoard) {
//
//        if(timer == null)
//            timer = new Timer();
//        timerTask = new TimerTask() {
//            int trialNumber = 0;
//            @Override
//            public void run() {
//                if (!isAnotherTaskRunning.get()) {
//                    isAnotherTaskRunning.set(true);
//                    if(!isProgrammingTaskFinished.get())
//                    {
//                        if (!isActiveOperationOngoing) {
//                            isActiveOperationOngoing = true;
//                            try {
//                                arduinoBoard.sendRebootCMD();
//                                System.out.println("Sending RBT command #" + trialNumber);
//                                if (arduinoBoard.getACKTCP() == true) {
//                                    //System.out.println("We got ACK");
//                                    startFirmwareUpload(arduinoBoard, "/Users/Burak/Desktop/Blink.ino.hex");
//
//
//                                }
//                                else
//                                    System.out.println("We missed bootloader time interval.");
//                            }
//                            catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            trialNumber++;
//                            isActiveOperationOngoing = false;
//                        }
//                        isAnotherTaskRunning.set(false);
//                    }
//
//                }
//
//
//            }
//        };
//        timer.schedule(timerTask,0,2000);
//    }
//    }
}

