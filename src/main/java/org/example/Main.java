package org.example;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Main {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);
        DeploymentOptions deploymentOptions = new DeploymentOptions();

        vertx.deployVerticle(new vertxVerticle(), deploymentOptions)
                .onSuccess(id -> System.out.println("Verticle deployed successfully with ID: " + id))
                .onFailure(err -> System.err.println("Deployment failed: " + err.getMessage()));
    }
}
