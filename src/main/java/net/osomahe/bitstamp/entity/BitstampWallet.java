package net.osomahe.bitstamp.entity;

import java.util.Map;

import com.salaryrobot.api.entity.Asset;
import com.salaryrobot.api.entity.ExchangePair;
import com.salaryrobot.api.strategy.Wallet;


/**
 * @author Antonin Stoklasek
 */
public class BitstampWallet implements Wallet {

    private static final String AVAILABLE_SUFFIX = "_available";

    private static final String RESERVED_SUFFIX = "_reserved";

    private static final String TOTAL_SUFFIX = "_balance";

    private static final String FEE_SUFFIX = "_fee";

    private final Map data;


    public BitstampWallet(Map data) {
        this.data = data;
    }

    @Override
    public Double getAvailable(Asset asset) {
        String key = asset.getCode() + AVAILABLE_SUFFIX;
        if (data.containsKey(key)) {
            String value = (String) data.get(key);
            return Double.valueOf(value);
        }
        return null;
    }


    @Override
    public Double getReserved(Asset asset) {
        String key = asset.getCode() + RESERVED_SUFFIX;
        if (data.containsKey(key)) {
            String value = (String) data.get(key);
            return Double.valueOf(value);
        }
        return null;
    }


    @Override
    public Double getTotal(Asset asset) {
        String key = asset.getCode() + TOTAL_SUFFIX;
        if (data.containsKey(key)) {
            String value = (String) data.get(key);
            return Double.valueOf(value);
        }
        return null;
    }


    @Override
    public Double getFeePercentage(ExchangePair exchangePair) {
        String key = exchangePair.getCode() + FEE_SUFFIX;
        if (data.containsKey(key)) {
            String value = (String) data.get(key);
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
