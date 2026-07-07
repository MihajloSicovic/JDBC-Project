package student;

import rs.ac.bg.etf.sab.operations.GenresOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class sm230029_GenresOperations implements GenresOperations {
    @Override
    public Integer addGenre(String name) {
        String query = "INSERT INTO Genres (name) VALUES (?)";
        Connection conn = DB.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                else return null;
            }
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer updateGenre(Integer id, String name) {
        String query = "UPDATE Genres SET name = ? WHERE idG = ?";
        Connection conn = DB.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setInt(2, id);

            return stmt.executeUpdate() == 1 ? id : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer removeGenre(Integer id) {
        String query = "DELETE FROM Genres WHERE idG = ?";
        Connection conn = DB.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() == 1 ? id : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public boolean doesGenreExist(String name) {
        String query = "SELECT * FROM Genres WHERE name = ?";
        Connection conn = DB.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Integer getGenreId(String name) {
        String query = "SELECT * FROM Genres WHERE name = ?";
        Connection conn = DB.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                else return null;
            }
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<Integer> getAllGenreIds() {
        String query = "SELECT idG FROM Genres";
        List<Integer> list = new ArrayList<>();
        Connection conn = DB.getConnection();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next())
                list.add(rs.getInt(1));
        }
        catch (SQLException e) {}

        return list;
    }
}
