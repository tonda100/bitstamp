package net.osomahe.bitstamp.control;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.salaryrobot.api.exchange.entity.ExchangePair;
import net.osomahe.bitstamp.entity.BitstampCredentials;
import net.osomahe.bitstamp.entity.BuyTransaction;
import net.osomahe.bitstamp.entity.SellTransaction;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class MarketService extends AbstractBitstampService {
    private static final Logger logger = Logger.getLogger(MarketService.class.getName());

    private static final String URL_MARKET_BUY = "https://www.bitstamp.net/api/v2/buy/market/";

    private static final String URL_MARKET_SELL = "https://www.bitstamp.net/api/v2/sell/market/";

    @Inject
    private SignatureService serviceSignature;

    private Client client;

    @PostConstruct
    public void initClient() {
        this.client = ClientBuilder.newClient();
    }

    public Optional<BuyTransaction> buyMarketOrder(Double commodityUnits, ExchangePair exchangePair, BitstampCredentials credentials) {
        return tryMultipleTimes(() -> buyMarketOrderUnsafe(commodityUnits, exchangePair, credentials));
    }

    private Optional<BuyTransaction> buyMarketOrderUnsafe(Double commodityUnits, ExchangePair exchangePair, BitstampCredentials credentials) {
        WebTarget target = client.target(URL_MARKET_BUY + exchangePair.getCode() + "/");

        Form form = this.serviceSignature.createSignedForm(credentials);
        form.param("amount", double2String(commodityUnits, exchangePair.getCommodityPrecision()));

        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            JsonObject body = response.readEntity(JsonObject.class);
            BuyTransaction transaction = new BuyTransaction();
            transaction.setOrderId(body.getString("id"));
            transaction.setPrice(Double.valueOf(body.getString("price")));
            transaction.setDateTime(ZonedDateTime.now());
            return Optional.of(transaction);
        } else {
            String bodyError = response.readEntity(String.class);
            logger.log(Level.WARNING, "BUY transaction failed with response: " + bodyError);
        }
        return Optional.empty();
    }

    public Optional<SellTransaction> sellMarketOrder(Double commodityUnits, ExchangePair exchangePair, BitstampCredentials credentials) {
        return tryMultipleTimes(() -> sellMarketOrderUnsafe(commodityUnits, exchangePair, credentials));
    }

    private Optional<SellTransaction> sellMarketOrderUnsafe(Double commodityUnits, ExchangePair exchangePair, BitstampCredentials credentials) {
        WebTarget target = client.target(URL_MARKET_SELL + exchangePair.getCode() + "/");

        Form form = this.serviceSignature.createSignedForm(credentials);
        form.param("amount", double2String(commodityUnits, exchangePair.getCommodityPrecision()));

        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            JsonObject body = response.readEntity(JsonObject.class);
            SellTransaction transaction = new SellTransaction();
            transaction.setOrderId(body.getString("id"));
            transaction.setPrice(Double.valueOf(body.getString("price")));
            transaction.setDateTime(ZonedDateTime.now());
            return Optional.of(transaction);
        } else {
            String bodyError = response.readEntity(String.class);
            logger.log(Level.WARNING, "SELL transaction failed with response: " + bodyError);
        }
        return Optional.empty();
    }

    private String double2String(double number, int scale) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(number));
        BigDecimal bigDecimalRounded = bigDecimal.setScale(scale, RoundingMode.UP);
        return bigDecimalRounded.toPlainString();
    }

    @PreDestroy
    private void destroy() {
        if (this.client != null) {
            this.client.close();
        }
    }
}
