package umm3601.mongotest.game;
import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.mock;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

// import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
// import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
// import io.javalin.validation.BodyValidator;
import umm3601.game.Game;
import umm3601.game.GameController;
// import io.javalin.validation.ValidationException;
// import io.javalin.json.JavalinJackson;

class GameControllerSpec {

  private ObjectId gameID;



    private GameController gameController;

    private static MongoClient mongoClient;
    private static MongoDatabase db;

    // private static JavalinJackson javalinJackson = new JavalinJackson();

    @Mock
    private Context ctx;

    @Captor
    private ArgumentCaptor<Map<String, String>> mapCaptor;

    @Captor
    private ArgumentCaptor<Game> gameCaptor;

    @BeforeAll
    static void setupAll() {
      String mongoAddr = System.getenv().getOrDefault("MONGO_ADDR", "localhost");

      mongoClient = MongoClients.create(
          MongoClientSettings.builder()
              .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(mongoAddr))))
              .build());
      db = mongoClient.getDatabase("test");


  }

  @AfterAll
  static void teardown() {
    db.drop();
    mongoClient.close();
  }

  @BeforeEach
  void setupEach() throws IOException {
    MockitoAnnotations.openMocks(this);

    MongoCollection<Document> gameDocuments = db.getCollection("games");
    gameDocuments.drop();

    gameController = new GameController(db);

    gameID = new ObjectId();


    BsonArray usernames = new BsonArray();
    usernames.add(new BsonString("Kristin"));
    usernames.add(new BsonString("Jeff"));

    BsonArray responses = new BsonArray();
    responses.add(new BsonString("apple"));
    responses.add(new BsonString("banana"));
    responses.add(new BsonString("grape"));

    Document newGame = new Document()
      .append("players", usernames)
      .append("prompt", "What is the meaning of life?")
      .append("responses", responses)
      .append("judge", 1)
      .append("discardLast", true)
      .append("winnerBecomesJudge", false)
      .append("_id", gameID);


    // test_id = new ObjectId("67c74ff45818e91bd07be91b");

    gameDocuments.insertOne(newGame);

  }



  @Test
  void getGameWithExistentId() throws IOException {

    String id = gameID.toHexString();
    when(ctx.pathParam("id")).thenReturn(id);

    gameController.getGame(ctx);

    verify(ctx).json(gameCaptor.capture());
    verify(ctx).status(HttpStatus.OK);
    assertEquals(gameID.toHexString(), gameCaptor.getValue()._id);
  }


  @Test
  void getGameWithNonexistentId() throws IOException {

    String id = new ObjectId().toHexString();
    when(ctx.pathParam("id")).thenReturn(id);

    assertThrows(NotFoundResponse.class, () -> {
      gameController.getGame(ctx);
    });
  }

  @Test
  void getGameWithNULLId() throws IOException {

    when(ctx.pathParam("id")).thenReturn(null);

    assertThrows(BadRequestResponse.class, () -> {
      gameController.getGame(ctx);
    });
  }




  @Test
  void testAddRouts() {
    // Javalin javalin = mock(Javalin.class);
    // gameController.addRoutes(javalin);
    // gameController.addRoutes(javalin);
    // verify(javalin).get("/api/game/{id}", gameController::getGame);
    // verify(javalin).post("/api/game/new", gameController::addNewGame);
    // verify(javalin).get("/api/game/number", gameController::numGames);
  }



  // @Test
  // void returnNumGames() throws IOException {
  //   gameController.numGames(ctx);
  //   verify(ctx).json(mapCaptor.capture());
  //   assertEquals(1, mapCaptor.getValue().size());
  // }
 }
