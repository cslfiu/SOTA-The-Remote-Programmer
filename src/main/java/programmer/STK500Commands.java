package programmer;

/**
 * Created by Burak on 10/23/17.
 */
public class STK500Commands {
    public static byte CMD_SIGN_ON = (byte)0x01;
    public static byte TOKEN = (byte)0x0E;
    public static byte MESSAGE_START =(byte) 0x1B;
    public static byte CMD_SET_PARAMETER =(byte) 0x02;
    public static byte CMD_GET_PARAMETER = (byte)0x03;
    public static byte CMD_ENTER_PROGMODE_ISP = (byte)0x10;
    public static byte CMD_LEAVE_PROGMODE_ISP = (byte)0x11;
    public static byte CMD_LOAD_ADDRESS = (byte)0x06;
    // STK STATUS
    public static byte STATUS_CMD_OK = (byte)0x00;
    public static byte STATUS_CMD_TOUT =(byte) 0x80;
    public static byte STATUS_RDY_BSY_TOUT = (byte)0x81;
    public static byte STATUS_SET_PARAM_MISSING = (byte)0x82;
    public static byte STATUS_CKSUM_ERROR =(byte) 0xC1;
    public static byte STATUS_CMD_ILLEGAL_PARAMETER = (byte)0xCA;


}
