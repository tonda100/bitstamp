package net.osomahe.bitstamp.control;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import net.osomahe.bitstamp.entity.BitstampWalletException;


/**
 * @author Antonin Stoklasek
 */
@Provider
public class BitstampWalletExceptionMapper implements ExceptionMapper<BitstampWalletException> {

    @Override
    public Response toResponse(BitstampWalletException exception) {
        JsonObject body = Json.createObjectBuilder()
                .add("message", exception.getMessage())
                .add("customerId", exception.getCustomerId())
                .add("apiKey", exception.getApiKey())
                .build();
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
