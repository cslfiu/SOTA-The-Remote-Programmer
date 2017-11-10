package programmer;

import programmer.device.MicroController;

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

    private ArrayList<MicroController> microControllers;

    public DeviceManager()
    {
        microControllers = new ArrayList<>();

    }

    public void addDevice(MicroController microController)
    {
        microControllers.add(microController);
    }

    public ArrayList<MicroController> getMicroControllers() {
        return microControllers;
    }




}
