package storeusersneo4j;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.neo4j.driver.*;

public class StoreUsersNeo4j implements HttpFunction
{
  @Override public void service(HttpRequest httpRequest,
      HttpResponse httpResponse) throws Exception
  {
    // Allow cross-origin requests
    httpResponse.appendHeader("Access-Control-Allow-Origin", "*");
    httpResponse.appendHeader("Access-Control-Allow-Methods", "POST, GET");
    httpResponse.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    // Handle preflight requests (OPTIONS)
    if (httpRequest.getMethod().equals("OPTIONS"))
    {
      httpResponse.setStatusCode(204);
      return;
    }

    // Get the uid and username from the request
    String uid = httpRequest.getFirstQueryParameter("uid").orElse("");
    String username = httpRequest.getFirstQueryParameter("username").orElse("");

    // Neo4j instance
    String uri = System.getenv().get("NEO4J_URI");
    String user = System.getenv().get("NEO4J_USER");
    String password = System.getenv().get("NEO4J_PASSWORD");

    if (httpRequest.getMethod().equals("GET"))
    {
      try (Driver driver = GraphDatabase.driver(uri,
          AuthTokens.basic(user, password)); Session session = driver.session())
      {
        // check if the username already exists
        Result result = session.run(
            "MATCH (u:User {username: $username}) RETURN u",
            Values.parameters("username", username));

        if (result.hasNext())
        {
          // Username already exists
          httpResponse.setStatusCode(409);
          httpResponse.getWriter().write("Username already exists.");
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
        httpResponse.setStatusCode(500);
      }
    }

    if (httpRequest.getMethod().equals("POST"))
    {
      // Execute query - ensure automatic closing of session and the driver
      try (Driver driver = GraphDatabase.driver(uri,
          AuthTokens.basic(user, password)); Session session = driver.session())
      {
        // Create a new user
        session.run("CREATE (:User {uid: $uid, username: $username})",
            Values.parameters("uid", uid, "username", username));

        httpResponse.setStatusCode(200);
        httpResponse.getWriter()
            .write("Username and uid have been stored successfully.");
      }
      catch (Exception e)
      {
        e.printStackTrace();
        httpResponse.setStatusCode(500);
      }
    }
  }
}
