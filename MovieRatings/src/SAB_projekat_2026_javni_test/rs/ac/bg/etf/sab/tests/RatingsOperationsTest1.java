//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rs.ac.bg.etf.sab.tests;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.ac.bg.etf.sab.operations.*;
import student.*;

public class RatingsOperationsTest1 {
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private UsersOperations usersOperations;
    private GenresOperations genresOperations;
    private MoviesOperations moviesOperations;
    private RatingsOperations ratingsOperations;

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
        this.ratingsOperations = this.testHandler.getRatingsOperations();
        Assert.assertNotNull(this.generalOperations);
        Assert.assertNotNull(this.usersOperations);
        Assert.assertNotNull(this.genresOperations);
        Assert.assertNotNull(this.moviesOperations);
        Assert.assertNotNull(this.ratingsOperations);
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
    public void addRating_Valid() {
        Integer u = this.createUser("alice");
        Integer m = this.createMovie("Inception", "Christopher Nolan", "Sci-Fi");
        Assert.assertTrue(this.ratingsOperations.addRating(u, m, 9));
        Assert.assertEquals(Integer.valueOf(9), this.ratingsOperations.getRating(u, m));
    }

    @Test
    public void addRating_Duplicate() {
        Integer u = this.createUser("bob");
        Integer m = this.createMovie("The Matrix", "Lana Wachowski", "Sci-Fi");
        Assert.assertTrue(this.ratingsOperations.addRating(u, m, 10));
        Assert.assertFalse(this.ratingsOperations.addRating(u, m, 10));
    }

    @Test
    public void updateRating_Valid() {
        Integer u = this.createUser("carol");
        Integer m = this.createMovie("The Dark Knight", "Christopher Nolan", "Action");
        this.ratingsOperations.addRating(u, m, 7);
        Assert.assertTrue(this.ratingsOperations.updateRating(u, m, 10));
        Assert.assertEquals(Integer.valueOf(10), this.ratingsOperations.getRating(u, m));
    }

    @Test
    public void updateRating_Invalid() {
        Integer u = this.createUser("dave");
        Integer m = this.createMovie("Interstellar", "Christopher Nolan", "Sci-Fi");
        Assert.assertFalse(this.ratingsOperations.updateRating(u, m, 9));
    }

    @Test
    public void removeRating_Valid() {
        Integer u = this.createUser("eve");
        Integer m = this.createMovie("Pulp Fiction", "Quentin Tarantino", "Crime");
        this.ratingsOperations.addRating(u, m, 10);
        Assert.assertTrue(this.ratingsOperations.removeRating(u, m));
        Assert.assertNull(this.ratingsOperations.getRating(u, m));
    }

    @Test
    public void removeRating_Invalid() {
        Integer u = this.createUser("frank");
        Integer m = this.createMovie("Gladiator", "Ridley Scott", "Drama");
        Assert.assertFalse(this.ratingsOperations.removeRating(u, m));
    }

    @Test
    public void getRatedMoviesByUser_Multiple() {
        Integer u = this.createUser("george");
        Integer m1 = this.createMovie("The Godfather", "Francis Ford Coppola", "Crime");
        Integer m2 = this.createMovie("The Irishman", "Martin Scorsese", "Crime");
        this.ratingsOperations.addRating(u, m1, 9);
        this.ratingsOperations.addRating(u, m2, 7);
        List<Integer> movies = this.ratingsOperations.getRatedMoviesByUser(u);
        Assert.assertEquals(2L, (long)movies.size());
        Assert.assertTrue(movies.contains(m1));
        Assert.assertTrue(movies.contains(m2));
    }

    @Test
    public void getUsersWhoRatedMovie_Multiple() {
        Integer u1 = this.createUser("henry");
        Integer u2 = this.createUser("isabel");
        Integer m = this.createMovie("Goodfellas", "Martin Scorsese", "Crime");
        this.ratingsOperations.addRating(u1, m, 8);
        this.ratingsOperations.addRating(u2, m, 9);
        List<Integer> users = this.ratingsOperations.getUsersWhoRatedMovie(m);
        Assert.assertEquals(2L, (long)users.size());
        Assert.assertTrue(users.contains(u1));
        Assert.assertTrue(users.contains(u2));
    }
}
