package com.arcmobileapp.utils;

import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
 
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Security {


	

	public String encryptBlowfish(String to_encrypt, String strkey) {
		try {
			SecretKeySpec key = new SecretKeySpec(strkey.getBytes(), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			//return new String(cipher.doFinal(to_encrypt.getBytes()));
			
			String encrypted = new String(cipher.doFinal(to_encrypt.getBytes()),  "ISO-8859-1");

			return encrypted;

		} catch (Exception e){
			return null; 
		}
	}
	
	public  String decryptBlowfish(String to_decrypt, String strkey) {
		  try {
		     SecretKeySpec key = new SecretKeySpec(strkey.getBytes(), "Blowfish");
		     Cipher cipher = Cipher.getInstance("Blowfish");
		     cipher.init(Cipher.DECRYPT_MODE, key);
		     byte[] decrypted = cipher.doFinal(to_decrypt.getBytes("ISO-8859-1"));
		     return new String(decrypted);
		  } catch (Exception e) { 

			  return null;

		  }
		}
	

	public String encrypt(String pin, String creditCardNumber) {
	    
		return creditCardNumber;
		
		
	}

	public String decrypt(String pin, String encryptedCreditCard) {
	   
		return encryptedCreditCard;
		
	}



}
