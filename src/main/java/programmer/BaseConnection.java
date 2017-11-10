package programmer;

import programmer.device.MicroController;
import programmer.model.DataGetResult;
import programmer.model.DataSendResult;

/**
 * Created by Burak on 10/23/17.
 */
public abstract class BaseConnection {



    protected MicroController parentMicroController;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected int timeout;
    public BaseConnection(MicroController microController){parentMicroController = microController;}

    public MicroController getParentMicroController() {
        return parentMicroController;
    }


    public abstract DataSendResult sendDataToMicroController(byte[] packet);
    public abstract DataGetResult<Byte> getBytesFromMicroController();
    public abstract DataGetResult<String> getStringFromMicroController();


}
