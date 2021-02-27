package com.mycompany.myapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

public class SigningActivity extends Activity {
    private static final int INSTALL_CERT_CODE = 1001;
    private static final String CERT_FILENAME = "MyKeyStore.pfx";
    private static final String CERTIFICATE_NAME = "MyCertificate";
    private static final String TAG = "SigningActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if(requestCode == INSTALL_CERT_CODE) {
            if(resultCode == RESULT_OK) {
                // Certificate successfully installed
            } else {
                // User cancelled certificate installation
            }
        }
    }

    // click-listener for installing certificate
    public void doInstallCertificate(View view) {
        byte[] certData = readFile(CERT_FILENAME);
        Intent installCert = KeyChain.createInstallIntent();
        installCert.putExtra(KeyChain.EXTRA_NAME, CERTIFICATE_NAME)
        installCert.putExtra(KeyChain.EXTRA_PKCS12, certData);
        startActivityForResult(installCert, INSTALL_CERT_CODE);
    }

    private byte[] readFile(String certFilename) {
        // TODO Read the certificate from somewhere...
        return null;
    }

    public void doSignNoteData(View view) {
        KeyChain.choosePrivateKeyAlias(this, new KeyChainAliasCallback() {
            @Override
            public void alias(String alias) {
                EditText editText = (EditText) findViewById(R.id.input_text);
                String textToSign = editText.getText().toString();
                new MySigningTask().execute(textToSign, alias);
            }
        }, null, null, null, -1, null);
    }

    private class MySigningTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String[] data) {
            return createSignedNote(data[0], data[1]);
        }
    }

    public String createSignedNote(String textToSign, String alias) {
        try {
            byte[] textData = textToSign.getBytes("UTF-8");
            PrivateKey privateKey
                    = KeyChain.getPrivateKey(getApplicationContext(), alias);
            Signature signature
                    = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(textData);
            byte[] signed = signature.sign();
            return Base64.encodeToString(textData,
                    Base64.NO_WRAP | Base64.NO_PADDING)
                    + "]" + Base64.encodeToString(signed,
                    Base64.NO_WRAP | Base64.NO_PADDING);
        } catch (Exception e) {
            Log.e(TAG, "Error signing data.", e);
        }
        return null;
    }

    private boolean verifySignature(String dataAndSignature, String alias) {
        try {
            String[] parts = dataAndSignature.split("]");
            byte[] decodedText = Base64.decode(parts[0], Base64.DEFAULT);
            byte[] signed = Base64.decode(parts[1], Base64.DEFAULT);
            X509Certificate[] chain = KeyChain.getCertificateChain(this, alias);
            PublicKey publicKey = chain[0].getPublicKey();
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(publicKey);
            signature.update(decodedText);
            return signature.verify(signed);
        } catch (Exception e) {
            Log.e(TAG, "Error verifying signature.", e);
        }
        return false;
    }

}
