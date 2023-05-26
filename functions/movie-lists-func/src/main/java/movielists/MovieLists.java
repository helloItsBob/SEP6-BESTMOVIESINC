package movielists;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.neo4j.driver.*;

import java.io.BufferedWriter;

public class MovieLists implements HttpFunction
{
  String uri = System.getenv().get("NEO4J_URI");
  String neo4JUser = System.getenv().get("NEO4J_USER");
  String password = System.getenv().get("NEO4J_PASSWORD");

  @Override public void service(HttpRequest httpRequest,
      HttpResponse httpResponse) throws Exception
  {
    // Allow cross-origin requests
    httpResponse.appendHeader("Access-Control-Allow-Origin", "*");
    httpResponse.appendHeader("Access-Control-Allow-Methods", "POST");
    httpResponse.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    BufferedWriter writer = httpResponse.getWriter();

    try
    {
      String uid = httpRequest.getFirstQueryParameter("uid").orElse("");
      int movieId = Integer.parseInt(
          httpRequest.getFirstQueryParameter("movieId").orElse(""));
      String list = httpRequest.getFirstQueryParameter("list").orElse("");
      String action = httpRequest.getFirstQueryParameter("action").orElse("");

      if (action.equals("store"))
      {
        // Call a method to store the movie in the database
        storeMovie(list, uid, movieId);
        httpResponse.setStatusCode(200);
        writer.write("Movie stored successfully!");

      }
      else if (action.equals("remove"))
      {
        // Call a method to remove the movie from the database
        removeMovie(list, uid, movieId);
        httpResponse.setStatusCode(200);
        writer.write("Movie removed successfully!");
      }
      else
      {
        httpResponse.setStatusCode(403);
        writer.write("Invalid action!");
      }
    }
    catch (Exception e)
    {
      httpResponse.setStatusCode(500);
      writer.write("An error has occurred!");
    }
  }

  private void storeMovie(String list, String uid, int movieId)
  {
    try (Driver driver = GraphDatabase.driver(uri,
        AuthTokens.basic(neo4JUser, password));
        Session session = driver.session())
    {
      // Create a movie node, match user and movie nodes, create relationship if it doesn't exist
      if (list.equals("favorites"))
      {
        session.run("MATCH (u:User {uid: $uid}) "
                + "MERGE (m:Movie {movieId: $movieId}) "
                + "MERGE (u)-[:FAVORITES]->(m)",
            Values.parameters("uid", uid, "movieId", movieId));
      }
      else if (list.equals("watchlist"))
      {
        session.run("MATCH (u:User {uid: $uid}) "
                + "MERGE (m:Movie {movieId: $movieId}) "
                + "MERGE (u)-[:WATCHLIST]->(m)",
            Values.parameters("uid", uid, "movieId", movieId));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void removeMovie(String list, String uid, int movieId)
  {
    try (Driver driver = GraphDatabase.driver(uri,
        AuthTokens.basic(neo4JUser, password));
        Session session = driver.session())
    {
      // Delete the relationship between the user and a movie
      if (list.equals("favorites"))
      {
        session.run(
            "MATCH (:User {uid: $uid})-[r:FAVORITES]->(:Movie {movieId: $movieId}) "
                + "DELETE r",
            Values.parameters("uid", uid, "movieId", movieId));
      }
      else if (list.equals("watchlist"))
      {
        session.run(
            "MATCH (:User {uid: $uid})-[r:WATCHLIST]->(:Movie {movieId: $movieId}) "
                + "DELETE r",
            Values.parameters("uid", uid, "movieId", movieId));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}

