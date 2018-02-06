package net.osomahe.bitstamp.entity;

/**
 * @author Antonin Stoklasek
 */
public class BitstampCredentials {

    private final String customerId;

    private final String apiKey;

    private final String secretKey;

    public BitstampCredentials(final String customerId, final String apiKey, final String secretKey) {
        this.customerId = customerId;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String toString() {
        return "BitstampCredentials{" +
                "customerId='" + customerId + '\'' +
                ", apiKey='" + apiKey + '\'' +
                '}';
    }
}
