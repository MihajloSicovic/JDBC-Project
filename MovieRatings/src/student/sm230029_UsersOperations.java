package student;

import rs.ac.bg.etf.sab.operations.UsersOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class sm230029_UsersOperations implements UsersOperations {
    @Override
    public Integer addUser(String username) {
        String query = "INSERT INTO Users (username) VALUES (?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);

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
    public Integer updateUser(Integer id, String username) {
        String query = "UPDATE Users SET username = ? WHERE idU = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setInt(2, id);

            return stmt.executeUpdate() == 1 ? id : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer removeUser(Integer id) {
        String query = "DELETE FROM Users WHERE idU = ?";

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
    public boolean doesUserExist(String username) {
        String query = "SELECT * FROM Users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Integer getUserId(String username) {
        String query = "SELECT * FROM Users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

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
    public List<Integer> getAllUserIds() {
        String query = "SELECT idU FROM Users";
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
    public List<Integer> getRecommendedMoviesFromFavoriteGenres(Integer userId) {
        return List.of();
    }

    @Override
    public Integer getRewards(Integer userId) {
        return 0;
    }

    @Override
    public List<String> getThematicSpecializations(Integer userId) {
        return List.of();
    }

    @Override
    public String getUserDescription(Integer userId) {
        return "";
    }
}
