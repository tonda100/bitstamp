package net.osomahe.bitstamp.boundary;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import com.salaryrobot.api.exchange.boundary.Wallet;
import com.salaryrobot.api.exchange.entity.ExchangePair;
import com.salaryrobot.api.ticker.entity.Price;
import net.osomahe.bitstamp.control.BalanceService;
import net.osomahe.bitstamp.control.MarketService;
import net.osomahe.bitstamp.control.PriceService;
import net.osomahe.bitstamp.entity.BitstampCredentials;
import net.osomahe.bitstamp.entity.BuyTransaction;
import net.osomahe.bitstamp.entity.SellTransaction;


/**
 * Provides method for Bitstamp operations.
 *
 * @author Antonin Stoklasek
 */
@Stateless
public class BitstampBoundary {

    private static final Logger logger = Logger.getLogger(BitstampBoundary.class.getName());

    private static final int TIMEOUT_SECONDS_DEFAULT = 20;

    private static final int MAX_ATTEMPTS_DEFAULT = 3;

    @Resource
    private ManagedExecutorService mes;

    @Inject
    private PriceService servicePrice;

    @Inject
    private BalanceService serviceBalance;

    @Inject
    private MarketService serviceMarket;

    /**
     * Provides current price of given exchange pair.
     *
     * @param exchangePair - exchange pair which price will be downloaded
     * @return optional price with there the getting of price form Bitstamp is successful.
     */
    public Optional<Price> getRecentPrice(ExchangePair exchangePair) {
        return this.servicePrice.getRecentPrice(exchangePair);
    }

    /**
     * Provides current price of given exchange pair. It will try to download the price multiple times if the first one is not successful.
     *
     * @param exchangePair - exchange pair which price will be downloaded
     * @return optional price with there the getting of price form Bitstamp is successful.
     */
    public Optional<Price> getRecentPriceRepeatedly(ExchangePair exchangePair) {
        return getRecentPriceRepeatedly(exchangePair, TIMEOUT_SECONDS_DEFAULT, MAX_ATTEMPTS_DEFAULT);
    }

    /**
     * Provides current price of given exchange pair. It will try to download the price multiple times if the first one is not successful.
     *
     * @param exchangePair - exchange pair which price will be downloaded
     * @param timeoutSeconds - how long should wait for the response
     * @param maxAttempts - how many times should try if attempt failed
     * @return optional price with there the getting of price form Bitstamp is successful.
     */
    public Optional<Price> getRecentPriceRepeatedly(ExchangePair exchangePair, int timeoutSeconds, int maxAttempts) {
        return tryMultipleTimes(() -> this.servicePrice.getRecentPrice(exchangePair), timeoutSeconds, maxAttempts);
    }

    /**
     * Provides current wallet. What amount of every currency does a Bitstamp user have.
     *
     * @param credentials - credentials of the user
     * @return wallet data.
     */
    public Optional<Wallet> getWallet(BitstampCredentials credentials) {
        return this.serviceBalance.findWallet(credentials);
    }

    /**
     * Provides current wallet. What amount of every currency does a Bitstamp user have.
     * It will try to download the wallet data multiple times if the first one is not successful.
     *
     * @param credentials - credentials of the user
     * @return wallet data.
     */
    public Optional<Wallet> getWalletRepeatedly(BitstampCredentials credentials) {
        return getWalletRepeatedly(credentials, TIMEOUT_SECONDS_DEFAULT, MAX_ATTEMPTS_DEFAULT);
    }


    /**
     * Provides current wallet. What amount of every currency does a Bitstamp user have.
     * It will try to download the wallet data multiple times if the first one is not successful.
     *
     * @param credentials - credentials of the user
     * @param timeoutSeconds - how long should wait for the response
     * @param maxAttempts - how many times should try if attempt failed
     * @return wallet data.
     */
    public Optional<Wallet> getWalletRepeatedly(BitstampCredentials credentials, int timeoutSeconds, int maxAttempts) {
        return tryMultipleTimes(() -> this.serviceBalance.findWallet(credentials), timeoutSeconds, maxAttempts);
    }

    /**
     * Process the buy market order to Bitstamp.
     *
     * @param commodityUnits - how much of commodity you want to buy
     * @param exchangePair - what is the commodity and payment
     * @param credentials - credentials of the user
     * @return buy transaction details.
     */
    public Optional<BuyTransaction> buyCommodity(Double commodityUnits, ExchangePair exchangePair, BitstampCredentials credentials) {
        return this.serviceMarket.buyMarketOrder(commodityUnits, exchangePair, credentials);
    }

    /**
     * Process the buy market order to Bitstamp.
     * It will try to process the market buy order multiple times if the first one is not successful.
     *
     * @param commodityUnits - how much of commodity you want to buy
     * @param exchangePair - what is the commodity and payment
     * @param credentials - credentials of the user
     * @return buy transaction details.
     */
    public Optional<BuyTransaction> buyCommodityRepeatedly(Double commodityUnits, ExchangePair exchangePair, BitstampCredentials credentials) {
        return tryMultipleTimes(() ->
                        this.serviceMarket.buyMarketOrder(commodityUnits, exchangePair, credentials),
                TIMEOUT_SECONDS_DEFAULT,
                MAX_ATTEMPTS_DEFAULT);
    }

    /**
     * Process the sell market order to Bitstamp.
     *
     * @param commodityUnits - how much of commodity you want to sell
     * @param exchangePair - what is the commodity and payment
     * @param credentials - credentials of the user
     * @return sell transaction details.
     */
    public Optional<SellTransaction> sellCommodity(Double commodityUnits, ExchangePair exchangePair, BitstampCredentials credentials) {
        return this.serviceMarket.sellMarketOrder(commodityUnits, exchangePair, credentials);
    }

    /**
     * Process the sell market order to Bitstamp.
     * It will try to process the market sell order multiple times if the first one is not successful.
     *
     * @param commodityUnits - how much of commodity you want to sell
     * @param exchangePair - what is the commodity and payment
     * @param credentials - credentials of the user
     * @return sell transaction details.
     */
    public Optional<SellTransaction> sellCommodityRepeatedly(Double commodityUnits, ExchangePair exchangePair, BitstampCredentials credentials) {
        return tryMultipleTimes(() ->
                        this.serviceMarket.sellMarketOrder(commodityUnits, exchangePair, credentials),
                TIMEOUT_SECONDS_DEFAULT,
                MAX_ATTEMPTS_DEFAULT);
    }

    private <T> Optional<T> tryMultipleTimes(Callable<Optional<T>> task, int timeoutSeconds, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            Future<Optional<T>> result = this.mes.submit(task);
            try {
                return result.get(timeoutSeconds, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cannot get from Bitstamp", e);
            }
        }
        return Optional.empty();
    }
}
