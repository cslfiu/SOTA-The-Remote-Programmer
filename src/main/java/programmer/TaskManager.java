package programmer;

import programmer.model.ProgrammerTaskResult;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Burak on 11/9/17.
 */
public class TaskManager {
    private static TaskManager ourInstance = new TaskManager();

    public static TaskManager getInstance() {
        return ourInstance;
    }

    private ExecutorService executorService;

    private TaskManager() {
       executorService= Executors.newFixedThreadPool(SOTAGlobals.getInstance().getMaxAllowedSimultaniousProgrammingTask());

    }

    public Future<ProgrammerTaskResult> addTask(Callable<ProgrammerTaskResult> programmingTask)
    {
        return executorService.submit(programmingTask);
    }
}
