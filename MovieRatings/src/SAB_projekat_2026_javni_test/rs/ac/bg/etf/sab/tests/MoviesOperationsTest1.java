//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rs.ac.bg.etf.sab.tests;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.ac.bg.etf.sab.operations.*;
import student.*;

public class MoviesOperationsTest1 {
    private TestHandler th;
    private GeneralOperations general;
    private UsersOperations users;
    private GenresOperations genres;
    private MoviesOperations movies;
    private RatingsOperations ratings;

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

        this.th = TestHandler.getInstance();
        Assert.assertNotNull(this.th);
        this.general = this.th.getGeneralOperations();
        this.users = this.th.getUsersOperations();
        this.genres = this.th.getGenresOperations();
        this.movies = this.th.getMoviesOperations();
        this.ratings = this.th.getRatingsOperations();
        Assert.assertNotNull(this.general);
        Assert.assertNotNull(this.users);
        Assert.assertNotNull(this.genres);
        Assert.assertNotNull(this.movies);
        Assert.assertNotNull(this.ratings);
        this.general.eraseAll();
    }

    @After
    public void tearDown() {
        this.general.eraseAll();
    }

    @Test
    public void addMovie_AndGetAll() {
        Integer g = this.genres.addGenre("Crime");
        Integer m = this.movies.addMovie("The Godfather", g, "Francis Ford Coppola");
        Assert.assertNotNull(m);
        List<Integer> all = this.movies.getAllMovieIds();
        Assert.assertEquals(1L, (long)all.size());
        Assert.assertTrue(all.contains(m));
        Assert.assertTrue(this.movies.getGenreIdsForMovie(m).contains(g));
    }

    @Test
    public void updateMovieTitle() {
        Integer g = this.genres.addGenre("Sci-Fi");
        Integer m = this.movies.addMovie("Star Wars", g, "George Lucas");
        Assert.assertEquals(m, this.movies.updateMovieTitle(m, "Star Wars: A New Hope"));
        Assert.assertTrue(this.movies.getMovieIds("Star Wars: A New Hope", "George Lucas").contains(m));
        Assert.assertTrue(this.movies.getMovieIds("Star Wars", "George Lucas").isEmpty());
        Assert.assertNull(this.movies.updateMovieTitle((new Random()).nextInt(), "Avatar"));
    }

    @Test
    public void manageGenresForMovie() {
        Integer g1 = this.genres.addGenre("Drama");
        Integer g2 = this.genres.addGenre("Romance");
        Integer m = this.movies.addMovie("Titanic", g1, "James Cameron");
        Assert.assertEquals(m, this.movies.addGenreToMovie(m, g2));
        List<Integer> gs = this.movies.getGenreIdsForMovie(m);
        Assert.assertEquals(2L, (long)gs.size());
        Assert.assertTrue(gs.containsAll(Arrays.asList(g1, g2)));
        Assert.assertNull(this.movies.addGenreToMovie(m, g2));

        int mx;
        for(mx = (new Random()).nextInt(); mx == m; mx = (new Random()).nextInt()) {
        }

        Assert.assertNull(this.movies.addGenreToMovie(mx, g1));
        Assert.assertEquals(m, this.movies.removeGenreFromMovie(m, g2));
        Assert.assertFalse(this.movies.getGenreIdsForMovie(m).contains(g2));
        Assert.assertNull(this.movies.removeGenreFromMovie(m, g2));
    }

    @Test
    public void updateMovieDirector() {
        Integer g = this.genres.addGenre("Thriller");
        Integer m = this.movies.addMovie("Se7en", g, "David Fincher");
        Assert.assertEquals(m, this.movies.updateMovieDirector(m, "David Andrew Leo Fincher"));
        Assert.assertTrue(this.movies.getMovieIdsByDirector("David Andrew Leo Fincher").contains(m));
        Assert.assertTrue(this.movies.getMovieIdsByDirector("David Fincher").isEmpty());

        int mx;
        for(mx = (new Random()).nextInt(); mx == m; mx = (new Random()).nextInt()) {
        }

        Assert.assertNull(this.movies.updateMovieDirector(mx, "X"));
    }

    @Test
    public void removeMovie() {
        Integer g1 = this.genres.addGenre("Adventure");
        Integer g2 = this.genres.addGenre("Action");
        Integer m = this.movies.addMovie("Indiana Jones and the Last Crusade", g1, "Steven Spielberg");
        this.movies.addGenreToMovie(m, g2);
        Assert.assertEquals(m, this.movies.removeMovie(m));
        Assert.assertFalse(this.movies.getAllMovieIds().contains(m));
        Assert.assertFalse(this.movies.getMovieIdsByGenre(g1).contains(m));
        Assert.assertNull(this.movies.removeMovie((new Random()).nextInt()));
    }

    @Test
    public void getMovieIds_ByTitleAndDirector() {
        Integer g = this.genres.addGenre("Horror");
        Integer m1 = this.movies.addMovie("Psycho", g, "Alfred Hitchcock");
        Integer m2 = this.movies.addMovie("Psycho", g, "Gus Van Sant");
        Assert.assertTrue(this.movies.getMovieIds("Psycho", "Alfred Hitchcock").contains(m1));
        Assert.assertTrue(this.movies.getMovieIds("Psycho", "Gus Van Sant").contains(m2));
    }

    @Test
    public void getAllMovieIds_Multiple() {
        Integer g = this.genres.addGenre("Action");
        Integer m1 = this.movies.addMovie("Die Hard", g, "John McTiernan");
        Integer m2 = this.movies.addMovie("Mad Max: Fury Road", g, "George Miller");
        Integer m3 = this.movies.addMovie("The Fifth Element", g, "Luc Besson");
        List<Integer> all = this.movies.getAllMovieIds();
        Assert.assertEquals(3L, (long)all.size());
        Assert.assertTrue(all.containsAll(Arrays.asList(m1, m2, m3)));
    }

    @Test
    public void getMovieIdsByGenre_AndByDirector() {
        Integer g1 = this.genres.addGenre("Fantasy");
        Integer g2 = this.genres.addGenre("Sci-Fi");
        Integer m1 = this.movies.addMovie("The Lord of the Rings", g1, "Peter Jackson");
        Integer m2 = this.movies.addMovie("The Matrix", g2, "Lana Wachowski");
        this.movies.addGenreToMovie(m1, g2);
        Assert.assertTrue(this.movies.getMovieIdsByGenre(g1).contains(m1));
        Assert.assertTrue(this.movies.getMovieIdsByGenre(g2).containsAll(Arrays.asList(m1, m2)));
        Integer g = this.genres.addGenre("Crime");
        Integer m3 = this.movies.addMovie("Goodfellas", g, "Martin Scorsese");
        Integer m4 = this.movies.addMovie("The Irishman", g, "Martin Scorsese");
        Integer m5 = this.movies.addMovie("Pulp Fiction", g, "Quentin Tarantino");
        List<Integer> scorsese = this.movies.getMovieIdsByDirector("Martin Scorsese");
        Assert.assertEquals(2L, (long)scorsese.size());
        Assert.assertTrue(scorsese.containsAll(Arrays.asList(m3, m4)));
        List<Integer> tarantino = this.movies.getMovieIdsByDirector("Quentin Tarantino");
        Assert.assertEquals(1L, (long)tarantino.size());
        Assert.assertTrue(tarantino.contains(m5));
    }

    @Test
    public void getGenreIdsForMovie_ListIntegrity() {
        Integer g1 = this.genres.addGenre("Biography");
        Integer g2 = this.genres.addGenre("History");
        Integer g3 = this.genres.addGenre("War");
        Integer m = this.movies.addMovie("Schindler's List", g1, "Steven Spielberg");
        this.movies.addGenreToMovie(m, g2);
        this.movies.addGenreToMovie(m, g3);
        List<Integer> gs = this.movies.getGenreIdsForMovie(m);
        Assert.assertEquals(3L, (long)gs.size());
        Assert.assertTrue(gs.containsAll(Arrays.asList(g1, g2, g3)));
        this.movies.removeGenreFromMovie(m, g2);
        gs = this.movies.getGenreIdsForMovie(m);
        Assert.assertEquals(2L, (long)gs.size());
        Assert.assertFalse(gs.contains(g2));
    }

    private void addRatingsFromNewUsers(Integer movieId, String usernamePrefix, int count, int score) {
        for(int i = 0; i < count; ++i) {
            Integer u = this.users.addUser(usernamePrefix + i);
            Assert.assertNotNull(u);
            Assert.assertTrue(this.ratings.addRating(u, movieId, score));
        }

    }

    @Test
    public void getMovieTrend_ClassicFallingAndRising() {
        Integer g = this.genres.addGenre("Drama");
        Integer classic = this.movies.addMovie("Cinema Paradiso", g, "Giuseppe Tornatore");
        Integer falling = this.movies.addMovie("Sunset Boulevard", g, "Billy Wilder");
        Integer rising = this.movies.addMovie("The Apartment", g, "Billy Wilder");
        this.addRatingsFromNewUsers(classic, "classic_user_", 5, 8);
        Assert.assertEquals("Classic", this.movies.getMovieTrend(classic));
        this.addRatingsFromNewUsers(falling, "falling_high_user_", 5, 10);
        this.addRatingsFromNewUsers(falling, "falling_low_user_", 5, 1);
        Assert.assertEquals("Falling", this.movies.getMovieTrend(falling));
        this.addRatingsFromNewUsers(rising, "rising_low_user_", 5, 1);
        this.addRatingsFromNewUsers(rising, "rising_high_user_", 5, 10);
        Assert.assertEquals("Rising", this.movies.getMovieTrend(rising));
    }
}
