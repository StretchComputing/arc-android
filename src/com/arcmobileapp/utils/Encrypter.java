package com.arcmobileapp.utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.GregorianCalendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Base64;

import com.arcmobileapp.ArcMobileApp;

/**
 * 
 * Handles encryption and decryption of strings in the application.
 */
public class Encrypter {

	public final static Encrypter INSTANCE = new Encrypter();

	private Cipher encryptCipher;
	private Cipher decryptCipher;
	private boolean mReadytoRun = false;

	private Encrypter() {
		// Singleton
	}

	/**
	 * enter a plain text string, receive an encrypted and encoded string ready for db storage
	 * 
	 * @param s
	 *            input string
	 * @return the encoded string
	 */
	public String packString(String s) {
		if (!mReadytoRun || s == null)
			return null;

		byte[] bytes = s.getBytes();
		try {
			byte[] encryptedBytes = encryptCipher.doFinal(bytes);
			return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
		} catch (IllegalBlockSizeException e) {
			Logger.d(this.toString(), "Illegal Block Size Exception.");
			e.printStackTrace();
		} catch (BadPaddingException e) {
			Logger.d(this.toString(), "Bad Padding Exception.");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * enter an encoded string from the db and receive the unencrypted version
	 * 
	 * @param s
	 *            encrypted string
	 * @return plaintext string
	 */
	public String unpackString(String s) {
		if (!mReadytoRun || s == null)
			return null;
		byte[] bytes = Base64.decode(s, Base64.DEFAULT);
		byte[] decryptedBytes;
		try {
			decryptedBytes = decryptCipher.doFinal(bytes);
			return new String(decryptedBytes);
		} catch (IllegalBlockSizeException e) {
			Logger.d(this.toString(), "Illegal Block Size exception");
			e.printStackTrace();
		} catch (BadPaddingException e) {
			Logger.d(this.toString(), "Bad Padding exception");
			e.printStackTrace();
		} catch (Exception e) {
			Logger.d(this.toString(), "Some other bad exception");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Called once to initialize the encrypter for the given device
	 * 
	 * @param baseContext
	 * @param seed
	 */
	public void configureEncrypter(String seed) {
		try {
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = null;
			Key localKey = null;
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, generatePassword(seed));
			localKey = keyStore.getKey("ac360mobile", generatePassword(seed));

			if (localKey == null) {
				localKey = generateKey(seed, keyStore);
			}
			setupKey(localKey);
			mReadytoRun = true;
		} catch (Exception e) {
			Logger.d(this.toString(), "Encrypter did not setup correctly.");
			e.printStackTrace();
			throw new RuntimeException(e);
			// TODO : warn user they are in trouble and all data and hope is lost!
		}

	}

	private char[] generatePassword(String seed) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		byte[] digest = messageDigest.digest(seed.getBytes());
		return Utils.byteArray2Hex(digest).toCharArray();
	}

	private void setupKey(Key localKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		encryptCipher = Cipher.getInstance("AES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, localKey);
		decryptCipher = Cipher.getInstance("AES");
		decryptCipher.init(Cipher.DECRYPT_MODE, localKey);
	}

	private Key generateKey(String seed, KeyStore keyStore) throws NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException {
		KeyGenerator kg;

		kg = KeyGenerator.getInstance("AES");
		SecureRandom sr = null;
		if (android.os.Build.VERSION.SDK_INT >= 17) {
			sr = SecureRandom.getInstance("SHA1PRNG", "Crypto"); // Needed for >= 4.2
		} else {
			sr = SecureRandom.getInstance("SHA1PRNG"); // Needed for < 4.2
		}
		sr.setSeed(seed.getBytes());
		kg.init(128, sr);
		SecretKey generateKey = kg.generateKey();
		keyStore.setKeyEntry("arcMobileApp", generateKey, generatePassword(seed), null);
		return generateKey;
	}

	/**
	 * determine if the encrypter has been configured
	 * 
	 * @return true if the encrypter is configured
	 */
	public boolean isReadyToRun() {
		return mReadytoRun;
	}

	public void setupEncrypter() {
		if (!mReadytoRun) {
			ArcPreferences prefs = new ArcPreferences();

			String seed = prefs.getBaseSeed();

			if (stringIsNullOrEmpty(seed)) {
				// we only want this to run once...
				seed = createSeed();
				prefs.setBaseSeed(seed);
			}

			configureEncrypter(seed + Constants.SALT);
		}
	}

	private String createSeed() {
		Long currentTimeInMillis = GregorianCalendar.getInstance().getTimeInMillis();
		String phoneId = "";
		String androidId = "";
		String seed = "";

		try {
			try {
				phoneId = ((TelephonyManager) ArcMobileApp.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
				Logger.d("phone ID = " + phoneId);
			} catch (Exception e) {
				Logger.d("Unable to get the Phone ID");
				e.printStackTrace();
			}

			try {
				androidId = Secure.getString(ArcMobileApp.getContext().getContentResolver(), Secure.ANDROID_ID);
				Logger.d("android ID = " + androidId);
			} catch (Exception e) {
				Logger.d("Unable to get the Android ID");
				e.printStackTrace();
			}
		} finally {
			seed = currentTimeInMillis.toString() + phoneId + androidId;
		}

		return seed;
	}

	private boolean stringIsNullOrEmpty(String seed) {
		return seed == null || "".equals(seed);
	}
}
