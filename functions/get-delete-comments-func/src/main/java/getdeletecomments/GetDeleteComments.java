package getdeletecomments;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.neo4j.driver.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;

public class GetDeleteComments implements HttpFunction
{
  String uri = System.getenv().get("NEO4J_URI");
  String neo4JUser = System.getenv().get("NEO4J_USER");
  String password = System.getenv().get("NEO4J_PASSWORD");

  @Override public void service(HttpRequest request, HttpResponse response)
      throws IOException
  {
    // Allow cross-origin requests
    response.appendHeader("Access-Control-Allow-Origin", "*");
    response.appendHeader("Access-Control-Allow-Methods", "GET, DELETE");
    response.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    String uid = request.getFirstQueryParameter("uid").orElse("");
    int movieId = Integer.parseInt(
        request.getFirstQueryParameter("movieId").orElse(""));

    try (Driver driver = GraphDatabase.driver(uri,
        AuthTokens.basic(neo4JUser, password));
        Session session = driver.session())
    {
      BufferedWriter writer = response.getWriter();

      if (request.getMethod().equals("GET"))
      {
        try
        {
          var recordList = session.executeRead(tx -> tx.run(
              "MATCH (u:User)-[r:COMMENTS]->(m:Movie{movieId: $movieId}) "
                  + "RETURN u.username, r.content, r.rating, r.date",
              Values.parameters("movieId", movieId)).list());

          JsonArray jsonArray = new JsonArray();

          for (var record : recordList) {
            JsonObject recordJson = new JsonObject();

            var username = record.get("u.username");
            var comment = record.get("r.content");
            var rating = record.get("r.rating");
            var date = record.get("r.date");

            recordJson.addProperty("username", username.asString());
            recordJson.addProperty("comment", comment.asString());
            recordJson.addProperty("rating", rating.asInt());
            String dateString = date.asLocalDate().toString();
            recordJson.addProperty("date", dateString);

            jsonArray.add(recordJson);
          }

          JsonObject result = new JsonObject();
          result.add("comments", jsonArray);

          writer.write(result.toString());
          response.setStatusCode(HttpURLConnection.HTTP_OK);
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          writer.write(ex.getMessage());
        }
      }
      else if (request.getMethod().equals("DELETE"))
      {
        try
        {
          session.executeWrite(tx -> tx.run(
              "MATCH (:User {uid: $uid})-[r:COMMENTS]->(:Movie {movieId: $movieId}) "
                  + "DELETE r",
              Values.parameters("uid", uid, "movieId", movieId)));
          response.setStatusCode(HttpURLConnection.HTTP_OK);
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          writer.write(ex.getMessage());
        }
        writer.write(uid + " uncommented " + movieId);
      }
      else
      {
        response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
        writer.write("Something went wrong!");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
