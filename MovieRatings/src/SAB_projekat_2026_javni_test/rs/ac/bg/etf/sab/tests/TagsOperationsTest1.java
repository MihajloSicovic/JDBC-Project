//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rs.ac.bg.etf.sab.tests;

import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.ac.bg.etf.sab.operations.*;
import student.*;

public class TagsOperationsTest1 {
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private GenresOperations genresOperations;
    private MoviesOperations moviesOperations;
    private TagsOperations tagsOperations;

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
        this.genresOperations = this.testHandler.getGenresOperations();
        this.moviesOperations = this.testHandler.getMoviesOperations();
        this.tagsOperations = this.testHandler.getTagsOperations();
        Assert.assertNotNull(this.generalOperations);
        Assert.assertNotNull(this.genresOperations);
        Assert.assertNotNull(this.moviesOperations);
        Assert.assertNotNull(this.tagsOperations);
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() {
        this.generalOperations.eraseAll();
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
    public void addTag_ValidAndDuplicate() {
        Integer m = this.createMovie("Inception", "Christopher Nolan", "Sci-Fi");
        Integer res1 = this.tagsOperations.addTag(m, "mind-bending");
        Assert.assertEquals(m, res1);
        Integer res2 = this.tagsOperations.addTag(m, "mind-bending");
        Assert.assertNull(res2);
        Assert.assertTrue(this.tagsOperations.hasTag(m, "mind-bending"));
        List<String> tags = this.tagsOperations.getTagsForMovie(m);
        Assert.assertEquals(1L, (long)tags.size());
        Assert.assertTrue(tags.contains("mind-bending"));
        List<Integer> moviesByTag = this.tagsOperations.getMovieIdsByTag("mind-bending");
        Assert.assertEquals(1L, (long)moviesByTag.size());
        Assert.assertTrue(moviesByTag.contains(m));
    }

    @Test
    public void addTag_NonExistingMovie() {
        Integer fakeMovieId = (new Random()).nextInt();
        Integer res = this.tagsOperations.addTag(fakeMovieId, "ghost");
        Assert.assertNull(res);
        Assert.assertFalse(this.tagsOperations.hasTag(fakeMovieId, "ghost"));
        Assert.assertTrue(this.tagsOperations.getTagsForMovie(fakeMovieId).isEmpty());
        Assert.assertTrue(this.tagsOperations.getMovieIdsByTag("ghost").isEmpty());
    }

    @Test
    public void removeTag_ValidAndInvalid() {
        Integer m = this.createMovie("Pulp Fiction", "Quentin Tarantino", "Crime");
        this.tagsOperations.addTag(m, "nonlinear");
        this.tagsOperations.addTag(m, "cult");
        Integer rem1 = this.tagsOperations.removeTag(m, "cult");
        Assert.assertEquals(m, rem1);
        Assert.assertFalse(this.tagsOperations.hasTag(m, "cult"));
        Integer rem2 = this.tagsOperations.removeTag(m, "cult");
        Assert.assertNull(rem2);
        Assert.assertTrue(this.tagsOperations.hasTag(m, "nonlinear"));
        List<String> tags = this.tagsOperations.getTagsForMovie(m);
        Assert.assertEquals(1L, (long)tags.size());
        Assert.assertTrue(tags.contains("nonlinear"));
    }

    @Test
    public void removeAllTagsForMovie_CountAndEffects() {
        Integer m = this.createMovie("The Godfather", "Francis Ford Coppola", "Crime");
        this.tagsOperations.addTag(m, "mafia");
        this.tagsOperations.addTag(m, "classic");
        this.tagsOperations.addTag(m, "oscar-winner");
        int removed = this.tagsOperations.removeAllTagsForMovie(m);
        Assert.assertEquals(3L, (long)removed);
        Assert.assertFalse(this.tagsOperations.hasTag(m, "mafia"));
        Assert.assertTrue(this.tagsOperations.getTagsForMovie(m).isEmpty());
        Assert.assertEquals(0L, (long)this.tagsOperations.removeAllTagsForMovie(m));
    }

    @Test
    public void getMovieIdsByTag_MultipleMovies() {
        Integer m1 = this.createMovie("The Matrix", "Lana Wachowski", "Sci-Fi");
        Integer m2 = this.createMovie("The Matrix Reloaded", "Lana Wachowski", "Sci-Fi");
        Integer m3 = this.createMovie("Blade Runner 2049", "Denis Villeneuve", "Sci-Fi");
        this.tagsOperations.addTag(m1, "cyberpunk");
        this.tagsOperations.addTag(m2, "cyberpunk");
        this.tagsOperations.addTag(m3, "neo-noir");
        List<Integer> cyberpunkMovies = this.tagsOperations.getMovieIdsByTag("cyberpunk");
        Assert.assertEquals(2L, (long)cyberpunkMovies.size());
        Assert.assertTrue(cyberpunkMovies.contains(m1));
        Assert.assertTrue(cyberpunkMovies.contains(m2));
        List<Integer> noirMovies = this.tagsOperations.getMovieIdsByTag("neo-noir");
        Assert.assertEquals(1L, (long)noirMovies.size());
        Assert.assertTrue(noirMovies.contains(m3));
    }

    @Test
    public void getAllTags_DistinctAcrossMovies() {
        Integer m1 = this.createMovie("Interstellar", "Christopher Nolan", "Sci-Fi");
        Integer m2 = this.createMovie("Mad Max: Fury Road", "George Miller", "Action");
        this.tagsOperations.addTag(m1, "space");
        this.tagsOperations.addTag(m1, "epic");
        this.tagsOperations.addTag(m2, "epic");
        this.tagsOperations.addTag(m2, "practical-effects");
        List<String> allTags = this.tagsOperations.getAllTags();
        Assert.assertTrue(allTags.contains("space"));
        Assert.assertTrue(allTags.contains("epic"));
        Assert.assertTrue(allTags.contains("practical-effects"));
        Assert.assertEquals(3L, (long)allTags.size());
    }

    @Test
    public void removeTag_NonExistingMovie() {
        Integer fakeMovieId = (new Random()).nextInt();
        Integer rem = this.tagsOperations.removeTag(fakeMovieId, "nothing");
        Assert.assertNull(rem);
        Assert.assertTrue(this.tagsOperations.getTagsForMovie(fakeMovieId).isEmpty());
    }
}
