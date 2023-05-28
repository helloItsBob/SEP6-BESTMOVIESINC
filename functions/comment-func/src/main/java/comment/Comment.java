package comment;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

public class Comment implements HttpFunction
{
  String uri = System.getenv().get("NEO4J_URI");
  String neo4JUser = System.getenv().get("NEO4J_USER");
  String password = System.getenv().get("NEO4J_PASSWORD");

  @Override public void service(HttpRequest request, HttpResponse response)
      throws IOException
  {

    response.setContentType("application/json");
    // Allow cross-origin requests
    response.appendHeader("Access-Control-Allow-Origin", "*");
    response.appendHeader("Access-Control-Allow-Methods", "PUT");
    response.appendHeader("Access-Control-Allow-Methods", "POST");
    response.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    BufferedWriter writer = response.getWriter();

    Query query;

    Driver driver = GraphDatabase.driver(uri,
        AuthTokens.basic(neo4JUser, password), Config.defaultConfig());

    String uid = request.getFirstQueryParameter("uid").orElse("");
    int movieId = Integer.parseInt(
        request.getFirstQueryParameter("movieId").orElse(""));
    String comment = request.getFirstQueryParameter("content").orElse("");
    int rating = Integer.parseInt(
        request.getFirstQueryParameter("rating").orElse(""));

    switch (request.getMethod())
    {
      case "PUT":
        response.setStatusCode(HttpURLConnection.HTTP_OK);

        query = new Query(
            "MATCH (u:User {uid: $uid})-[r:COMMENTS]->(m:Movie {movieId: $movieId}) "
                + "SET r.content=$content, r.rating=$rating, r.date=date()",
            Map.of("uid", uid, "movieId", movieId, "content", comment, "rating",
                rating));

        try (var session = driver.session(SessionConfig.forDatabase("neo4j")))
        {

          session.executeWrite(tx -> tx.run(query));

        }
        catch (Neo4jException ex)
        {
          ex.printStackTrace();
        }

        writer.write(uid + " commented " + movieId);

        break;

      case "POST":
        response.setStatusCode(HttpURLConnection.HTTP_OK);

        query = new Query(
            "MATCH (u:User {uid: $uid}) MERGE (m:Movie {movieId: $movieId}) "
                + "MERGE (u)-[r:COMMENTS {content: $content, rating: $rating, date: date()}]->(m)",
            Map.of("uid", uid, "movieId", movieId, "content", comment, "rating",
                rating));

        try (var session = driver.session(SessionConfig.forDatabase("neo4j")))
        {

          session.executeWrite(tx -> tx.run(query));

        }
        catch (Neo4jException ex)
        {
          ex.printStackTrace();
        }

        writer.write(uid + " commented " + movieId);
        break;

      default:
        response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
        writer.write("Something went wrong!");
        break;
    }
    driver.close();
  }
}
