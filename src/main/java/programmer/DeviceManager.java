package programmer;

import programmer.device.BaseMicroController;

import java.util.ArrayList;

/**
 * Created by Burak on 10/23/17.
 */
public class DeviceManager {

    private static DeviceManager self;
    public static DeviceManager getInstance()
    {
        if(self == null)
            self = new DeviceManager();
        return self;
    }

    private ArrayList<BaseMicroController> baseMicroControllers;

    public DeviceManager()
    {
        baseMicroControllers = new ArrayList<>();

    }

    public void addDevice(BaseMicroController baseMicroController)
    {
        baseMicroControllers.add(baseMicroController);
    }

    public ArrayList<BaseMicroController> getBaseMicroControllers() {
        return baseMicroControllers;
    }




}
