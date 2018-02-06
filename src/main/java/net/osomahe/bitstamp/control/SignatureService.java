package net.osomahe.bitstamp.control;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.ws.rs.core.Form;

import net.osomahe.bitstamp.entity.BitstampCredentials;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class SignatureService {

    public Form createSignedForm(BitstampCredentials credentials) {
        String nonce = System.currentTimeMillis() + "";
        String message = nonce + credentials.getCustomerId() + credentials.getApiKey();
        String signature = getSignature(credentials.getSecretKey(), message);

        Form form = new Form();
        form.param("key", credentials.getApiKey());
        form.param("signature", signature);
        form.param("nonce", nonce);
        return form;

    }

    private String getSignature(String secretKey, String message) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] encodedData = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));

            // encode to HexString
            return String.format("%x", new BigInteger(1, encodedData)).toUpperCase();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot sign message", e);
        }
    }
}
