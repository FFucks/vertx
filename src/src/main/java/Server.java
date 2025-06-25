import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;

public class Server extends AbstractVerticle {

    public Map<Long, Client> clients = new HashMap<>();

    @Override
    public void start(Future<Void> future) {

        Router router = Router.router(vertx);

        /*router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html").end("<h1>First server with vertx</h1>");
        });*/

        router.route("/api/client*").handler(BodyHandler.create());
        router.post("/api/client").handler(this::addClient);
        router.get("/api/clients").handler(this::getAllClients);

        vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 9000),
            result -> {
                if (result.succeeded()) {
                    future.complete();
                } else {
                    future.fail(result.cause());
                }
            });
    }

    private void addClient(RoutingContext routingContext) {
        try {
            final Client client = Json.decodeValue(routingContext.getBodyAsString(),
                    Client.class);

            clients.put(client.getId(), client);

            routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(client));

        } catch (Exception e) {
            e.printStackTrace();
            routingContext.response()
                    .setStatusCode(500)
                    .end("Invalid client data: " + e.getMessage());
        }
    }

    private void getAllClients(RoutingContext routingContext) {
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(clients.values()));
    }
}
