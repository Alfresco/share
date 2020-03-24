/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.encryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.jasypt.encryption.StringEncryptor;

public class PublicPrivateKeyShareStringEncryptor implements StringEncryptor
{

    static final int KEY_SIZE = 512;
    protected String keyAlgorithm = "RSA";
    protected String encryptionAlgorithm = "RSA/ECB/PKCS1PADDING";

    protected PrivateKey privateKey = null;
    protected PublicKey publicKey = null;

    static final String KEYNAME = "alfrescoSpringKey";
    static final String PUBKEYNAME = KEYNAME + ".pub";
    static final String PRIKEYNAME = KEYNAME + ".pri";

    static final String KEY_PACKAGE = "/alfresco/web-extension/";
    static final String PRIKEYPATH = KEY_PACKAGE + "/" + PRIKEYNAME;
    static final String PUBKEYPATH = KEY_PACKAGE + "/" + PUBKEYNAME;

    @Override
    public String encrypt(String message)
    {
        Cipher cipher;
        byte[] cipherText = new byte[0];
        String retval = message;
        if (publicKey == null)
        {
            return retval;
        }

        try
        {
            cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // Number of bytes in the key - 11 for padding
            int chunkSize = (KEY_SIZE / 8) - 11;

            // bytes to encrypt
            byte[] messageBytes = message.getBytes("UTF-8");

            if (messageBytes.length > chunkSize)
            {
                // yes we need multiple chunks
                byte[] cipherChunk = new byte[0]; // chunk of encrypted stuff
                byte[] buffer = new byte[chunkSize]; // working buffer

                for (int i = 0; i < messageBytes.length; i++)
                {
                    // if we filled our buffer array we have our block ready for
                    // encryption
                    if ((i > 0) && (i % chunkSize == 0))
                    {
                        // execute the encryption operation
                        cipherChunk = cipher.doFinal(buffer);
                        cipherText = append(cipherText, cipherChunk);

                        // here we calculate the length of the next buffer
                        // required
                        int newlength = chunkSize;

                        // if newlength would be longer than remaining bytes in
                        // the bytes array we
                        // shorten it.
                        if (i + chunkSize > messageBytes.length)
                        {
                            newlength = messageBytes.length - i;
                        }
                        // clean the buffer array
                        buffer = new byte[newlength];
                    }
                    // copy byte into our buffer.
                    buffer[i % chunkSize] = messageBytes[i];
                } // for each byte in message

                // Any remaining bytest in buffer
                cipherChunk = cipher.doFinal(buffer);
                cipherText = append(cipherText, cipherChunk);
            }
            else
            {
                // we don't need multiple chunks
                cipherText = cipher.doFinal(messageBytes);
            }

            retval = new String(Base64.encodeBase64(cipherText));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        catch (NoSuchPaddingException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        catch (InvalidKeyException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        catch (IllegalBlockSizeException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        catch (BadPaddingException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        return retval;
    }

    /**
     * append two byte arrays together
     * 
     * @param prefix
     * @param suffix
     * @return a byte array containing the contents of prefix + suffix
     */
    private static byte[] append(byte[] prefix, byte[] suffix)
    {
        byte[] toReturn = new byte[prefix.length + suffix.length];
        for (int i = 0; i < prefix.length; i++)
        {
            toReturn[i] = prefix[i];
        }
        for (int i = 0; i < suffix.length; i++)
        {
            toReturn[i + prefix.length] = suffix[i];
        }
        return toReturn;
    }

    @Override
    public String decrypt(String encryptedMessage)
    {
        Cipher cipher;
        String retval = encryptedMessage;

        byte[] plainText = new byte[0];

        if (privateKey == null)
        {
            throw new RuntimeException("Unable to decrypt value,  private key not found");
        }
        try
        {
            cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // Chunk size is Number of bytes in the key
            int chunkSize = (KEY_SIZE / 8);

            // bytes to decrypt
            byte[] messageBytes = Base64.decodeBase64(encryptedMessage.getBytes("UTF-8"));

            // encrypted chunk
            byte[] cipherChunk = new byte[0];
            // Working buffer
            byte[] buffer = new byte[chunkSize];

            for (int i = 0; i < messageBytes.length; i++)
            {
                // if we filled our buffer array we have our block ready for
                // encryption
                if ((i > 0) && (i % chunkSize == 0))
                {
                    // execute the operation
                    cipherChunk = cipher.doFinal(buffer);
                    // add the result to our total result.
                    plainText = append(plainText, cipherChunk);

                    // here we calculate the length of the next buffer required
                    int newlength = chunkSize;

                    // if newlength would be longer than remaining bytes in the
                    // bytes array we
                    // shorten it.
                    if (i + chunkSize > messageBytes.length)
                    {
                        newlength = messageBytes.length - i;
                    }
                    // clean the buffer array
                    buffer = new byte[newlength];
                }
                // copy byte into our buffer.
                buffer[i % chunkSize] = messageBytes[i];
            }

            // Any remaining buffer
            cipherChunk = cipher.doFinal(buffer);
            plainText = append(plainText, cipherChunk);

            retval = new String(plainText, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Could not encrypt value", e);
        }
        catch (NoSuchPaddingException e)
        {
            throw new RuntimeException("Could not decrypt value", e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Could not decrypt value", e);
        }
        catch (InvalidKeyException e)
        {
            throw new RuntimeException("Could not decrypt value", e);
        }
        catch (IllegalBlockSizeException e)
        {
            throw new RuntimeException("Could not decrypt value", e);
        }
        catch (BadPaddingException e)
        {
            throw new RuntimeException("Could not decrypt value", e);
        }
        return retval;
    }

    /**
     * 
     * @param alfrescoSharedDir
     * @throws RuntimeException
     */
    public void initPublic(String alfrescoSharedDir)
    {
        File webExtensionDir = getWebExtensionDir(alfrescoSharedDir);

        File publicKeyFile = new File(webExtensionDir, PUBKEYNAME);

        if (publicKeyFile.canRead())
        {
            try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(publicKeyFile));)
            {
                publicKey = (PublicKey) is.readObject();
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Could not instantiate Public Key", e);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not open Public Key", e);
            }
        }
        else
        {
            throw new RuntimeException("Public Key File Not Found :" + publicKeyFile.getPath());
        }
    }

    /**
     * @param alfrescoSharedDir
     * @throws RuntimeException
     */
    public void initPrivate(String alfrescoSharedDir)
    {
        File webExtension = getWebExtensionDir(alfrescoSharedDir);

        File privateKeyFile = new File(webExtension, PRIKEYNAME);
        if (privateKeyFile.canRead())
        {
            ObjectInputStream is = null;
            try
            {
                is = new ObjectInputStream(new FileInputStream(privateKeyFile));
                privateKey = (PrivateKey) is.readObject();
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("Could not instantiate Private Key", e);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Could not find Private Key", e);
            }
            finally
            {
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException error)
                    {
                        // TODO log
                    }
                }
            }
        }
        else
        {
            throw new RuntimeException("Private Key File Not Found :" + privateKeyFile.getPath());
        }
    }

    /**
     * createKeyFiles
     * 
     * @param alfrescoSharedDir
     *            String
     */
    public void createKeyFiles(String alfrescoSharedDir)
    {
        File sharedDir = new File(alfrescoSharedDir);

        File webExtensionDir = getWebExtensionDir(alfrescoSharedDir);

        if (!sharedDir.exists())
        {
            throw new RuntimeException("alfresco shared dir does not exist : " + sharedDir);
        }

        if (!webExtensionDir.exists())
        {
            throw new RuntimeException("alfresco enterprise dir does not exist : " + webExtensionDir);
        }

        File publicKeyFile = new File(webExtensionDir, PUBKEYNAME);
        File privateKeyFile = new File(webExtensionDir, PRIKEYNAME);
        try
        {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyAlgorithm);
            keyGen.initialize(KEY_SIZE);
            KeyPair key = keyGen.generateKeyPair();

            try (ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));)
            {
                publicKeyOS.writeObject(key.getPublic());

                // TODO config log
                // info("public key created file: "+ publicKeyFile.getPath());
            }
            catch (IOException e)
            {
                throw new RuntimeException("unable to create public key file", e);
            }

            try (ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));)
            {
                privateKeyOS.writeObject(key.getPrivate());

                // TODO config log
                // info("private key created file:" + privateKeyFile.getPath());

            }
            catch (IOException e)
            {
                throw new RuntimeException("unable to create private key file", e);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Unable to generate public/private key", e);
        }
    }

    private File getWebExtensionDir(String alfrescoSharedDir)
    {
        File sharedDir = new File(alfrescoSharedDir);
        File alfrescoDir = new File(sharedDir, "alfresco");
        File webExtensionDir = new File(alfrescoDir, "web-extension");
        return webExtensionDir;
    }

    public String getPasswordFromConsole()
    {
        String enteredPassword = null;
        String verifyPassword = null;
        boolean firstOne = true;
        do
        {
            if (!firstOne)
            {
                System.console().writer().println("Please enter the same value twice to verify your encrypted value");
                System.console().writer().flush();
            }
            firstOne = false;
            System.console().writer().print("Please Enter Value: ");
            System.console().writer().flush();
            enteredPassword = new String(System.console().readPassword());
            System.console().writer().print("Please Repeat Value: ");
            System.console().writer().flush();
            verifyPassword = new String(System.console().readPassword());
        } while (enteredPassword == null || enteredPassword.length() < 1 || !enteredPassword.equals(verifyPassword));
        return enteredPassword;
    }

}
