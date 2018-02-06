package net.osomahe.bitstamp.entity;

import javax.json.JsonObject;

import com.salaryrobot.api.exchange.boundary.Wallet;
import com.salaryrobot.api.exchange.entity.Asset;
import com.salaryrobot.api.exchange.entity.ExchangePair;


/**
 * @author Antonin Stoklasek
 */
public class BitstampWallet implements Wallet {

    private static final String AVAILABLE_SUFFIX = "_available";

    private static final String RESERVED_SUFFIX = "_reserved";

    private static final String TOTAL_SUFFIX = "_balance";

    private static final String FEE_SUFFIX = "_fee";

    private final JsonObject data;


    public BitstampWallet(JsonObject data) {
        this.data = data;
    }

    public JsonObject getData() {
        return data;
    }

    @Override
    public Double getAvailable(Asset asset) {
        String key = asset.getCode() + AVAILABLE_SUFFIX;
        if (data.containsKey(key)) {
            String value = data.getString(key);
            return Double.valueOf(value);
        }
        return null;
    }


    @Override
    public Double getReserved(Asset asset) {
        String key = asset.getCode() + RESERVED_SUFFIX;
        if (data.containsKey(key)) {
            String value = data.getString(key);
            return Double.valueOf(value);
        }
        return null;
    }


    @Override
    public Double getTotal(Asset asset) {
        String key = asset.getCode() + TOTAL_SUFFIX;
        if (data.containsKey(key)) {
            String value = data.getString(key);
            return Double.valueOf(value);
        }
        return null;
    }


    @Override
    public Double getFeePercentage(ExchangePair exchangePair) {
        String key = exchangePair.getCode() + FEE_SUFFIX;
        if (data.containsKey(key)) {
            String value = data.getString(key);
            return Double.valueOf(value);
        }
        return null;
    }


    @Override
    public String toString() {
        return "BitstampWallet{" +
                "data=" + data +
                '}';
    }
}
