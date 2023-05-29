package getcastandtrailer;

import static org.mockito.Mockito.when;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.*;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class GetCastAndTrailerTest {
    @Mock
    private HttpRequest request;
    @Mock
    private HttpResponse response;

    private BufferedWriter writerOut;
    private StringWriter responseOut;

    final String movieId = "429"; //"The Good, the Bad and the Ugly" movie

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
    public void getMovieCastAndTrailerTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("movieId")).thenReturn(Optional.of(movieId));

        new GetCastAndTrailer().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().contains("\"title\":\"The Good, the Bad and the Ugly\""));
        assert (responseOut.toString().contains("\"name\":\"Clint Eastwood\"")); //actor
        assert (responseOut.toString().contains("\"type\":\"Trailer\"")); //to see that there is object of type trailer
    }

    @Test
    public void getCastWithEmptyQueryTest() throws Exception {

        new GetCastAndTrailer().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("Movie id cannot be empty."));
    }
}
