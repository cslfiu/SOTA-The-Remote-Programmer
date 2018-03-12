package programmer;

import programmer.device.BaseMicroController;
import programmer.security.BaseSecurityManager;

/**
 * Created by Burak on 10/26/17.
 */
public abstract class BaseProgrammingProtocol {

    protected BaseMicroController baseMicroController;

    public BaseSecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(BaseSecurityManager securityManager) {
        this.securityManager = securityManager;
    }
    protected BaseSecurityManager securityManager;


    public BaseMicroController getBaseMicroController() {
        return baseMicroController;
    }

    public BaseProgrammingProtocol(BaseMicroController baseMicroController)
    {
        this.baseMicroController = baseMicroController;
    }

    public abstract  boolean startFirmwareUploading(byte[] firmware);


}
