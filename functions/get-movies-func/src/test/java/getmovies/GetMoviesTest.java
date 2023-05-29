package getmovies;

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
public class GetMoviesTest {
    @Mock
    private HttpRequest request;
    @Mock
    private HttpResponse response;

    private BufferedWriter writerOut;
    private StringWriter responseOut;

    //filter strings
    final String popular = "popular";
    final String topRated = "top_rated";
    final String upcoming = "upcoming";

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
    public void getNoQueryAttributesDefaultMoviesTest() throws Exception {

        new GetMovies().service(request, response);

        writerOut.flush();
        assert (!responseOut.toString().isEmpty());
        assert (responseOut.toString().contains("\"page\":2"));
    }

    @Test
    public void getPopularMoviesTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("endpoint")).thenReturn(Optional.of(popular));

        new GetMovies().service(request, response);

        writerOut.flush();
        assert (!responseOut.toString().isEmpty());
        assert (responseOut.toString().contains("\"page\":1"));
    }

    @Test
    public void getTopRatedMoviesTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("endpoint")).thenReturn(Optional.of(topRated));

        new GetMovies().service(request, response);

        writerOut.flush();
        assert (!responseOut.toString().isEmpty());
        assert (responseOut.toString().contains("\"page\":1"));
    }

    @Test
    public void getUpcomingMoviesTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("endpoint")).thenReturn(Optional.of(upcoming));

        new GetMovies().service(request, response);

        writerOut.flush();
        assert (!responseOut.toString().isEmpty());
        assert (responseOut.toString().contains("\"page\":1"));
    }
}
