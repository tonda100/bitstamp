package net.osomahe.bitstamp.boundary;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.salaryrobot.api.exchange.boundary.Wallet;
import com.salaryrobot.api.exchange.entity.ExchangePair;
import com.salaryrobot.api.ticker.entity.Price;
import net.osomahe.bitstamp.control.BalanceService;
import net.osomahe.bitstamp.control.MarketService;
import net.osomahe.bitstamp.control.PriceService;
import net.osomahe.bitstamp.entity.BitstampCredentials;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class BitstampBoundary {

    @Inject
    private PriceService servicePrice;

    @Inject
    private BalanceService serviceBalance;

    @Inject
    private MarketService serviceMarket;

    public Optional<Price> getRecentPrice(ExchangePair exchangePair) {
        return this.servicePrice.getRecentPrice(exchangePair);
    }

    public Optional<Wallet> getWallet(BitstampCredentials credentials) {
        return this.serviceBalance.findWallet(credentials);
    }

    public boolean isCredentialsValid(BitstampCredentials credentials) {
        return getWallet(credentials).isPresent();
    }

    public void buyCommodity(Double commodityUnits, ExchangePair exchangePair, BitstampCredentials credentials) {
        this.serviceMarket.buyMarketOrder(commodityUnits, exchangePair, credentials);
    }
}
