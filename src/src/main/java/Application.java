import io.vertx.core.Vertx;

public class Application {

    public static void main(String[] args) {
        /*vertx.deployVerticle(new HelloVerticle(), res -> {
            if (res.succeeded()) {
                System.out.println("Deployment id is: " + res.result());
            } else {
                System.out.println("Deployment failed!");
            }
        });*/

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Server());
        System.out.println(" --- Starting on localhost:9000 --- ");
    }
}
