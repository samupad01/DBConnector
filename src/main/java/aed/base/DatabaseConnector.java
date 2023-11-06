package aed.base;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnector {
    Connection connect() throws SQLException;
}

