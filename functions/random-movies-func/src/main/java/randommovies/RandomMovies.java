package randommovies;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class RandomMovies implements HttpFunction
{
  String tmdbApiKey = System.getenv().get("TMDB_API_KEY");

  @Override public void service(HttpRequest httpRequest,
      HttpResponse httpResponse) throws Exception
  {
    String number = httpRequest.getFirstQueryParameter("number").orElse("");

    // Add CORS headers to allow cross-origin requests
    httpResponse.appendHeader("Access-Control-Allow-Origin", "*");
    httpResponse.appendHeader("Access-Control-Allow-Methods", "GET");
    httpResponse.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    try
    {
      String moviesJson = getRandomMovies(Integer.parseInt(number));
      BufferedWriter writer = httpResponse.getWriter();
      writer.write(moviesJson);
      httpResponse.setStatusCode(200);
    }
    catch (Exception e)
    {
      httpResponse.setStatusCode(500);
      BufferedWriter writer = httpResponse.getWriter();
      writer.write("Error: " + e.getMessage());
    }
  }

  private String getRandomMovies(int number) throws IOException
  {
    // generate a random page between 0 - 99
    int randomPage = new Random().nextInt(1, 100);
    String apiUrl = "https://api.themoviedb.org/3/discover/movie" + "?api_key="
        + tmdbApiKey + "&page=" + randomPage;

    URL url = new URL(apiUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder responseBody = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        responseBody.append(line);
      }
      reader.close();

      Gson gson = new Gson();
      JsonObject responseJson = gson.fromJson(responseBody.toString(), JsonObject.class);
      JsonArray movieResults = responseJson.get("results").getAsJsonArray();

      JsonArray randomMovies = new JsonArray();
      int moviesCount = Math.min(number, movieResults.size());
      for (int i = 0; i < moviesCount; i++) {
        JsonObject movie = movieResults.get(i).getAsJsonObject();
        randomMovies.add(movie);
      }

      JsonObject moviesObject = new JsonObject();
      moviesObject.add("results", randomMovies);

      return gson.toJson(moviesObject);
    } else {
      throw new IOException("HTTP request failed with response code: " + responseCode);
    }
  }
}

