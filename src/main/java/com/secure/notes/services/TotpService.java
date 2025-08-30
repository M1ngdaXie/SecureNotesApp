package com.secure.notes.services;

import com.google.zxing.WriterException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.io.IOException;

public interface TotpService {

    GoogleAuthenticatorKey generateSecretKey();

    String getQrCodeUrl(String username, GoogleAuthenticatorKey key);

    boolean verifyCode(String secret, int code);

    String generateQrCodeImage(String qrCodeUrl) throws WriterException, IOException;

}
