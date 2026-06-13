package student;

import rs.ac.bg.etf.sab.operations.MoviesOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class sm230029_MoviesOperations implements MoviesOperations {
    @Override
    public Integer addMovie(String title, Integer genreId, String director) {
        String query = "INSERT INTO Movies (title, director) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, title);
            stmt.setString(2, director);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);

                    query = "INSERT INTO MovieGenres (movieId, genreId) VALUES (?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(query)) {

                        ps.setInt(1, newId);
                        ps.setInt(2, genreId);

                        ps.executeUpdate();
                    }
                    catch (SQLException e) {
                        return null;
                    }

                    return newId;
                }
                else return null;
            }
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer updateMovieTitle(Integer id, String title) {
        String query = "UPDATE Movies SET title = ? WHERE idM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setInt(2, id);

            return stmt.executeUpdate() == 1 ? id : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer addGenreToMovie(Integer movieId, Integer genreId) {
        String query = "INSERT INTO MovieGenres (movieId, genreId) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);
            stmt.setInt(2, genreId);

            return stmt.executeUpdate() == 1 ? movieId : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer removeGenreFromMovie(Integer movieId, Integer genreId) {
        String query = "DELETE FROM MovieGenres WHERE movieId = ? AND genreId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);
            stmt.setInt(2, genreId);

            return stmt.executeUpdate() == 1 ? movieId : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer updateMovieDirector(Integer id, String director) {
        String query = "UPDATE Movies SET director = ? WHERE idM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, director);
            stmt.setInt(2, id);

            return stmt.executeUpdate() == 1 ? id : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer removeMovie(Integer id) {
        String query = "DELETE FROM Movies WHERE idM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() == 1 ? id : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public List<Integer> getMovieIds(String title, String director) {
        String query = "SELECT idM FROM Movies WHERE title = ? AND director = ?";
        List<Integer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, director);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(rs.getInt(1));
            }
        }
        catch (SQLException e) {}

        return list;
    }

    @Override
    public List<Integer> getAllMovieIds() {
        String query = "SELECT idM FROM Movies";
        List<Integer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next())
                list.add(rs.getInt(1));
        }
        catch (SQLException e) {}

        return list;
    }

    @Override
    public List<Integer> getMovieIdsByGenre(Integer genreId) {
        String query = "SELECT movieId FROM MovieGenres WHERE genreId = ?";
        List<Integer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, genreId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(rs.getInt(1));
            }
        }
        catch (SQLException e) {}

        return list;
    }

    @Override
    public List<Integer> getGenreIdsForMovie(Integer movieId) {
        String query = "SELECT genreId FROM MovieGenres WHERE movieId = ?";
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

    @Override
    public List<Integer> getMovieIdsByDirector(String director) {
        String query = "SELECT idM FROM Movies WHERE director = ?";
        List<Integer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, director);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(rs.getInt(1));
            }
        }
        catch (SQLException e) {}

        return list;
    }

    @Override
    public String getMovieTrend(Integer movieId) {
        String query = "SELECT status FROM Movies WHERE idM = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString(1);
                else return null;
            }
        }
        catch (SQLException e) {
            return null;
        }
    }
}
