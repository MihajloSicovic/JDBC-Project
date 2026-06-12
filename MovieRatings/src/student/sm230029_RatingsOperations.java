package student;

import rs.ac.bg.etf.sab.operations.RatingsOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class sm230029_RatingsOperations implements RatingsOperations {
    @Override
    public boolean addRating(Integer userId, Integer movieId, Integer score) {
        String query = "INSERT INTO Ratings (userId, movieId, score) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.setInt(3, score);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean updateRating(Integer userId, Integer movieId, Integer newScore) {
        String query = "UPDATE Ratings SET score = ? WHERE movieId = ? AND userId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, newScore);
            stmt.setInt(2, movieId);
            stmt.setInt(3, userId);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean removeRating(Integer userId, Integer movieId) {
        String query = "DELETE FROM Ratings WHERE movieId = ? AND userId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() == 1;
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Integer getRating(Integer userId, Integer movieId) {
        String query = "SELECT score FROM Ratings WHERE userId = ? AND movieId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);

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
    public List<Integer> getRatedMoviesByUser(Integer userId) {
        String query = "SELECT movieId FROM Ratings WHERE userId = ?";
        List<Integer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(rs.getInt(1));
            }
        }
        catch (SQLException e) {}

        return list;
    }

    @Override
    public List<Integer> getUsersWhoRatedMovie(Integer movieId) {
        String query = "SELECT userId FROM Ratings WHERE movieId = ?";
        List<Integer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(rs.getInt(1));
            }
        }
        catch (SQLException e) {}

        return list;
    }
}
