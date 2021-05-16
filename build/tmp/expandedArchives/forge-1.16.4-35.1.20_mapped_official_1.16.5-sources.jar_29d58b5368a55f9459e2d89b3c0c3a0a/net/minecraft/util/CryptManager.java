package net.minecraft.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CryptManager {
   @OnlyIn(Dist.CLIENT)
   public static SecretKey generateSecretKey() throws CryptException {
      try {
         KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
         keygenerator.init(128);
         return keygenerator.generateKey();
      } catch (Exception exception) {
         throw new CryptException(exception);
      }
   }

   public static KeyPair generateKeyPair() throws CryptException {
      try {
         KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
         keypairgenerator.initialize(1024);
         return keypairgenerator.generateKeyPair();
      } catch (Exception exception) {
         throw new CryptException(exception);
      }
   }

   public static byte[] digestData(String p_75895_0_, PublicKey p_75895_1_, SecretKey p_75895_2_) throws CryptException {
      try {
         return digestData(p_75895_0_.getBytes("ISO_8859_1"), p_75895_2_.getEncoded(), p_75895_1_.getEncoded());
      } catch (Exception exception) {
         throw new CryptException(exception);
      }
   }

   private static byte[] digestData(byte[]... p_244731_0_) throws Exception {
      MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");

      for(byte[] abyte : p_244731_0_) {
         messagedigest.update(abyte);
      }

      return messagedigest.digest();
   }

   @OnlyIn(Dist.CLIENT)
   public static PublicKey byteToPublicKey(byte[] p_75896_0_) throws CryptException {
      try {
         EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(p_75896_0_);
         KeyFactory keyfactory = KeyFactory.getInstance("RSA");
         return keyfactory.generatePublic(encodedkeyspec);
      } catch (Exception exception) {
         throw new CryptException(exception);
      }
   }

   public static SecretKey decryptByteToSecretKey(PrivateKey p_75887_0_, byte[] p_75887_1_) throws CryptException {
      byte[] abyte = decryptUsingKey(p_75887_0_, p_75887_1_);

      try {
         return new SecretKeySpec(abyte, "AES");
      } catch (Exception exception) {
         throw new CryptException(exception);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static byte[] encryptUsingKey(Key p_75894_0_, byte[] p_75894_1_) throws CryptException {
      return cipherData(1, p_75894_0_, p_75894_1_);
   }

   public static byte[] decryptUsingKey(Key p_75889_0_, byte[] p_75889_1_) throws CryptException {
      return cipherData(2, p_75889_0_, p_75889_1_);
   }

   private static byte[] cipherData(int p_75885_0_, Key p_75885_1_, byte[] p_75885_2_) throws CryptException {
      try {
         return setupCipher(p_75885_0_, p_75885_1_.getAlgorithm(), p_75885_1_).doFinal(p_75885_2_);
      } catch (Exception exception) {
         throw new CryptException(exception);
      }
   }

   private static Cipher setupCipher(int p_75886_0_, String p_75886_1_, Key p_75886_2_) throws Exception {
      Cipher cipher = Cipher.getInstance(p_75886_1_);
      cipher.init(p_75886_0_, p_75886_2_);
      return cipher;
   }

   public static Cipher getCipher(int p_151229_0_, Key p_151229_1_) throws CryptException {
      try {
         Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
         cipher.init(p_151229_0_, p_151229_1_, new IvParameterSpec(p_151229_1_.getEncoded()));
         return cipher;
      } catch (Exception exception) {
         throw new CryptException(exception);
      }
   }
}
