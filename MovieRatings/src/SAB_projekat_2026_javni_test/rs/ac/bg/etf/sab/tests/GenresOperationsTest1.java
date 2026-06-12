package rs.ac.bg.etf.sab.tests;

import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rs.ac.bg.etf.sab.operations.*;
import student.*;

public class GenresOperationsTest1 {
    private TestHandler testHandler;
    private GeneralOperations generalOperations;
    private GenresOperations genresOperations;

    @Before
    public void setUp() throws Exception {
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
        this.genresOperations = this.testHandler.getGenresOperations();
        Assert.assertNotNull(this.genresOperations);
        this.generalOperations = this.testHandler.getGeneralOperations();
        Assert.assertNotNull(this.generalOperations);
        this.generalOperations.eraseAll();
    }

    @After
    public void tearDown() throws Exception {
        this.generalOperations.eraseAll();
    }

    @Test
    public void addGenre_OnlyOne() {
        String name = "Action";
        Integer id = this.genresOperations.addGenre(name);
        Assert.assertNotNull(id);
        List<Integer> list = this.genresOperations.getAllGenreIds();
        Assert.assertEquals(1L, (long)list.size());
        Assert.assertTrue(list.contains(id));
    }

    @Test
    public void addGenre_DuplicateName() {
        String name = "Comedy";
        Integer id1 = this.genresOperations.addGenre(name);
        Integer id2 = this.genresOperations.addGenre(name);
        Assert.assertNotNull(id1);
        Assert.assertNull(id2);
        List<Integer> list = this.genresOperations.getAllGenreIds();
        Assert.assertEquals(1L, (long)list.size());
        Assert.assertTrue(list.contains(id1));
    }

    @Test
    public void updateGenre_Valid() {
        String name = "Drama";
        Integer id = this.genresOperations.addGenre(name);
        Assert.assertNotNull(id);
        Integer updatedId = this.genresOperations.updateGenre(id, "Historical Drama");
        Assert.assertEquals(id, updatedId);
        Assert.assertTrue(this.genresOperations.doesGenreExist("Historical Drama"));
        Assert.assertFalse(this.genresOperations.doesGenreExist("Drama"));
    }

    @Test
    public void updateGenre_InvalidId() {
        Random random = new Random();
        int fakeId = random.nextInt();
        Integer result = this.genresOperations.updateGenre(fakeId, "Fantasy");
        Assert.assertNull(result);
    }

    @Test
    public void removeGenre_Valid() {
        String name = "Thriller";
        Integer id = this.genresOperations.addGenre(name);
        Assert.assertNotNull(id);
        Integer removedId = this.genresOperations.removeGenre(id);
        Assert.assertEquals(id, removedId);
        Assert.assertEquals(0L, (long)this.genresOperations.getAllGenreIds().size());
    }

    @Test
    public void removeGenre_InvalidId() {
        Random random = new Random();
        int fakeId = random.nextInt();
        Integer removedId = this.genresOperations.removeGenre(fakeId);
        Assert.assertNull(removedId);
    }

    @Test
    public void doesGenreExist_Check() {
        String name = "Horror";
        Assert.assertFalse(this.genresOperations.doesGenreExist(name));
        this.genresOperations.addGenre(name);
        Assert.assertTrue(this.genresOperations.doesGenreExist(name));
    }

    @Test
    public void getGenreId_Valid() {
        String name = "Romance";
        Integer id = this.genresOperations.addGenre(name);
        Assert.assertNotNull(id);
        Integer retrievedId = this.genresOperations.getGenreId(name);
        Assert.assertEquals(id, retrievedId);
    }

    @Test
    public void getGenreId_Invalid() {
        Integer retrievedId = this.genresOperations.getGenreId("NonExisting");
        Assert.assertNull(retrievedId);
    }

    @Test
    public void addMultipleGenres() {
        String g1 = "Sci-Fi";
        String g2 = "Documentary";
        Integer id1 = this.genresOperations.addGenre(g1);
        Integer id2 = this.genresOperations.addGenre(g2);
        List<Integer> list = this.genresOperations.getAllGenreIds();
        Assert.assertEquals(2L, (long)list.size());
        Assert.assertTrue(list.contains(id1));
        Assert.assertTrue(list.contains(id2));
    }
}
