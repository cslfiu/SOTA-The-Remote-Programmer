package programmer;

import programmer.device.MicroController;
import programmer.model.ProgrammerTaskResult;

import java.util.concurrent.Callable;

/**
 * Created by Burak on 11/8/17.
 */
public class SOTAFirmwareTransfer implements Callable<ProgrammerTaskResult> {

    private SOTAProtocol sotaProtocol;
    private byte[] firmware;
    public SOTAFirmwareTransfer(MicroController microController)
    {
        this.sotaProtocol = (SOTAProtocol)microController.getBaseProgrammingProtocol();
        this.firmware = microController.getFirmwareBytes();
    }





    @Override
    public ProgrammerTaskResult call() throws Exception {
        ProgrammerTaskResult programmerTaskResult = new ProgrammerTaskResult();
        boolean isChainBroke = false;
        while(true)
        {

            while(!sotaProtocol.GetSync())
            {
                Thread.sleep(1000);
            }

            while(!sotaProtocol.EnableProgramMode())
            {
                Thread.sleep(1000);
            }

            if(sotaProtocol.sendFirmware(firmware)) {

            }

            break;
        }

        programmerTaskResult.setSuccessed(true);
        return programmerTaskResult;

    }
}
