package programmer;

import programmer.device.BaseMicroController;
import programmer.model.DataGetResult;
import programmer.model.DataSendResult;

/**
 * Created by Burak on 10/23/17.
 */
public abstract class BaseConnection {



    protected BaseMicroController parentBaseMicroController;

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected int timeout;
    public BaseConnection(BaseMicroController baseMicroController){
        parentBaseMicroController = baseMicroController;}

    public BaseMicroController getParentBaseMicroController() {
        return parentBaseMicroController;
    }


    public abstract DataSendResult sendDataToMicroController(byte[] packet);
    public abstract DataGetResult<Byte> getBytesFromMicroController();
    public abstract DataGetResult<String> getStringFromMicroController();


}
