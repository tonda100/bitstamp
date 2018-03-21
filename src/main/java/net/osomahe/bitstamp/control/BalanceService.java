package net.osomahe.bitstamp.control;

import java.util.Optional;

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

import com.salaryrobot.api.exchange.boundary.Wallet;
import net.osomahe.bitstamp.entity.BitstampCredentials;
import net.osomahe.bitstamp.entity.BitstampWallet;


/**
 * @author Antonin Stoklasek
 */
@Stateless
public class BalanceService {

    private static final String URL_BALANCE = "https://www.bitstamp.net/api/v2/balance/";

    @Inject
    private SignatureService serviceSignature;

    private Client client;

    @PostConstruct
    public void initClient() {
        this.client = ClientBuilder.newClient();
    }

    public Optional<Wallet> findWallet(BitstampCredentials credentials) {
        WebTarget target = this.client.target(URL_BALANCE);

        Form form = this.serviceSignature.createSignedForm(credentials);

        Response response = target.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        JsonObject data = response.readEntity(JsonObject.class);
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return Optional.of(new BitstampWallet(data));
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
