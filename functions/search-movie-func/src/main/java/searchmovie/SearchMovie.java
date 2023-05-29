package searchmovie;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SearchMovie implements HttpFunction {

  @Override
  public void service(HttpRequest httpRequest, HttpResponse httpResponse)
      throws IOException {

    String tmdbApiKey = System.getenv().get("TMDB_API_KEY");

    String movieTitle = httpRequest.getFirstQueryParameter("movieTitle").orElse("");

    // To handle search with multiple words, we have to encode the query
    String queryEncoded = URLEncoder.encode(movieTitle, StandardCharsets.UTF_8);

    String apiUrl =
        "https://api.themoviedb.org/3/search/movie?api_key=" + tmdbApiKey
            + "&query=" + queryEncoded;

    if (!movieTitle.isEmpty()) {
      // Make api request and get data
      String responseData = makeApiRequest(apiUrl);

      // Set the response headers
      httpResponse.setStatusCode(200);
      httpResponse.setContentType("application/json");
      httpResponse.appendHeader("Access-Control-Allow-Origin", "*");
      httpResponse.appendHeader("Access-Control-Allow-Methods", "GET");

      // Write the API response to the HTTP response
      PrintWriter writer = new PrintWriter(httpResponse.getWriter());
      writer.write(responseData);
    }
  }

  private String makeApiRequest(String apiUrl) throws IOException {
    URL url = new URL(apiUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    // Read the response from the API
    Scanner scanner = new Scanner(connection.getInputStream());
    StringBuilder responseData = new StringBuilder();
    while (scanner.hasNextLine()) {
      responseData.append(scanner.nextLine());
    }
    scanner.close();

    return responseData.toString();
  }
}