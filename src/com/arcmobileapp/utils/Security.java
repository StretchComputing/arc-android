package com.arcmobileapp.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.arcmobileapp.web.rskybox.CreateClientLogTask;

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
			(new CreateClientLogTask("Security.encryptBlowfish", "Exception Caught", "error", e)).execute();

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
			  //Errors here are if the PIN entered was incorrect
				//(new CreateClientLogTask("Security.decryptBlowfish", "Exception Caught", "error", e)).execute();

			  return null;

		  }
		}
	

	



}
