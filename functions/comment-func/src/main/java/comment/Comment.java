package comment;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Query;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.exceptions.Neo4jException;

public class Comment implements HttpFunction {

  String uri = System.getenv().get("NEO4J_URI");
  String neo4JUser = System.getenv().get("NEO4J_USER");
  String password = System.getenv().get("NEO4J_PASSWORD");
  private static final Gson gson = new Gson();

  @Override
  public void service(HttpRequest request, HttpResponse response) throws IOException {

    response.setContentType("application/json");
    // Allow cross-origin requests
    response.appendHeader("Access-Control-Allow-Origin", "*");
    response.appendHeader("Access-Control-Allow-Methods", "GET");
    response.appendHeader("Access-Control-Allow-Methods", "PUT");
    response.appendHeader("Access-Control-Allow-Methods", "POST");
    response.appendHeader("Access-Control-Allow-Methods", "DELETE");
    response.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    BufferedWriter writer = response.getWriter();

    Query query;
    JsonObject responseJson = new JsonObject();

    Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(neo4JUser, password), Config.defaultConfig());

    String uid = request.getFirstQueryParameter("uid").orElse("");
    int movieId = Integer.parseInt(request.getFirstQueryParameter("movieId").orElse(""));
    String comment = request.getFirstQueryParameter("content").orElse("");
    int rating = Integer.parseInt(request.getFirstQueryParameter("rating").orElse(""));

    switch (request.getMethod()) {
      case "GET":

        response.setStatusCode(HttpURLConnection.HTTP_OK);

          query = new Query("MATCH (u:User)-[r:COMMENTS]->(m:Movie{movieId: $movieId}) RETURN u, r, m",
          Map.of("movieId", movieId));

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {
          var record = new Object();

            record = session.executeRead(tx -> tx.run(query).list().toArray());

          String json = gson.toJson(record);
          responseJson.addProperty("neo4jData", json);

        } catch (Neo4jException ex) {
          ex.printStackTrace();
        }

        writer.write(responseJson.toString());

        break;

      case "PUT":
        response.setStatusCode(HttpURLConnection.HTTP_OK);

        query = new Query(
            "MATCH (u:User{uid: $uid})-[r:COMMENTS]->(m:Movie{movieId: $movieId}) SET r.content =$content, r.rating=$rating, r.date=date()",
            Map.of("uid", uid, "movieId", movieId, "content", comment, "rating", rating));

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {

          session.executeWrite(tx -> tx.run(query));

        } catch (Neo4jException ex) {
          ex.printStackTrace();
        }

        writer.write(uid + " commented " + movieId);

        break;

      case "POST":
        response.setStatusCode(HttpURLConnection.HTTP_OK);

        query = new Query(
            "MATCH (u:User{uid: $uid}) MERGE (m:Movie{movieId: $movieId}) MERGE (u)-[r:COMMENTS{content: $content, rating: $rating}]->(m)",
            Map.of("uid", uid, "movieId", movieId, "content", comment, "rating", rating));

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {

          session.executeWrite(tx -> tx.run(query));

        } catch (Neo4jException ex) {
          ex.printStackTrace();
        }

        writer.write(uid + " commented " + movieId);

        break;

      case "DELETE":
        response.setStatusCode(HttpURLConnection.HTTP_OK);

        query = new Query("MATCH (:User{uid: $uid})-[r:COMMENTS]->(:Movie{movieId: $movieId}) DELETE r",
        Map.of("uid", uid, "movieId", movieId));

        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {

          session.executeWrite(tx -> tx.run(query));

        } catch (Neo4jException ex) {
          ex.printStackTrace();
        }

        writer.write(uid + " uncommented " + movieId);

        break;

      default:
        response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
        writer.write("Something went wrong!");
        break;
    }
    driver.close();
  }
}
