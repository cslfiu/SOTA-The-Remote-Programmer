package programmer.device;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programmer.SOTAGlobals.AUTH_MODE;
import programmer.SOTAGlobals.OTA_MODE;
import programmer.model.Line;
import programmer.model.LoadFirmwareResult;
import programmer.model.ProgrammingResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Burak on 10/23/17.
 */
public class AtmelMicroController extends BaseMicroController {

    private boolean isAuthenticated;

    private Logger sotaErrorAppender = LogManager.getLogger("SOTAErrorLogger");
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public AtmelMicroController(OTA_MODE ota_mode, AUTH_MODE authMode, String firmwarePath)
    {
        super(288,256);

        deviceId = UUID.randomUUID();
        this.authMode = authMode;
        this.otaMode = ota_mode;
        this.firmwarePath = firmwarePath;


    }




    @Override
    public ProgrammingResult sendFirmware() {
        ProgrammingResult programmingResult = new ProgrammingResult();

        try {
            long startingTime = System.currentTimeMillis();
            LoadFirmwareResult loadFirmwareResult = loadFirmware();
            firmwareBytes = loadFirmwareResult.getData();
            if (loadFirmwareResult.isLoaded()) {
                if(baseProgrammingProtocol.startFirmwareUploading(loadFirmwareResult.getData()))
                {
                    programmingResult.setStatus(true);
                }
                else
                {
                    programmingResult.setStatus(false);
                }
            }

            long finish = System.currentTimeMillis();

            programmingResult.setDuration(finish - startingTime);
            programmingResult.setStatus(true);

        }
        catch (Exception ex)
        {
            programmingResult.setStatus(false);
            programmingResult.setDuration(-1);
            sotaErrorAppender.error("Error occured during sending firmware : ",ex);
        }
        return programmingResult;
    }

    @Override
    public LoadFirmwareResult loadFirmware() {
        long startingTime = System.currentTimeMillis();
        LoadFirmwareResult loadFirmwareResult = new LoadFirmwareResult();

        ArrayList<Line> sketchFileLine = new ArrayList<>();
        StringBuilder  data = new StringBuilder();
        // todo timer is needed to be added.
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(firmwarePath)));
            String text  = null;
            while ((text = bufferedReader.readLine()) != null)
            {
                Line line = new Line(text);
                sketchFileLine.add(line);
                data.append(line.getData());

            }
            char[] firmwareCharArray = data.toString().toCharArray();
            byte[] firmwareArray = new byte[firmwareCharArray.length];
            for(int i=0; i<firmwareCharArray.length; i++)
            {
                firmwareArray[i] = (byte) firmwareCharArray[i];
            }
            loadFirmwareResult.setData(firmwareArray);
            long finishingTime = System.currentTimeMillis();
            loadFirmwareResult.setDuration(finishingTime - startingTime);
            loadFirmwareResult.setLoaded(true);
        }
        catch (Exception ex)
        {
            loadFirmwareResult.setLoaded(false);
            loadFirmwareResult.setDuration(-1);
            sotaErrorAppender.error("Loading Firmware Failure: ",ex);

        }
        firmwareBytes = loadFirmwareResult.getData();
        return loadFirmwareResult;
    }
}
