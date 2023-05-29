package storeusersneo4j;

import static org.mockito.Mockito.when;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class StoreUsersNeo4jTest {
    @Mock
    private HttpRequest request;
    @Mock
    private HttpResponse response;

    private BufferedWriter writerOut;
    private StringWriter responseOut;
    final String uid = "testuid";
    final String username = "test user";
    final String notExistentUsername = "654as654d65as4d65as46f54g65dg24xc43f45d6s4f9ds1fs2sd98";

    @Before
    public void beforeTest() throws IOException {
        MockitoAnnotations.openMocks(this);

        // use an empty string as the default request content
        BufferedReader reader = new BufferedReader(new StringReader(""));
        when(request.getReader()).thenReturn(reader);

        responseOut = new StringWriter();
        writerOut = new BufferedWriter(responseOut);
        when(response.getWriter()).thenReturn(writerOut);
    }

    @Test
    public void checkTakenUsernameTest() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getFirstQueryParameter("username")).thenReturn(Optional.of(username));

        new StoreUsersNeo4j().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("Username already exists."));
    }

    @Test
    public void checkNotTakenUsernameTest() throws Exception {
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getFirstQueryParameter("username")).thenReturn(Optional.of(notExistentUsername));

        new StoreUsersNeo4j().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().isEmpty());
    }

    //Test is working, but creating redundant users due to Neo4j specifics (constraint limitations in free version)
//    @Test
//    public void storeUserTest() throws Exception {
//        Mockito.when(request.getMethod()).thenReturn("POST");
//        Mockito.when(request.getFirstQueryParameter("username")).thenReturn(Optional.of(username));
//        Mockito.when(request.getFirstQueryParameter("uid")).thenReturn(Optional.of(uid));
//
//        new StoreUsersNeo4j().service(request, response);
//
//        writerOut.flush();
//        assert (responseOut.toString().equals("Username and uid have been stored successfully."));
//    }

}
