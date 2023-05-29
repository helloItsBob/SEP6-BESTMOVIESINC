package movielists;

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
public class MovieListsTest {
    @Mock
    private HttpRequest request;
    @Mock
    private HttpResponse response;

    private BufferedWriter writerOut;
    private StringWriter responseOut;
    final String uid = "testuid";
    final String movieId = "123456789";
    final String favList = "favorites";
    final String watchList = "watchlist";
    final String storeAction = "store";
    final String removeAction = "remove";

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
    public void storeFavouriteMovieTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("uid")).thenReturn(Optional.of(uid));
        Mockito.when(request.getFirstQueryParameter("movieId")).thenReturn(Optional.of(movieId));
        Mockito.when(request.getFirstQueryParameter("list")).thenReturn(Optional.of(favList));
        Mockito.when(request.getFirstQueryParameter("action")).thenReturn(Optional.of(storeAction));
        new MovieLists().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("Movie stored successfully!"));
    }

    @Test
    public void removeFavouriteMovieTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("uid")).thenReturn(Optional.of(uid));
        Mockito.when(request.getFirstQueryParameter("movieId")).thenReturn(Optional.of(movieId));
        Mockito.when(request.getFirstQueryParameter("list")).thenReturn(Optional.of(favList));
        Mockito.when(request.getFirstQueryParameter("action")).thenReturn(Optional.of(removeAction));
        new MovieLists().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("Movie removed successfully!"));
    }

    @Test
    public void storeWatchlistMovieTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("uid")).thenReturn(Optional.of(uid));
        Mockito.when(request.getFirstQueryParameter("movieId")).thenReturn(Optional.of(movieId));
        Mockito.when(request.getFirstQueryParameter("list")).thenReturn(Optional.of(watchList));
        Mockito.when(request.getFirstQueryParameter("action")).thenReturn(Optional.of(storeAction));
        new MovieLists().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("Movie stored successfully!"));
    }

    @Test
    public void removeWatchlistMovieTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("uid")).thenReturn(Optional.of(uid));
        Mockito.when(request.getFirstQueryParameter("movieId")).thenReturn(Optional.of(movieId));
        Mockito.when(request.getFirstQueryParameter("list")).thenReturn(Optional.of(watchList));
        Mockito.when(request.getFirstQueryParameter("action")).thenReturn(Optional.of(removeAction));
        new MovieLists().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("Movie removed successfully!"));
    }

    @Test
    public void wrongActionStoreMovieTest() throws Exception {
        Mockito.when(request.getFirstQueryParameter("uid")).thenReturn(Optional.of(uid));
        Mockito.when(request.getFirstQueryParameter("movieId")).thenReturn(Optional.of(movieId));
        Mockito.when(request.getFirstQueryParameter("list")).thenReturn(Optional.of(favList));
        Mockito.when(request.getFirstQueryParameter("action")).thenReturn(Optional.of("wrong action"));

        new MovieLists().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("Invalid action!"));
    }

    @Test
    public void emptyParametersStoreMovieTest() throws Exception {
        new MovieLists().service(request, response);

        writerOut.flush();
        assert (responseOut.toString().equals("An error has occurred!"));
    }
}