package getcastandtrailer;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class GetCastAndTrailer implements HttpFunction
{
  public static String getTMDBApiKey()
  {
    Map<String, String> env = System.getenv();
    return env.get("TMDB_API_KEY");
  }

  @Override public void service(HttpRequest httpRequest,
      HttpResponse httpResponse) throws Exception
  {
    // Add CORS headers to allow cross-origin requests
    httpResponse.appendHeader("Access-Control-Allow-Origin", "*");
    httpResponse.appendHeader("Access-Control-Allow-Methods", "GET");
    httpResponse.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    String tmdbApiKey = getTMDBApiKey();
    String movieId = httpRequest.getFirstQueryParameter("movieId").orElse("");

    if (movieId.isEmpty())
    {
      httpResponse.setStatusCode(400);
      httpResponse.getWriter().write("Movie id cannot be empty.");
      return;
    }

    String apiUrlCastAndTrailer =
        "https://api.themoviedb.org/3/movie/" + movieId + "?api_key="
            + tmdbApiKey + "&append_to_response=videos,credits";

    HttpURLConnection connection = null;

    try
    {
      // Create URL object with the API URL
      URL url = new URL(apiUrlCastAndTrailer);

      // Open connection
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      // Get the response code
      int responseCode = connection.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK)
      {
        // Read the response content
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null)
        {
          responseBuilder.append(line);
        }

        reader.close();

        // Process the JSON response
        String response = responseBuilder.toString();

        // Return the response as the HTTP response
        httpResponse.setStatusCode(200);
        httpResponse.getWriter().write(response);
      }
      else
      {
        httpResponse.setStatusCode(responseCode);
        httpResponse.getWriter()
            .write("Failed to retrieve movie details from the TMDb API.");
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      httpResponse.setStatusCode(500);
      httpResponse.getWriter().write("Internal Server Error.");
    }
    finally
    {
      if (connection != null)
      {
        connection.disconnect();
      }
    }
  }
}


