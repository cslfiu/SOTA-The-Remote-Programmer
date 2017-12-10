package programmer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programmer.device.MicroController;
import programmer.model.ProgrammerTaskResult;

import java.util.concurrent.Callable;

/**
 * Created by Burak on 11/8/17.
 */
public class SOTAFirmwareTransfer implements Callable<ProgrammerTaskResult> {

    private SOTAProtocol sotaProtocol;
    private byte[] firmware;
    private MicroController microController;
    private Logger resultLogger = LogManager.getLogger(("SOTAResultLogger"));
    private int maximumAllowedTry = 10;

    public SOTAFirmwareTransfer(MicroController microController)
    {
        this.sotaProtocol = (SOTAProtocol)microController.getBaseProgrammingProtocol();
        this.firmware = microController.getFirmwareBytes();
        this.microController = microController;
    }





    @Override
    public ProgrammerTaskResult call() throws Exception {
        ProgrammerTaskResult programmerTaskResult = new ProgrammerTaskResult();
        boolean isChainBroke = false;
        long startingTime;
        long finishTime;

//        sotaProtocol.sendFirmware(firmware);
        while(true)
        {
            startingTime = System.currentTimeMillis();
            int counter = 0;
            while(!sotaProtocol.GetSync())
            {

                Thread.sleep(100);
                counter++;
                if(counter == maximumAllowedTry) {
                    isChainBroke = true;
                    programmerTaskResult.setSuccessed(false);
                    return programmerTaskResult;
                }
            }
            finishTime = System.currentTimeMillis();
            resultLogger.trace(microController.getDeviceName()+" - Sync Part took: "+ (finishTime - startingTime) + " ms");
            startingTime = System.currentTimeMillis();
            counter = 0;
            while(!sotaProtocol.EnableProgramMode())
            {
                Thread.sleep(100);
                counter++;
                if(counter == maximumAllowedTry) {
                    isChainBroke = true;
                    programmerTaskResult.setSuccessed(false);
                    return programmerTaskResult;
                }
            }
            finishTime = System.currentTimeMillis();
            resultLogger.trace(microController.getDeviceName()+" - Enabling Programming Part took: "+ (finishTime - startingTime) + " ms");

            startingTime = System.currentTimeMillis();
            if(sotaProtocol.sendFirmware(firmware)) {
                finishTime = System.currentTimeMillis();
                resultLogger.trace(microController.getDeviceName()+" - Sending Firmware Part took: "+ (finishTime - startingTime) + " ms");
                startingTime = System.currentTimeMillis();
                counter = 0;
                while(!sotaProtocol.CloseProgramMode()){Thread.sleep(100);
                counter++;
                    if(counter == maximumAllowedTry) {
                        isChainBroke = true;
                        programmerTaskResult.setSuccessed(false);
                        return programmerTaskResult;
                    }}
                finishTime = System.currentTimeMillis();
                resultLogger.trace(microController.getDeviceName()+" - Closing Programming Part took: "+ (finishTime - startingTime) + " ms");

                programmerTaskResult.setSuccessed(true);
            }
            else
            {
                programmerTaskResult.setSuccessed(false);
                return programmerTaskResult;
            }

            break;
        }


        return programmerTaskResult;

    }
}
