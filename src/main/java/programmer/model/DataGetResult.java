package programmer.model;

/**
 * Created by Burak on 10/24/17.
 */
public class DataGetResult<T> {

    public boolean isSuccessfull() {
        return isSuccessfull;
    }

    public void setSuccessfull(boolean successfull) {
        isSuccessfull = successfull;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public T[] getReceivedData() {
        return receivedData;
    }

    public void setReceivedData(T[] receivedData) {
        this.receivedData = receivedData;
    }

    private boolean isSuccessfull;
    private double period;
    private T[] receivedData;
}
