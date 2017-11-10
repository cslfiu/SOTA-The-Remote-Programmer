package programmer;

import programmer.device.MicroController;
import programmer.security.BaseSecurityManager;

/**
 * Created by Burak on 10/26/17.
 */
public abstract class BaseProgrammingProtocol {

    protected MicroController microController;

    public BaseSecurityManager getSecurityManager() {
        return securityManager;
    }

    public void setSecurityManager(BaseSecurityManager securityManager) {
        this.securityManager = securityManager;
    }
    protected BaseSecurityManager securityManager;


    public MicroController getMicroController() {
        return microController;
    }

    public BaseProgrammingProtocol(MicroController microController)
    {
        this.microController = microController;
    }

    public abstract  void startFirmwareUploading(byte[] firmware);


}
