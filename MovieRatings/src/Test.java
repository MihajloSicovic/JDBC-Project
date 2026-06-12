import rs.ac.bg.etf.sab.operations.GeneralOperations;
import rs.ac.bg.etf.sab.operations.GenresOperations;
import student.sm230029_GeneralOperations;
import student.sm230029_GenresOperations;

import java.sql.*;

public class Test {
    public static void main(String[] args) {
        GeneralOperations g = new sm230029_GeneralOperations();
        g.eraseAll();
        String name = "Drama";
        GenresOperations genresOperations = new sm230029_GenresOperations();
        Integer id = genresOperations.addGenre(name);
        System.out.println(id);
    }
}
