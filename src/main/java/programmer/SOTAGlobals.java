package programmer;

/**
 * Created by Burak on 10/23/17.
 */
public class SOTAGlobals {
    private static SOTAGlobals ourInstance = new SOTAGlobals();

    public static SOTAGlobals getInstance() {
        return ourInstance;
    }

    public int getMaxAllowedSimultaniousProgrammingTask() {
        return maxAllowedSimultaniousProgrammingTask;
    }

    public void setMaxAllowedSimultaniousProgrammingTask(int maxAllowedSimultaniousProgrammingTask) {
        this.maxAllowedSimultaniousProgrammingTask = maxAllowedSimultaniousProgrammingTask;
    }

    private int maxAllowedSimultaniousProgrammingTask;

    public enum AUTH_MODE {
        ON,
        OFF
    }

    private String serverIpAdress;

    public String getServerIpAdress() {
        return serverIpAdress;
    }

    public void setServerIpAdress(String serverIpAdress) {
        this.serverIpAdress = serverIpAdress;
    }

    public enum OTA_MODE {
        BASIC_CONFIDENTIALITY,
        FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY,
        ECHO_INTEGRITY,
        FULL_CONFIDENTIALITY_WITH_DEFAULT_INTEGRITY_DOUBLE_AUTH_CHECK,
        CONSERVATIVE_ECHO_INTEGRITY,
        CHECKSUM_CHECK
    }

    public enum OTA_RESULT
    {
        SUCCESS,
        FAIL
    }

    private SOTAGlobals() {
        maxAllowedSimultaniousProgrammingTask = 20;
    }
}
