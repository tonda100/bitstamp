package net.osomahe.bitstamp.entity;


/**
 * @author Antonin Stoklasek
 */
public class BitstampWalletException extends RuntimeException {


    private final String customerId;

    private final String apiKey;

    public BitstampWalletException(String customerId, String apiKey) {
        super("Cannot load wallet");
        this.customerId = customerId;
        this.apiKey = apiKey;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getApiKey() {
        return apiKey;
    }
}
