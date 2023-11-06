package aed.base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Clase para conectar con SQL Server
public class SQLServerConnector implements DatabaseConnector {
 // Detalles de conexión a SQL Server
 private static final String URL = "jdbc:sqlserver://:1433;databaseName=PRODCUTOS_SAMUEL2";
 private static final String USERNAME = "alu";
 private static final String PASSWORD = "alu";
 
 @Override
 public Connection connect() throws SQLException {
     // Carga el driver JDBC, solo necesario para versiones de Java anteriores a la 6
     try {
         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
     } catch (ClassNotFoundException e) {
         throw new SQLException("Driver JDBC para SQL Server no encontrado", e);
     }
     return DriverManager.getConnection(URL, USERNAME, PASSWORD);
 }
}
