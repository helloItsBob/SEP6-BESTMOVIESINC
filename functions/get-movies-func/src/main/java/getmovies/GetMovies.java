package getmovies;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class GetMovies implements HttpFunction
{

  @Override public void service(HttpRequest httpRequest,
      HttpResponse httpResponse) throws Exception
  {
  String tmdbApiKey = System.getenv().get("TMDB_API_KEY");

    String endpoint = httpRequest.getFirstQueryParameter("endpoint").orElse("");
    String apiUrl = "";

    switch (endpoint)
    {
      case "popular" -> apiUrl =
          "https://api.themoviedb.org/3/movie/popular?language=en-US&page=1&api_key="
              + tmdbApiKey;
      case "top_rated" -> apiUrl =
          "https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1&api_key="
              + tmdbApiKey;
      case "upcoming" -> apiUrl =
          "https://api.themoviedb.org/3/movie/upcoming?language=en-US&page=1&api_key="
              + tmdbApiKey;
      default ->
      {
        apiUrl =
            "https://api.themoviedb.org/3/discover/movie?api_key=" + tmdbApiKey
                + "&language=en-US&sort_by=popularity.desc&page=2";
      }
    }

    // HTTP request to TMDB API
    String response = sendGetRequest(apiUrl);

    // Set the response headers (CORS)
    httpResponse.setStatusCode(200);
    httpResponse.setContentType("application/json");
    httpResponse.appendHeader("Access-Control-Allow-Origin", "*");
    httpResponse.appendHeader("Access-Control-Allow-Methods", "GET");

    // Write the response to the output stream
    BufferedWriter writer = new BufferedWriter(httpResponse.getWriter());
    writer.write(response);
    writer.flush();
  }

  private String sendGetRequest(String url) throws IOException
  {
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    try
    {
      URL apiUrl = new URL(url);
      connection = (HttpURLConnection) apiUrl.openConnection();
      connection.setRequestMethod("GET");

      StringBuilder response = new StringBuilder();
      reader = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null)
      {
        response.append(line);
      }
      return response.toString();
    }
    finally
    {
      if (reader != null)
      {
        reader.close();
      }
      if (connection != null)
      {
        connection.disconnect();
      }
    }
  }
}