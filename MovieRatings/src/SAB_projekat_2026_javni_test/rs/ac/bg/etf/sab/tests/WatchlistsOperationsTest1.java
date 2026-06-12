//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rs.ac.bg.etf.sab.tests;

import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.ac.bg.etf.sab.operations.*;
import student.*;

public class WatchlistsOperationsTest1 {
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private UsersOperations usersOperations;
    private GenresOperations genresOperations;
    private MoviesOperations moviesOperations;
    private WatchlistsOperations watchlistsOperations;

    @Before
    public void setUp() {
        GeneralOperations generalOperations = new sm230029_GeneralOperations();
        GenresOperations genresOperations = new sm230029_GenresOperations();
        MoviesOperations moviesOperations = new sm230029_MoviesOperations();
        RatingsOperations ratingsOperation = new sm230029_RatingsOperations();
        TagsOperations tagsOperations = new sm230029_TagsOperations();
        UsersOperations usersOperations = new sm230029_UsersOperations();
        WatchlistsOperations watchlistsOperations = new sm230029_WatchlistsOperations();

        TestHandler.createInstance(
                genresOperations,
                moviesOperations,
                ratingsOperation,
                tagsOperations,
                usersOperations,
                watchlistsOperations,
                generalOperations);

        this.testHandler = TestHandler.getInstance();
        Assert.assertNotNull(this.testHandler);
        this.generalOperations = this.testHandler.getGeneralOperations();
        this.usersOperations = this.testHandler.getUsersOperations();
        this.genresOperations = this.testHandler.getGenresOperations();
        this.moviesOperations = this.testHandler.getMoviesOperations();
        this.watchlistsOperations = this.testHandler.getWatchlistsOperations();
        Assert.assertNotNull(this.generalOperations);
        Assert.assertNotNull(this.usersOperations);
        Assert.assertNotNull(this.genresOperations);
        Assert.assertNotNull(this.moviesOperations);
        Assert.assertNotNull(this.watchlistsOperations);
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
    }

    private Integer createUser(String username) {
        Integer id = this.usersOperations.addUser(username);
        Assert.assertNotNull(id);
        return id;
    }

    private Integer createMovie(String title, String director, String genreName) {
        Integer gid = this.genresOperations.getGenreId(genreName);
        if (gid == null) {
            gid = this.genresOperations.addGenre(genreName);
        }

        Assert.assertNotNull(gid);
        Integer mid = this.moviesOperations.addMovie(title, gid, director);
        Assert.assertNotNull(mid);
        return mid;
    }

    @Test
    public void addMovieToWatchlist_OnlyOne() {
        Integer u = this.createUser("alice");
        Integer m = this.createMovie("The Godfather", "Francis Ford Coppola", "Crime");
        Assert.assertTrue(this.watchlistsOperations.addMovieToWatchlist(u, m));
        Assert.assertTrue(this.watchlistsOperations.isMovieInWatchlist(u, m));
        List<Integer> movies = this.watchlistsOperations.getMoviesInWatchlist(u);
        Assert.assertEquals(1L, (long)movies.size());
        Assert.assertTrue(movies.contains(m));
    }

    @Test
    public void addMovieToWatchlist_DuplicateRejected() {
        Integer u = this.createUser("bob");
        Integer m = this.createMovie("Pulp Fiction", "Quentin Tarantino", "Crime");
        Assert.assertTrue(this.watchlistsOperations.addMovieToWatchlist(u, m));
        Assert.assertFalse(this.watchlistsOperations.addMovieToWatchlist(u, m));
        List<Integer> movies = this.watchlistsOperations.getMoviesInWatchlist(u);
        Assert.assertEquals(1L, (long)movies.size());
        Assert.assertTrue(movies.contains(m));
    }

    @Test
    public void removeMovieFromWatchlist_Valid() {
        Integer u = this.createUser("carol");
        Integer m = this.createMovie("Inception", "Christopher Nolan", "Sci-Fi");
        this.watchlistsOperations.addMovieToWatchlist(u, m);
        Assert.assertTrue(this.watchlistsOperations.removeMovieFromWatchlist(u, m));
        Assert.assertFalse(this.watchlistsOperations.isMovieInWatchlist(u, m));
        Assert.assertTrue(this.watchlistsOperations.getMoviesInWatchlist(u).isEmpty());
    }

    @Test
    public void removeMovieFromWatchlist_Invalid_NothingThere() {
        Integer u = this.createUser("dave");
        Integer m = this.createMovie("Interstellar", "Christopher Nolan", "Sci-Fi");
        Assert.assertFalse(this.watchlistsOperations.removeMovieFromWatchlist(u, m));
        Assert.assertFalse(this.watchlistsOperations.isMovieInWatchlist(u, m));
        Assert.assertTrue(this.watchlistsOperations.getMoviesInWatchlist(u).isEmpty());
    }

    @Test
    public void isMovieInWatchlist_Checks() {
        Integer u = this.createUser("eve");
        Integer m1 = this.createMovie("Die Hard", "John McTiernan", "Action");
        Integer m2 = this.createMovie("Mad Max: Fury Road", "George Miller", "Action");
        this.watchlistsOperations.addMovieToWatchlist(u, m1);
        Assert.assertTrue(this.watchlistsOperations.isMovieInWatchlist(u, m1));
        Assert.assertFalse(this.watchlistsOperations.isMovieInWatchlist(u, m2));
    }

    @Test
    public void getMoviesInWatchlist_Multiple() {
        Integer u = this.createUser("frank");
        Integer m1 = this.createMovie("The Matrix", "Lana Wachowski", "Sci-Fi");
        Integer m2 = this.createMovie("Blade Runner 2049", "Denis Villeneuve", "Sci-Fi");
        Integer m3 = this.createMovie("The Fifth Element", "Luc Besson", "Sci-Fi");
        this.watchlistsOperations.addMovieToWatchlist(u, m1);
        this.watchlistsOperations.addMovieToWatchlist(u, m2);
        this.watchlistsOperations.addMovieToWatchlist(u, m3);
        List<Integer> movies = this.watchlistsOperations.getMoviesInWatchlist(u);
        Assert.assertEquals(3L, (long)movies.size());
        Assert.assertTrue(movies.contains(m1));
        Assert.assertTrue(movies.contains(m2));
        Assert.assertTrue(movies.contains(m3));
    }

    @Test
    public void getUsersWithMovieInWatchlist_MultipleUsers() {
        Integer u1 = this.createUser("gina");
        Integer u2 = this.createUser("harry");
        Integer u3 = this.createUser("irene");
        Integer m = this.createMovie("Goodfellas", "Martin Scorsese", "Crime");
        this.watchlistsOperations.addMovieToWatchlist(u1, m);
        this.watchlistsOperations.addMovieToWatchlist(u2, m);
        List<Integer> users = this.watchlistsOperations.getUsersWithMovieInWatchlist(m);
        Assert.assertEquals(2L, (long)users.size());
        Assert.assertTrue(users.contains(u1));
        Assert.assertTrue(users.contains(u2));
        Assert.assertFalse(users.contains(u3));
    }

    @Test
    public void crossUserAndMovieQueries() {
        Integer u1 = this.createUser("john");
        Integer u2 = this.createUser("karen");
        Integer m1 = this.createMovie("The Lord of the Rings: The Fellowship of the Ring", "Peter Jackson", "Fantasy");
        Integer m2 = this.createMovie("The Irishman", "Martin Scorsese", "Crime");
        this.watchlistsOperations.addMovieToWatchlist(u1, m1);
        this.watchlistsOperations.addMovieToWatchlist(u1, m2);
        this.watchlistsOperations.addMovieToWatchlist(u2, m1);
        List<Integer> u1Movies = this.watchlistsOperations.getMoviesInWatchlist(u1);
        Assert.assertEquals(2L, (long)u1Movies.size());
        Assert.assertTrue(u1Movies.contains(m1));
        Assert.assertTrue(u1Movies.contains(m2));
        List<Integer> u2Movies = this.watchlistsOperations.getMoviesInWatchlist(u2);
        Assert.assertEquals(1L, (long)u2Movies.size());
        Assert.assertTrue(u2Movies.contains(m1));
        List<Integer> usersForM1 = this.watchlistsOperations.getUsersWithMovieInWatchlist(m1);
        Assert.assertEquals(2L, (long)usersForM1.size());
        Assert.assertTrue(usersForM1.contains(u1));
        Assert.assertTrue(usersForM1.contains(u2));
        List<Integer> usersForM2 = this.watchlistsOperations.getUsersWithMovieInWatchlist(m2);
        Assert.assertEquals(1L, (long)usersForM2.size());
        Assert.assertTrue(usersForM2.contains(u1));
    }
}
