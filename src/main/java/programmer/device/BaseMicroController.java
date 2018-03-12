package programmer.device;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programmer.BaseConnection;
import programmer.BaseProgrammingProtocol;
import programmer.SOTAGlobals.AUTH_MODE;
import programmer.SOTAGlobals.OTA_MODE;
import programmer.model.LoadFirmwareResult;
import programmer.model.ProgrammingResult;

import java.util.UUID;

/**
 * Created by Burak on 10/23/17.
 */
public abstract class BaseMicroController {
    protected UUID deviceId;
    protected String firmwarePath;
    protected AUTH_MODE authMode;
    protected OTA_MODE otaMode;
    protected boolean hasActiveConnection;
    protected BaseConnection baseConnection;
    protected BaseProgrammingProtocol baseProgrammingProtocol;
    protected byte[] authenticationToken;
    protected int authenticationNumber;
    protected int maximumFirmwareTransferPacketSize;
    protected String deviceName;



    protected Logger resultLogger = LogManager.getLogger(("SOTAResultLogger"));

    public byte[] getFirmwareBytes() {
        return firmwareBytes;
    }

    public void setFirmwareBytes(byte[] firmwareBytes) {
        this.firmwareBytes = firmwareBytes;
    }

    protected byte[] firmwareBytes;

    public int getMaximumFirmwareTransferPacketSize() {
        return maximumFirmwareTransferPacketSize;
    }

    public void setMaximumFirmwareTransferPacketSize(int maximumFirmwareTransferPacketSize) {
        this.maximumFirmwareTransferPacketSize = maximumFirmwareTransferPacketSize;
    }

    public int getAuthenticationNumber() {
        return authenticationNumber;
    }

    public void setAuthenticationNumber(int authenticationNumber) {
        this.authenticationNumber = authenticationNumber;
    }

    public byte[] getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(byte[] authenticationToken) {
        this.authenticationToken = authenticationToken;
    }



    public BaseProgrammingProtocol getBaseProgrammingProtocol() {
        return baseProgrammingProtocol;
    }

    public void setBaseProgrammingProtocol(BaseProgrammingProtocol baseProgrammingProtocol) {
        this.baseProgrammingProtocol = baseProgrammingProtocol;
    }

    public int getMaximumPacketSize() {
        return maximumPacketSize;
    }

    public void setMaximumPacketSize(int maximumPacketSize) {
        this.maximumPacketSize = maximumPacketSize;
    }

    protected int maximumPacketSize;

    public BaseConnection getBaseConnection() {
        return baseConnection;
    }

    public void setBaseConnection(BaseConnection baseConnection) {
        this.baseConnection = baseConnection;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isHasActiveConnection() {return hasActiveConnection;}
    public void setHasActiveConnection(boolean hasActiveConnection) {this.hasActiveConnection = hasActiveConnection;}
    public abstract ProgrammingResult sendFirmware();
    public abstract LoadFirmwareResult loadFirmware();
    public String getFirmwarePath(){return firmwarePath;}
    public AUTH_MODE getAuthMode(){return authMode;}
    public OTA_MODE getOtaMode(){return otaMode;}
    public String getDeviceId(){return deviceId.toString();}

    public BaseMicroController(int maximumPacketSize, int maximumFirmwareTransferPacketSize)
    {
        this.maximumPacketSize = maximumPacketSize;
        this.authenticationToken = new byte[4];
        this.maximumFirmwareTransferPacketSize = maximumFirmwareTransferPacketSize;


    }


}
