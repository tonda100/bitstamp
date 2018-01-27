package net.osomahe.bitstamp.boundary;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.salaryrobot.api.exchange.boundary.Wallet;
import com.salaryrobot.api.exchange.entity.ExchangePair;
import com.salaryrobot.api.ticker.entity.Price;
import net.osomahe.bitstamp.entity.BitstampWallet;
import net.osomahe.bitstamp.entity.BitstampWalletException;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class BitstampBoundary {

    private static final Logger logger = Logger.getLogger(BitstampBoundary.class.getName());

    private static final String URL_TICKER = "https://www.bitstamp.net/api/v2/ticker/";

    private static final String URL_BALANCE = "https://www.bitstamp.net/api/v2/balance/";

    private Client client;

    @PostConstruct
    public void initClient() {
        this.client = ClientBuilder.newClient();
    }

    public Optional<Price> getRecentPrice(ExchangePair exchangePair) {
        WebTarget target = client.target(URL_TICKER + exchangePair.getCode());
        Response response = target.request(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            JsonObject body = response.readEntity(JsonObject.class);
            Double ask = Double.valueOf(body.getString("ask"));
            Double bid = Double.valueOf(body.getString("bid"));
            return Optional.of(new Price(ask, bid));
        } else {
            logger.log(Level.WARNING, "Cannot receive ticker for " + exchangePair);
        }
        return Optional.empty();
    }

    public Wallet getWallet(final String customerId, final String apiKey, final String secretKey) {
        try {
            WebTarget target = this.client.target(URL_BALANCE);

            String nonce = System.currentTimeMillis() + "";
            // message = nonce + customer_id + api_key
            String message = nonce + customerId + apiKey;
            String signature = encodeHmacSHA256(secretKey, message);

            Form form = new Form();
            form.param("key", apiKey);
            form.param("signature", signature);
            form.param("nonce", nonce);

            Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
            JsonObject data = response.readEntity(JsonObject.class);
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return new BitstampWallet(data);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Loading of wallet failed for customer: %s and apiKey: %s", customerId, apiKey), e);
            throw new BitstampWalletException(customerId, apiKey);
        }
        return null;
    }

    private String encodeHmacSHA256(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] encodedData = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // encode to HexString
        return String.format("%x", new BigInteger(1, encodedData)).toUpperCase();
    }

    @PreDestroy
    private void destroy() {
        if (this.client != null) {
            this.client.close();
        }
    }

    public boolean isCredentialsValid(String customerId, String apiKey, String secretKey) {
        return getWallet(customerId, apiKey, secretKey) != null;
    }
}
