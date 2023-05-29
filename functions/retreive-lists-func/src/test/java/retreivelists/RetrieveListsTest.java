package retreivelists;

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
public class RetrieveListsTest {
    @Mock
    private HttpRequest request;
    @Mock
    private HttpResponse response;

    private BufferedWriter writerOut;
    private StringWriter responseOut;
    final String uid = "testListuid";
    final String favList = "favorites";
    final String watchList = "watchlist";

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
    public void retrieveFavouriteMovieTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("uid")).thenReturn(Optional.of(uid));
        Mockito.when(request.getFirstQueryParameter("list")).thenReturn(Optional.of(favList));
        new RetrieveLists().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().contains("Domicile conjugal")); //check to contain exact movies
        assert (responseOut.toString().contains("Murder Ahoy"));
    }

    @Test
    public void retrieveWatchlistTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("uid")).thenReturn(Optional.of(uid));
        Mockito.when(request.getFirstQueryParameter("list")).thenReturn(Optional.of(watchList));

        new RetrieveLists().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().contains("Domicile conjugal")); //check to contain exact movie
        assert (responseOut.toString().contains("Sissi - Die junge Kaiserin"));
    }


    @Test
    public void wrongListRetrieveListsTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("uid")).thenReturn(Optional.of(uid));
        Mockito.when(request.getFirstQueryParameter("list")).thenReturn(Optional.of("not existent list"));

        new RetrieveLists().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("{\"results\":[]}"));
    }
}