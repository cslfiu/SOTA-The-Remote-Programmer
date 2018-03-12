package programmer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import programmer.device.BaseMicroController;
import programmer.model.ProgrammerTaskResult;

import java.util.concurrent.Callable;

/**
 * Created by Burak on 11/8/17.
 */
public class SOTAFirmwareTransfer implements Callable<ProgrammerTaskResult> {

    private SOTAProtocol sotaProtocol;
    private byte[] firmware;
    private BaseMicroController baseMicroController;
    private Logger resultLogger = LogManager.getLogger(("SOTAResultLogger"));
    private int maximumAllowedTry = 10;

    public SOTAFirmwareTransfer(BaseMicroController baseMicroController)
    {
        this.sotaProtocol = (SOTAProtocol) baseMicroController.getBaseProgrammingProtocol();
        this.firmware = baseMicroController.getFirmwareBytes();
        this.baseMicroController = baseMicroController;
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
            resultLogger.trace(baseMicroController.getDeviceName()+" - Sync Part took: "+ (finishTime - startingTime) + " ms");
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
            resultLogger.trace(baseMicroController.getDeviceName()+" - Enabling Programming Part took: "+ (finishTime - startingTime) + " ms");

            startingTime = System.currentTimeMillis();
            if(sotaProtocol.sendFirmware(firmware)) {
                finishTime = System.currentTimeMillis();
                resultLogger.trace(baseMicroController.getDeviceName()+" - Sending Firmware Part took: "+ (finishTime - startingTime) + " ms");
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
                resultLogger.trace(baseMicroController.getDeviceName()+" - Closing Programming Part took: "+ (finishTime - startingTime) + " ms");

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
