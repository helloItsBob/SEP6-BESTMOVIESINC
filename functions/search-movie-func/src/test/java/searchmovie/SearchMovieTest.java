package searchmovie;

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
public class SearchMovieTest {
    @Mock
    private HttpRequest request;
    @Mock
    private HttpResponse response;

    private BufferedWriter writerOut;
    private StringWriter responseOut;

    final String movieTitle = "Domicile conjugal";
    final String partOfMovieTitle = "conjugal";
    final String wrongMovieTitle = "Domigile confugal";

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
    public void getMovieByTitleTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("movieTitle")).thenReturn(Optional.of(movieTitle));

        new SearchMovie().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().contains("\"original_title\":\"Domicile conjugal\""));
    }

    @Test
    public void getMovieByPartOfTitleTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("movieTitle")).thenReturn(Optional.of(partOfMovieTitle));

        new SearchMovie().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().contains("\"original_title\":\"Domicile conjugal\""));
    }

    @Test
    public void getMovieByWrongTitleTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("movieTitle")).thenReturn(Optional.of(wrongMovieTitle));

        new SearchMovie().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("{\"page\":1,\"results\":[],\"total_pages\":1,\"total_results\":0}"));
    }
}