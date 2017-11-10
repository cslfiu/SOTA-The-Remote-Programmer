package programmer.model;

/**
 * Created by Burak on 10/25/17.
 */
public class EncryptedData <T> {

    private double duration;
    private T[] data;

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public T[] getData() {
        return data;
    }

    public void setData(T[] data) {
        this.data = data;
    }
}
