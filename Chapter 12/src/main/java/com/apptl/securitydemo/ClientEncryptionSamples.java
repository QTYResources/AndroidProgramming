package com.apptl.securitydemo;

import android.util.Base64;
import android.util.JsonReader;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.StringReader;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Collection;

/**
 * @author Erik Hellman
 */
public class ClientEncryptionSamples {
    public static SecretKey generateKey(char[] password, byte[] salt)
            throws Exception {
        int iterations = 1000;
        int outputKeyLength = 256;
        SecretKeyFactory secretKeyFactory
                = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password, salt,
                iterations, outputKeyLength);
        byte[] keyBytes = secretKeyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encryptClearText(char[] password, String plainText)
            throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        int saltLength = 8;
        byte[] salt = new byte[saltLength];
        secureRandom.nextBytes(salt);
        SecretKey secretKey = generateKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] initVector = new byte[cipher.getBlockSize()];
        secureRandom.nextBytes(initVector);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] cipherData = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.encodeToString(cipherData,
                Base64.NO_WRAP | Base64.NO_PADDING)
                + "]" + Base64.encodeToString(initVector,
                Base64.NO_WRAP | Base64.NO_PADDING)
                + "]" + Base64.encodeToString(salt,
                Base64.NO_WRAP | Base64.NO_PADDING);
    }

    public static String decryptData(char[] password, String encodedData)
            throws Exception {
        String[] parts = encodedData.split("]");
        byte[] cipherData = Base64.decode(parts[0], Base64.DEFAULT);
        byte[] initVector = Base64.decode(parts[1], Base64.DEFAULT);
        byte[] salt = Base64.decode(parts[2], Base64.DEFAULT);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParams = new IvParameterSpec(initVector);
        SecretKey secretKey = generateKey(password, salt);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
        return new String(cipher.doFinal(cipherData), "UTF-8");
    }

    public String encryptNoteDataCollection(Collection<NoteData> notes,
                                            char[] password) {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<NoteData>>(){}.getType();
        gson.toJson(notes, type, jsonWriter);
        String clearText = writer.toString();
        try {
            return encryptClearText(password, clearText);
        } catch (Exception e) {
            // Ignore for brevity
            return null;
        }
    }

    public static Collection<NoteData> decryptAndDecode(char[] password,
                                                        String encryptedData) {
        try {
            String jsonData = decryptData(password, encryptedData);
            Gson gson =  new Gson();
            Type type = new TypeToken<Collection<NoteData>>(){}.getType();
            JsonReader jsonReader = new JsonReader(new StringReader(jsonData));
            return gson.fromJson(jsonReader, type);
        } catch (Exception e) {
            // Ignore for brevity...
            return null;
        }
    }

}
