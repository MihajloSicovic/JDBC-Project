package student;

import rs.ac.bg.etf.sab.operations.GeneralOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class sm230029_GeneralOperations implements GeneralOperations {

    @Override
    public void eraseAll() {
        String query =
                "DELETE FROM WatchLists;\n" +
                "DELETE FROM MovieTags;\n" +
                "DELETE FROM MovieGenres;\n" +
                "DELETE FROM Ratings;\n" +
                "DELETE FROM Movies;\n" +
                "DELETE FROM Tags;\n" +
                "DELETE FROM Genres;\n" +
                "DELETE FROM Users;\n" +
                "DBCC CHECKIDENT ('Ratings', RESEED, 0);\n" +
                "DBCC CHECKIDENT ('Movies', RESEED, 0);\n" +
                "DBCC CHECKIDENT ('Tags', RESEED, 0);\n" +
                "DBCC CHECKIDENT ('Genres', RESEED, 0);\n" +
                "DBCC CHECKIDENT ('Users', RESEED, 0);";

        Connection conn = DB.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false);

            stmt.executeUpdate();
            conn.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {}
        }
        finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {}
        }
    }
}
