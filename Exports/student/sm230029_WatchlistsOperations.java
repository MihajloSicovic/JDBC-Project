package student;

import rs.ac.bg.etf.sab.operations.WatchlistsOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class sm230029_WatchlistsOperations implements WatchlistsOperations {
    @Override
    public boolean addMovieToWatchlist(Integer userId, Integer movieId) {
        String query = "INSERT INTO Watchlists (movieId, userId) VALUES (?, ?)";

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
    public boolean removeMovieFromWatchlist(Integer userId, Integer movieId) {
        String query = "DELETE FROM Watchlists WHERE movieId = ? AND userId = ?";

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
    public boolean isMovieInWatchlist(Integer userId, Integer movieId) {
        String query = "SELECT * FROM Watchlists WHERE userId = ? AND movieId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<Integer> getMoviesInWatchlist(Integer userId) {
        String query = "SELECT movieId FROM Watchlists WHERE userId = ?";
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
    public List<Integer> getUsersWithMovieInWatchlist(Integer movieId) {
        String query = "SELECT userId FROM Watchlists WHERE movieId = ?";
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
