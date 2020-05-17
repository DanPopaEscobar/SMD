package com.example.lab7;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class CryptographySecurity {
    private KeyStore _keyStore;
    private String _algorithm = "RSA";
    private KeyPairGenerator _kGenerator;
    private KeyGenParameterSpec _keyGenParameterSpec;
    private String _alias = "Lab_7_CryptographySecurity";
//    private String _keyStorePass = "VeryImportantSecret";
    private Cipher _cipher;

    public CryptographySecurity() throws NoSuchProviderException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, NoSuchPaddingException {
        _keyStore = KeyStore.getInstance("AndroidKeyStore");
        _keyStore.load(null);
        _cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        _kGenerator = KeyPairGenerator.getInstance(_algorithm, "AndroidKeyStore");
        KeyGenerator();
    }

    private void KeyGenerator() {
        try {
            if (!KeyValidator()) {
                _keyGenParameterSpec = new KeyGenParameterSpec.Builder(_alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setKeySize(2048)
                        .build();
                _kGenerator.initialize(_keyGenParameterSpec);
                _kGenerator.generateKeyPair();
            }else {
                Log.d("TAG","Keypair already generated");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean KeyValidator() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        _keyStore.load(null);
        return _keyStore.containsAlias(_alias);
    }

    public String Encrypt(String decrypted) throws BadPaddingException, IllegalBlockSizeException, KeyStoreException, InvalidKeyException {
        Key pubKey = _keyStore.getCertificate(_alias).getPublicKey();
        _cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] bytes = _cipher.doFinal(decrypted.getBytes());
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public String Decrypt(String ecrypted) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key privKey = _keyStore.getKey(_alias, null);
        _cipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] decryptedData = Base64.decode(ecrypted, Base64.DEFAULT);
        return new String(_cipher.doFinal(decryptedData));
    }
}
