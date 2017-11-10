package programmer;

/**
 * Created by Burak on 10/23/17.
 */
public class DataManager {
    private static DataManager ourInstance = new DataManager();


    public static DataManager getInstance() {
        return ourInstance;
    }

    private DataManager() {
    }
}
