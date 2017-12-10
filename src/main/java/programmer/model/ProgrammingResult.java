package programmer.model;

/**
 * Created by Burak on 10/23/17.
 */
public class ProgrammingResult {
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private long duration;
    private boolean status;
}
