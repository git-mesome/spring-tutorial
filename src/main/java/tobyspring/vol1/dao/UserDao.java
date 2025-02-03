package tobyspring.vol1.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import tobyspring.vol1.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
  private DataSource dataSource;
  private JdbcContext jdbcContext;

  public UserDao() {

  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcContext = new JdbcContext();
    this.jdbcContext.setDataSource(dataSource);
    this.dataSource = dataSource;
  }

//  public void setJdbcContext(JdbcContext jdbcContext) {
//    this.jdbcContext = jdbcContext;
//  }


  // 로컬 클래스로 구현
  public void addLocalClass(final User user) throws SQLException {
    class AddStatement implements StatementStrategy {

      @Override
      public PreparedStatement makePreparedStatement(final Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement(
            "insert into users(id, name, password) values(?,?,?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());
        return ps;
      }

    }

    StatementStrategy st = new AddStatement();
//    jdbcContextWithStatementStrategy(st);
  }

  // 익명 클래스로 구현
  public void add(final User user) throws SQLException {

    this.jdbcContext.executeSqlWithParams
        ("insert into users(id, name, password) values(?,?,?)",
            user.getId(), user.getName(), user.getPassword()
        );
  }


  private void extracted(final User user) throws SQLException {

  }


  public User get(String id) throws ClassNotFoundException, SQLException {
    Connection c = this.dataSource.getConnection();
    PreparedStatement ps = c
        .prepareStatement("select * from users where id = ?");
    ps.setString(1, id);

    ResultSet rs = ps.executeQuery();

    User user = null;
    if (rs.next()) {
      user = new User();
      user.setId(rs.getString("id"));
      user.setName(rs.getString("name"));
      user.setPassword(rs.getString("password"));
    }

    rs.close();
    ps.close();
    c.close();

    if (user == null) {
      throw new EmptyResultDataAccessException(1);
    }

    return user;
  }

  public void deleteAll() throws SQLException {
    this.jdbcContext.executeSql("delete from users");

  }

  public int getCount() throws SQLException {
    Connection c = dataSource.getConnection();
    PreparedStatement ps = c.prepareStatement("select count(*) from users");

    ResultSet rs = ps.executeQuery();
    rs.next();
    int count = rs.getInt(1);

    rs.close();
    ps.close();
    c.close();

    return count;
  }


}
