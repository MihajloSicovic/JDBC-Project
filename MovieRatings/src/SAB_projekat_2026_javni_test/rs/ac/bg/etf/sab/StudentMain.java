package rs.ac.bg.etf.sab;

import rs.ac.bg.etf.sab.tests.TestHandler;
import rs.ac.bg.etf.sab.tests.TestRunner;
import rs.ac.bg.etf.sab.operations.*;
import student.*;

public class StudentMain {
    public static void main(String[] args) throws Exception {
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
        TestRunner.runTests();

        DB.getConnection().close();
    }
}