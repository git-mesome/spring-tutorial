package tobyspring.vol1.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DConnectionMaker implements ConnectionMaker {

  public Connection makeConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.postgresql.Driver");

    return DriverManager.getConnection(
        "jdbc:postgresql://127.0.0.1:15431/springbook", "postgres", "1LOls91VintrU45");
  }

}
