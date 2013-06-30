package com.arcmobileapp.utils;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.util.Base64;

public class Security {



	public String encrypt(String pin, String creditCardNumber) {
	    
		//encrypt the ccNumber, using the pin, and return encrypted value
		return creditCardNumber;
	}

	public String decrypt(String pin, String encryptedCreditCard) {
	   
		//decrypt the encryptedCC using the pin, and return CC number
		return encryptedCreditCard;
	}



}
