import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server extends AbstractVerticle {

    public Map<Long, Client> clients = new HashMap<>();

    @Override
    public void start(Future<Void> future) {

        Router router = Router.router(vertx);

        router.route("/api/*").handler(BodyHandler.create());

        router.post("/api/client").handler(this::addClient);
        router.get("/api/clients").handler(this::getAllClients);
        router.post("/api/address/:clientId").handler(this::addAddress);

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

    private void addAddress(RoutingContext routingContext) {
        String clientIdStr = routingContext.pathParam("clientId");
        Long clientId = Long.valueOf(clientIdStr);

        Client client = clients.get(clientId);
        if (client == null) {
            routingContext.response()
                    .setStatusCode(404)
                    .end("Client not found");
            return;
        }

        Address address = Json.decodeValue(routingContext.getBodyAsString(), Address.class);

        if (client.getAddresses() == null) {
            client.setAddresses(new ArrayList<>());
        }

        client.getAddresses().add(address);

        routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(client));
    }
}
