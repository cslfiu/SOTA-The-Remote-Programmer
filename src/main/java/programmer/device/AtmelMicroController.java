package programmer.device;

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
public class AtmelMicroController extends MicroController {

    private boolean isAuthenticated;

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
        LoadFirmwareResult loadFirmwareResult = loadFirmware();
        if(loadFirmwareResult.isLoaded())
        {
            baseProgrammingProtocol.startFirmwareUploading(loadFirmwareResult.getData());
        }



        return null;
    }

    @Override
    public LoadFirmwareResult loadFirmware() {
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
            loadFirmwareResult.setLoaded(true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            loadFirmwareResult.setLoaded(false);
        }
        firmwareBytes = loadFirmwareResult.getData();
        return loadFirmwareResult;
    }
}
