package programmer.security;

import programmer.model.DecryptedData;
import programmer.model.EncryptedData;

/**
 * Created by Burak on 10/25/17.
 */
public abstract class BaseSecurityManager  {


    public abstract EncryptedData<Byte> encrypt(byte[] plainText);
    public abstract DecryptedData<Byte> decrypt(byte[] cipherText);


}
