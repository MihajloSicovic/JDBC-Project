package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static final String URL =
            "jdbc:sqlserver://localhost:1433;" +
                    "databaseName=MovieRatings;" +
                    "encrypt=true;" +
                    "integratedSecurity=true;" +
                    "trustServerCertificate=true;";

    private static Connection connection;
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL);
            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return connection;
    }
}
