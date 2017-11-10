package programmer.security;

import org.apache.commons.lang3.ArrayUtils;
import programmer.SOTAGlobals;
import programmer.device.MicroController;
import programmer.model.DecryptedData;
import programmer.model.EncryptedData;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by Burak on 10/25/17.
 */
public class AESCBCEncryption extends BaseSecurityManager {



    private byte[] initializationVector = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
    private byte[] secretKey = {0x2b, 0x7e, 0x15, 0x16, 0x28, (byte)0xae, (byte)0xd2, (byte)0xa6, (byte)0xab, (byte)0xf7, 0x15, (byte)0x88, 0x09, (byte)0xcf, 0x4f, 0x3c};
    private Cipher cipher = null;
    private SecretKey secretKeyObject;
    private MicroController microController;
    private Random random;


    public AESCBCEncryption(MicroController microController)
    {
        super();

        this.microController = microController;
        secretKeyObject = new SecretKeySpec(secretKey,"AES");
        random = new Random();

        try {
            cipher = Cipher.getInstance("AES/CBC/NOPADDING");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EncryptedData<Byte> encrypt(byte[] packet) {
        // todo duration needs to be filled.
        EncryptedData<Byte> encryptedData = new EncryptedData<>();
        int difference = 0;
        int residualNumber = packet.length % 16;
        if ( residualNumber != 0) {
             difference  = 16 - (packet.length % 16);

        }
        int newPacketSize = difference + packet.length;
        byte[] plainText = new byte[newPacketSize];

        for(int index = 0; index < difference; index++) {
            if(microController.getOtaMode() == SOTAGlobals.OTA_MODE.BASIC_CONFIDENTIALITY)
            plainText[(newPacketSize - 1) - index] = (byte) 0xff;
            else
                plainText[(newPacketSize - 1) - index] = (byte) random.nextInt(256);
        }
        for(int i = 0; i <packet.length; i++)
            plainText[i] = packet[i];

        try {

                cipher.init(Cipher.ENCRYPT_MODE,secretKeyObject,new IvParameterSpec(initializationVector));

                encryptedData.setData(ArrayUtils.toObject(cipher.doFinal(plainText)));


        }
         catch (Exception ex)
         {
                // todo logger support.
         }
        return encryptedData;
    }

    @Override
    public DecryptedData<Byte> decrypt(byte[] cipherText) {
            DecryptedData<Byte> decryptedData = new DecryptedData<>();

        try {
            cipher.init(Cipher.DECRYPT_MODE,secretKeyObject,new IvParameterSpec(initializationVector));
            decryptedData.setData(ArrayUtils.toObject(cipher.doFinal(cipherText)));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }


        return decryptedData;
    }
}
