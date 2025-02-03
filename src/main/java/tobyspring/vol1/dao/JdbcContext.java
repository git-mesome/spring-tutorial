package tobyspring.vol1.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {
  private DataSource dataSource;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
    Connection c = null;
    PreparedStatement ps = null;

    try {
      c = dataSource.getConnection();

      //변하는 부분
      ps = stmt.makePreparedStatement(c);

      ps.executeUpdate();

    } catch (SQLException e) {
      throw e;
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (SQLException e) {
        }
      }
      if (c != null) {
        try {
          c.close();
        } catch (SQLException e) {
        }
      }
    }

  }

  public void executeSql(final String query) throws SQLException {
    this.workWithStatementStrategy(
        c -> c.prepareStatement(query)
    );
  }

  public void executeSqlWithParams(final String query, Object... params) throws SQLException {
    this.workWithStatementStrategy(c -> {
      PreparedStatement ps = c.prepareStatement(query);

      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }

      return ps;
    });
  }

}