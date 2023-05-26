package retreivelists;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class RetrieveLists implements HttpFunction
{
  @Override public void service(HttpRequest httpRequest,
      HttpResponse httpResponse) throws Exception
  {
    String uri = System.getenv().get("NEO4J_URI");
    String user = System.getenv().get("NEO4J_USER");
    String password = System.getenv().get("NEO4J_PASSWORD");

    String tmdbApiKey = System.getenv().get("TMDB_API_KEY");

    String list = httpRequest.getFirstQueryParameter("list").orElse("");
    String uid = httpRequest.getFirstQueryParameter("uid").orElse("");

    // Add CORS headers to allow cross-origin requests
    httpResponse.appendHeader("Access-Control-Allow-Origin", "*");
    httpResponse.appendHeader("Access-Control-Allow-Methods", "GET");
    httpResponse.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    try (Driver driver = GraphDatabase.driver(uri,
        AuthTokens.basic(user, password)))
    {
      try (Session session = driver.session())
      {
        Result result = null;

        if (list.equals("favorites"))
        {
          // Retrieve stored favorite movie IDs
          result = session.run(
              "MATCH (:User {uid: $uid})-[:FAVORITES]->(m:Movie) RETURN m.movieId AS movieId",
              Values.parameters("uid", uid));
        }
        else if (list.equals("watchlist"))
        {
          // Retrieve stored watchlist movie IDs
          result = session.run(
              "MATCH (:User {uid: $uid})-[:WATCHLIST]->(m:Movie) RETURN m.movieId AS movieId",
              Values.parameters("uid", uid));
        }

        // Process results and retrieve movies from Tmdb
        JsonObject moviesObject = new JsonObject();
        JsonArray resultsArray = new JsonArray();

        if (result != null)
        {
          while (result.hasNext()) {
            Record record = result.next();
            int movieId = record.get("movieId").asInt();
            String apiUrl = getApiUrl(movieId, tmdbApiKey);
            JsonObject movieData = makeApiRequest(apiUrl);
            resultsArray.add(movieData);
          }
        }

        moviesObject.add("results", resultsArray);

        // Set the response content
        String moviesJson = moviesObject.toString();
        httpResponse.getWriter().write(moviesJson);
        httpResponse.setStatusCode(200);
      }
    }
    catch (Exception e)
    {
      httpResponse.getWriter().write("Error: " + e.getMessage());
      httpResponse.setStatusCode(500);
    }
  }

  private JsonObject makeApiRequest(String apiUrl)
  {
    HttpURLConnection connection = null;
    try
    {
      URL url = new URL(apiUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK)
      {
        String responseBody;
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream())))
        {
          responseBody = reader.lines().collect(Collectors.joining());
        }

        // Process the response
        Gson gson = new Gson();
        return gson.fromJson(responseBody, JsonObject.class);
      }
      else
      {
        throw new IOException(
            "HTTP request failed with response code: " + responseCode);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (connection != null)
      {
        connection.disconnect();
      }
    }
    return null;
  }

  private String getApiUrl(int movieId, String tmdbApiKey)
  {
    return "https://api.themoviedb.org/3/movie/" + movieId + "?api_key="
        + tmdbApiKey;
  }
}

