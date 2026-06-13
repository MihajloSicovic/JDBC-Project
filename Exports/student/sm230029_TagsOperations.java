package student;

import rs.ac.bg.etf.sab.operations.TagsOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class sm230029_TagsOperations implements TagsOperations {
    @Override
    public Integer addTag(Integer movieId, String tag) {
        String query = "SELECT idT FROM Tags WHERE name = ?";
        int tagId;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tag);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) tagId = rs.getInt(1);
                else {
                    query = "INSERT INTO Tags (name) VALUES (?)";
                    try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                        ps.setString(1, tag);

                        ps.executeUpdate();

                        try (ResultSet rs1 = ps.getGeneratedKeys()) {
                            if (rs1.next()) tagId = rs1.getInt(1);
                            else return null;
                        }
                    }
                    catch (SQLException e) {
                        return null;
                    }
                }
            }
        }
        catch (SQLException e) {
            return null;
        }

        query = "INSERT INTO MovieTags (movieId, tagId) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, movieId);
            ps.setInt(2, tagId);

            return ps.executeUpdate() == 1 ? tagId : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Integer removeTag(Integer movieId, String tag) {
        String query = "DELETE FROM MovieTags WHERE movieId = ? AND " +
                "(SELECT name FROM Tags WHERE tagId = idT) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);
            stmt.setString(2, tag);

            return stmt.executeUpdate() == 1 ? movieId : null;
        }
        catch (SQLException e) {
            return null;
        }
    }

    @Override
    public int removeAllTagsForMovie(Integer movieId) {
        String query = "DELETE FROM MovieTags WHERE movieId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);

            return stmt.executeUpdate();
        }
        catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public boolean hasTag(Integer movieId, String tag) {
        String query = "SELECT * FROM MovieTags mt WHERE movieId = ? AND " +
                "(SELECT name FROM Tags t WHERE mt.tagId = t.idT) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);
            stmt.setString(2, tag);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<String> getTagsForMovie(Integer movieId) {
        String query = "SELECT name FROM Tags t WHERE " +
                "(SELECT movieId FROM MovieTags mt WHERE mt.tagId = t.idT) = ?";
        List<String> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, movieId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(rs.getString(1));
            }
        }
        catch (SQLException e) {}

        return list;
    }

    @Override
    public List<Integer> getMovieIdsByTag(String tag) {
        String query = "SELECT movieId FROM MovieTags WHERE " +
                "(SELECT name FROM Tags WHERE tagId = idT) = ?";
        List<Integer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, tag);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    list.add(rs.getInt(1));
            }
        }
        catch (SQLException e) {}

        return list;
    }

    @Override
    public List<String> getAllTags() {
        String query = "SELECT name FROM Tags";
        List<String> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next())
                list.add(rs.getString(1));
        }
        catch (SQLException e) {}

        return list;
    }
}
