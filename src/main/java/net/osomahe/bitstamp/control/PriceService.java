package net.osomahe.bitstamp.control;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.salaryrobot.api.exchange.entity.ExchangePair;
import com.salaryrobot.api.ticker.entity.Price;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class PriceService {
    private static final Logger logger = Logger.getLogger(PriceService.class.getName());

    private static final String URL_TICKER = "https://www.bitstamp.net/api/v2/ticker/";

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

    @PreDestroy
    private void destroy() {
        if (this.client != null) {
            this.client.close();
        }
    }

}
