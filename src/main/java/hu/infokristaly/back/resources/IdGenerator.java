package hu.infokristaly.back.resources;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class IdGenerator {
    public static String getMD5Sum(byte[] value) throws NoSuchAlgorithmException{
        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(value);
        byte messageDigest[] = algorithm.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        }
        return hexString.toString();
    }
}
