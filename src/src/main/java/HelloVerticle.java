import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class HelloVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.createHttpServer()
                .requestHandler(req -> req.response().end("Welcome to Vert.x"))
                .listen(config().getInteger("http.port", 9090), result -> {
                    if (result.succeeded()) {
                        LOGGER.info("HTTP server started on port {}", result.result().actualPort());
                        startPromise.complete();
                    } else {
                        LOGGER.error("Failed to start HTTP server", result.cause());
                        startPromise.fail(result.cause());
                    }
                });
    }

    @Override
    public void stop() {
        LOGGER.info("Shutting down application");
    }
}
