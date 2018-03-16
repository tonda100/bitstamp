package net.osomahe.bitstamp.control;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public abstract class AbstractBitstampService {

    private static final Logger logger = Logger.getLogger(AbstractBitstampService.class.getName());

    public static final int BITSTAMP_TIMEOUT_SECONDS = 20;

    public static final int BITSTAMP_MAX_ATTEMPTS = 3;

    @Resource
    private ManagedExecutorService mes;

    public <T> Optional<T> tryMultipleTimes(Callable<Optional<T>> task) {
        for (int i = 0; i < BITSTAMP_MAX_ATTEMPTS; i++) {
            Future<Optional<T>> result = this.mes.submit(task);
            try {
                return result.get(BITSTAMP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cannot get from Bitstamp", e);
            }
        }
        return Optional.empty();
    }
}
