import io.vertx.core.Vertx;

public class Application {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Server());
        System.out.println(" --- Starting on localhost:9000 --- ");
    }
}
