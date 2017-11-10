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
        this.sotaProtocol = sotaProtocol;
        this.firmware = firmware;
    }





    @Override
    public ProgrammerTaskResult call() throws Exception {
        ProgrammerTaskResult programmerTaskResult = new ProgrammerTaskResult();
        boolean isChainBroke = false;
        while(true)
        {
            sotaProtocol.startAuthenticationTask();

            while(sotaProtocol.GetSync())
            {
            }

            while(sotaProtocol.EnableProgramMode())
            {
            }

            sotaProtocol.sendFirmware(firmware);
            break;
        }

        programmerTaskResult.setSuccessed(true);
        return programmerTaskResult;

    }
}
