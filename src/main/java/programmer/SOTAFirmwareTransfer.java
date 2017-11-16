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
    private MicroController microController;
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
        sotaProtocol.sendFirmware(firmware);
//        while(true)
//        {
//
//            while(!sotaProtocol.GetSync())
//            {
//                Thread.sleep(50);
//            }
//
//            while(!sotaProtocol.EnableProgramMode())
//            {
//                Thread.sleep(50);
//            }
//
//            if(sotaProtocol.sendFirmware(firmware)) {
//                while(!sotaProtocol.CloseProgramMode()){Thread.sleep(50);}
//                programmerTaskResult.setSuccessed(true);
//            }
//
//            break;
//        }


        return programmerTaskResult;

    }
}
