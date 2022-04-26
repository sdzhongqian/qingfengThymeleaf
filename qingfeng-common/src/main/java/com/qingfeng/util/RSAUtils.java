package com.qingfeng.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * <p>
 *  rsa 加密工具
 */
public class RSAUtils {

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     *
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return encode(signature.sign());
    }

    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     *
     * @return
     * @throws Exception
     *
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(decode(sign));
    }

    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes =decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encode(key.getEncoded());
    }

    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return encode(key.getEncoded());
    }

    /**
     * base64 加密
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encode(byte[] bytes) throws Exception {
        return new String(Base64.encodeBase64(bytes));
    }
    /**
     * base64 解密
     * @param base64
     * @return
     * @throws Exception
     */
    public static byte[] decode(String base64) throws Exception {
        return Base64.decodeBase64(base64.getBytes());
    }

    /**
     * @Description: 解密
     * @Param: [data, privateKey]
     * @return: java.lang.String
     * @Author: anxingtao
     * @Date: 2019-3-13 11:06
     */
    public static String decrypt(String data, String privateKey) throws Exception {
        byte[] pubdata = decryptByPrivateKey(decode(data), privateKey);
        return new String(pubdata, "UTF-8");
    }

    /**
     * @Description: 加密
     * @Param: [data, publicKey]
     * @return: java.lang.String
     * @Author: anxingtao
     * @Date: 2019-3-13 11:10
     */
    public static String encrypt(String data, String publicKey) throws Exception {
        byte[] pubdata = encryptByPublicKey(data.getBytes("UTF-8"), publicKey);
        String outString = new String(encode(pubdata));
        return outString;
    }

    /**
     * @title encrypriva
     * @description 私钥加密
     * @author Administrator
     * @updateTime 2021/8/11 16:31
     */
    public static String encrypk(String data, String privateKey) throws Exception {
        byte[] resdata = encryptByPrivateKey(data.getBytes("UTF-8"), privateKey);
        String outString = new String(encode(resdata));
        return outString;
    }

    /**
     * @title decrypk
     * @description 公钥解密
     * @author Administrator
     * @updateTime 2021/8/11 16:32
     */
    public static String decrypk(String data, String publicKey) throws Exception {
        byte[] resdata = decryptByPublicKey(decode(data), publicKey);
        return new String(resdata, "UTF-8");
    }


    public static void main(String[] args) throws Exception {
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALbGpPkikVf51rzHHr5rCmtIjpsq9Pked0W5xu5Khf4fQEl7utYahfC3dleFaCyLkD39Q1PpyOrQm7YQPSxkHR7Bn/w/CA93EYNKXG1cAZjDr/ZJAd6xUY2K4GkriWB9v4OeCTThkKwfharJJW270No5VT49ktKAHKkug/WnCUN7AgMBAAECgYEAlxPNK20qTFjj6biBLg5WV2VrEtFIGl7XYdf0meUZqnr0bYkLX4we6GENPby05hUaTlL4gvT8MTPrcWss1XOPKQYRrSjLBZmmCiYe+SDxx0ZPmNN1db86X0ahQsNzxAVkTyIKKVW1SxwyvIOSN5H71yyBzZj/yG0qymj8H0hBVFkCQQDijlLZ9E7pGT/2HLitxEyi02kIjlsgLtjmk8NP8if5eCspmKXaw3xH2j3BLOGDGZUjeQGZcyOOHdGnNkbUVSdtAkEAzoe6ugzr63r70CwbWAEBQLRUfX4i8fiLazlCybhVAMEKjbKTdcAUFLfKNmharPHE/dlHLja/dReaLMKB7l69hwJABcM/AkI/m5hD0zvJysm6dU3RVyFf2gK3C65ogmkTcToIRweV+GmOiLlZZseAePg2ne9fBgsytVO22Hz98jq0RQJAGOyOX0eR7RAhdYTtI9izMwDQNXjUdMke4ii946QoNfgV8vW7D/nHMpzffWNolfhzYoMnMO+QeWwIwiATGBY83wJBAN5aLGltsuW/6ILvMTgciwdQlO3dTjzFXhbXURsyKWYF6QJnaeqNMVqr9F0lWXKQyuIu+mj5JRkZszOvS0jczNA=\n";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC2xqT5IpFX+da8xx6+awprSI6bKvT5HndFucbuSoX+H0BJe7rWGoXwt3ZXhWgsi5A9/UNT6cjq0Ju2ED0sZB0ewZ/8PwgPdxGDSlxtXAGYw6/2SQHesVGNiuBpK4lgfb+Dngk04ZCsH4WqySVtu9DaOVU+PZLSgBypLoP1pwlDewIDAQAB";
//        String msg = "{\"dev_id\":\"868989034328943\",\"user_id\":\"\",\"team_id\":\"\"}";
//        String msg = DateTimeUtil.getDate();
//        String sm = RSAUtils.encrypt(msg,publicKey);
//        System.out.println(sm);
//        String str = "IGCJsmAEdYDle/WYAS4qd3hCp9TAZRz806Sm8p4KepJ2XuDaOnKh65homSgZJ88/pQs5rEm6Qmsg+8JqMgqjixD8jZhnToVriKzkhHe2UarS+sT8oUAvXrO98FwZv7GELgt4mCuitwxrREYq4QUtwI6Fc+SGdF0BjeKNUSWHifg=";
//
//
//
//        String s = RSAUtils.decrypt(sm,privateKey);
//        System.out.println(s);

        //生成密钥对(公钥和私钥)
//        Map map = RSAUtils.genKeyPair();
//        System.out.println("----------------公钥----------------");
//        System.out.println(RSAUtils.getPublicKey(map));
//        System.out.println("----------------私钥----------------");
//        System.out.println(RSAUtils.getPrivateKey(map));

        String str = RSAUtils.encrypk("我爱你中国",privateKey);
        System.out.println(str);

        String res = RSAUtils.decrypk(str,publicKey);
        System.out.println(res);

//        String content = "F2cgqkTN1DehdikNnKtt5t0p+HrOr4hwWrbf9MGmksWUQFwWzFPbwt1at7uXYp8mt88KiIHrD/yKe8pkdzuq2VMTyvObryIbVannBHtzrhjBd2FoRQpaorMWtFuv7MZY3WmuqhzdhXMXAx1pB/hmEToncvW3ZpEWp7N5ItO89H8=";
//        String priKey = "MIICYQIBAAKBgQCijkoTGTel/S62WwUpXswc8m7Fdp+SY2ZMZKV8FeRD5QUi1mGYUHqmab7w/xN2vdNWI9sR9MPL4aO2IA57HD4HTGmidyCDmxl8QCRMSNwXroIP6mWpI5lHjajPth25n2gwsnYoAvrSfTnTkUAmoKUtQSg3/AZ9Rg8mf8NFY5AjGwIDAQABAoGBAJqS8W9NyHPn2DaBQNxBD5jrE1hj34NFT+6OuinPa1sAeSzSbMV4qdh6r53dADYmdcLwn41okZLbAmDaBBscUUZo/pIUPdGgU+P7RqpMtfwbq/RoleFpU2GWBErJ7/qKe6cfcEyuBhAMG8vLqtfA70P75vFAMoKhzWsUGtD5gQVZAkUAuRjD839QS/u17Xv6jWbcw0vxTD2UN/hyObyQORlX+LjxC5RCuYwkkWoXIGe94hzsyCoURc6RhakRM/LyqhCliTQ+PvcCPQDg0xUHm2qr8jfo1PTYf6Ks8Id9FZiqxD1VJh2OoRMSTCPo8O4PskbzictNu2siFLt0r3EKjCDP1jP0H/0CRFSsO5d8OiNINmU5PdjJoVvFtdCGqvMfuEEpPWChc1jYYYxGem+e6GuM+J9eVcLGMJswhK2aXX+jY7c8AD5D9zXYrFDpAj0Ap5OOXEIyy4FavRhmfCz+wyrxwoFjbv2gvaQQaeyTu5K3PXy/5UE783Ek8Yad/yQ26W2Ps43pMyF1TiS9AkQrfddTmrz7OZEfFtgaHuSj/8gbkGEdr7H8YIML/wZ1ozDkfxwsnzgclJXir/Qb2VjGOSzfUrlj2SaxgqIGfKI7DyEWeg==";
//        System.out.println(priKey);
//
//        String priKey2 = "MIICewIBADANBgkqhkiG9w0BAQEFAASCAmUwggJhAgEAAoGBAKKOShMZN6X9LrZbBSlezBzybsV2n5JjZkxkpXwV5EPlBSLWYZhQeqZpvvD/E3a901Yj2xH0w8vho7YgDnscPgdMaaJ3IIObGXxAJExI3Beugg/qZakjmUeNqM+2HbmfaDCydigC+tJ9OdORQCagpS1BKDf8Bn1GDyZ/w0VjkCMbAgMBAAECgYEAmpLxb03Ic+fYNoFA3EEPmOsTWGPfg0VP7o66Kc9rWwB5LNJsxXip2Hqvnd0ANiZ1wvCfjWiRktsCYNoEGxxRRmj+khQ90aBT4/tGqky1/Bur9GiV4WlTYZYESsnv+op7px9wTK4GEAwby8uq18DvQ/vm8UAygqHNaxQa0PmBBVkCRQC5GMPzf1BL+7Xte/qNZtzDS/FMPZQ3+HI5vJA5GVf4uPELlEK5jCSRahcgZ73iHOzIKhRFzpGFqREz8vKqEKWJND4+9wI9AODTFQebaqvyN+jU9Nh/oqzwh30VmKrEPVUmHY6hExJMI+jw7g+yRvOJy027ayIUu3SvcQqMIM/WM/Qf/QJEVKw7l3w6I0g2ZTk92MmhW8W10Iaq8x+4QSk9YKFzWNhhjEZ6b57oa4z4n15VwsYwmzCErZpdf6NjtzwAPkP3NdisUOkCPQCnk45cQjLLgVq9GGZ8LP7DKvHCgWNu/aC9pBBp7JO7krc9fL/lQTvzcSTxhp3/JDbpbY+zjekzIXVOJL0CRCt911OavPs5kR8W2Boe5KP/yBuQYR2vsfxggwv/BnWjMOR/HCyfOByUleKv9BvZWMY5LN9SuWPZJrGCogZ8ojsPIRZ6";
//
//        System.out.println(RSAUtils.decrypt(content,priKey2));
    }


}