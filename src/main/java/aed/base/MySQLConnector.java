package aed.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector implements DatabaseConnector {
    // Información de conexión codificada directamente
    private static final String URL = "jdbc:mysql://localhost:3306/PRODUCTOS_SAMUEL2";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    @Override
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}



