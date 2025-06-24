package org.example;


import com.mongodb.ClientSessionOptions;
import com.mongodb.client.*;
import com.mongodb.client.internal.MongoClientImpl;
import com.mongodb.connection.ClusterDescription;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class vertxVerticle extends AbstractVerticle {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    public vertxVerticle(){
        String connectionString = "mongodb://admin:admin@172.21.17.53:27017,172.21.17.54:27017,172.21.17.92:27017/";
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("CompanyMatt");
        MongoCollection<Document> booksCollection = database.getCollection("Employee");
    }


//
//    private static final List<JsonObject> students = new ArrayList<>();
//
//    @Override
//    public void start(Promise<Void> startPromise) {
//        HttpServer server = vertx.createHttpServer();
//        Router router = Router.router(vertx);
//
//        router.route().handler(BodyHandler.create());
//
//        router.get("/hai").handler(ctx -> {
//            ctx.response().end("You visited hello yay!!");
//        });
//
//        // POST /adds to add a student
//        router.post("/adds").handler(this::handleAddStudent);
//
//        // GET /stud to return all students
//        router.get("/stud").handler(ctx -> {
//            JsonArray arr = new JsonArray(students);
//            ctx.response()
//                    .putHeader("Content-Type", "application/json")
//                    .end(arr.encodePrettily());
//        });
//
//        server.requestHandler(router)
//                .listen(8080)
//                .onSuccess(s -> {
//                    System.out.println("Server started on port 8080");
//                    startPromise.complete();
//                })
//                .onFailure(startPromise::fail);
//    }
//
//    // Handles adding a student
//    public void handleAddStudent(RoutingContext ctx) {
//        JsonObject json = ctx.getBodyAsJson();
//        if (json == null) {
//            ctx.response().setStatusCode(400).end("Invalid JSON");
//            return;
//        }
//
//        String username = json.getString("username");
//        String phno = json.getString("phno"); // no space in key
//
//        if (username == null || phno == null) {
//            ctx.response().setStatusCode(400).end("Missing fields");
//            return;
//        }
//
//        students.add(json);
//
//        JsonObject response = new JsonObject()
//                .put("message", "Student added successfully")
//                .put("student", json);
//
//        ctx.response()
//                .putHeader("Content-Type", "application/json")
//                .end(response.encodePrettily());
//    }
//
    @Override
    public void stop(Promise<Void> stopPromise) {
        System.out.println("Server stopping...");
        stopPromise.complete();
    }

    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Vertx vertx = Vertx.vertx();
        String connectionString = "mongodb://admin:admin@172.21.17.53:27017,172.21.17.54:27017,172.21.17.92:27017/";
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("CompanyMatt");
        MongoCollection<Document> booksCollection = database.getCollection("Employee");

//        JsonObject mongoConfig = new JsonObject()
//                .put("connection_string", "mongodb://admin:admin@172.21.17.53:27017,172.21.17.54:27017,172.21.17.92:27017/")
//                .put("db_name", "CompanyMatt");


        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/employee").handler(ctx -> {
//                Document doc1=booksCollection.find();
            JsonArray jarr = new JsonArray();
            for (Document doc : booksCollection.find()) {
                jarr.add(new JsonObject(doc.toJson()));

            }
            if (jarr.isEmpty()) {
                ctx.response()
                        .setStatusCode(500)
                        .putHeader("Content", "application/json")
                        .end("Doc returned as NULL.");
            } else {
                ctx.response().putHeader("Content", "application/json").end(jarr.encodePrettily());

            }
        });


        router.post("/employee").handler(ctx -> {
            JsonObject employee = ctx.getBodyAsJson();

            if (employee == null
                    || !employee.containsKey("Emp id")
                    || !employee.containsKey("name")
                    || !employee.containsKey("Email")
                    || !employee.containsKey("Skills")
                    || !employee.containsKey("Department")
                    || !employee.containsKey("Joining date")) {

                ctx.response()
                        .setStatusCode(400)
                        .putHeader("Content", "application/json")
                        .end(new JsonObject()
                                .put("error", "'EMP id', 'name', 'Email', 'Skills', 'Department', and 'Joining date' are required")
                                .encode());

            }
            else{
                booksCollection.insertOne(Document.parse(employee.encode()));
                ctx.response().end("Values Inserted");
            }

        });

//            mongoClient.insert("Employee", employee, res1 -> {
//                if (res1.succeeded()) {
//                    ctx.response()
//                            .setStatusCode(201)
//                            .putHeader("Content", "application/json")
//                            .end(new JsonObject()
//                                    .put("message", "Employee added successfully")
//                                    .put("id", res1.result())
//                                    .encode());
//                } else {
//                    ctx.response()
//                            .setStatusCode(500)
//                            .putHeader("Content", "application/json")
//                            .end(new JsonObject()
//                                    .put("error", res1.cause().getMessage())
//                                    .encode());
//                }
//            });
//        });


        // Start HTTP server on port 8080
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, res -> {
                    if (res.succeeded()) {
                        System.out.println("Server running at http://localhost:8080");
                    } else {
                        System.err.println("Failed to start server: " + res.cause());
                    }
                });


    }
//            server.listen(8080).toCompletionStage().toCompletableFuture();
}

