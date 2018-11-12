package com.anhnguyen.multilevelauthenticator.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashMethods {

//    /**
//     * Returns the SHA1 hash for the provided String
//     * @param text
//     * @return the SHA1 hash or null if an error occurs
//     */
//    public static String SHA1(final String text) {
//        try {
//            MessageDigest md;
//            md = MessageDigest.getInstance("SHA-1");
//            md.update(text.getBytes("UTF-8"),
//                    0, text.length());
//            byte[] sha1hash = md.digest();
//
//            return toHex(sha1hash);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static String toHex(final byte[] buf) {
//        if (buf == null) return "";
//
//        int l = buf.length;
//        StringBuffer result = new StringBuffer(2 * l);
//
//        for (int i = 0; i < buf.length; i++) {
//            appendHex(result, buf[i]);
//        }
//        return result.toString();
//    }
//
//    private final static String HEX = "0123456789ABCDEF";
//
//    private static void appendHex(final StringBuffer sb, final byte b) {
//        sb.append(HEX.charAt((b >> 4) & 0x0f))
//                .append(HEX.charAt(b & 0x0f));
//    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}