package com.secure.notes.services.serviceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.secure.notes.services.TotpService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class TotpServiceImpl implements TotpService {
    private GoogleAuthenticator gAuth;

    public TotpServiceImpl(GoogleAuthenticator gAuth) {
        this.gAuth = new GoogleAuthenticator();
    }
    public TotpServiceImpl() {
        this.gAuth = new GoogleAuthenticator();
    }
    @Override
    public GoogleAuthenticatorKey generateSecretKey() {
        return gAuth.createCredentials();
    }
    @Override
    public String getQrCodeUrl(String username, GoogleAuthenticatorKey key) {
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("SecureNotesApp", username, key);
    }

    // New method to generate QR code as base64 image
    @Override
    public String generateQrCodeImage(String qrCodeUrl) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] qrCodeBytes = outputStream.toByteArray();

        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }
    @Override
    public boolean verifyCode(String secret, int code) {
        return gAuth.authorize(secret, code);
    }

}
