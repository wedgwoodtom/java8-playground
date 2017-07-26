package com.tpatterson.playground;

import com.theplatform.module.crypto.aes.PasswordAes;
import org.testng.annotations.Test;

/**
 * Simple password utils
 */
public class PasswordUtils
{
    @Test
    public void testEncrypt() throws Exception
    {
        String password = "Admin!!!";
        String encrypted = new PasswordAes().encrypt(password);
        System.out.println("encrypted '"+password+"' = '"+encrypted+"'");
    }

    @Test
    public void testDecrypt() throws Exception
    {
        String encryptedPassword = "1ip+oyixVyWrzaw8kret6FyymjqpvOW8FRBAbtSZB/U=";
        String decrypted = new PasswordAes().decrypt(encryptedPassword);
        System.out.println("decrypted '"+encryptedPassword+"' ='"+decrypted+"'");
    }

}
