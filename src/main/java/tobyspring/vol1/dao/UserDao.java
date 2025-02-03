package tobyspring.vol1.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import tobyspring.vol1.domain.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
  private JdbcTemplate jdbcTemplate;

  public UserDao() {

  }

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private static RowMapper<User> userRowMapper() {
    return new RowMapper<User>() {
      public User mapRow(final ResultSet rs,
                         final int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        return user;
      }
    };
  }

  // 익명 클래스로 구현
  public void add(final User user) throws SQLException {
    this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
                             user.getId(),
                             user.getName(),
                             user.getPassword());
  }


  public User get(String id) {
    return this.jdbcTemplate.queryForObject("select * from users where id = ?",
                                            new Object[]{id},
                                            this.userRowMapper()
    );
  }

  public List<User> getAll() {
    return this.jdbcTemplate.query("select * from users order by id",
                                   this.userRowMapper());
  }

  public void deleteAll() throws SQLException {
    this.jdbcTemplate.update("delete from users");
  }

  public int getCount() throws SQLException {
    return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
  }

  // lambda
  //  public int getCount() {
  //    return this.jdbcTemplate.query(con -> con.prepareStatement("select count(*) from users"),
  //                                   rs -> rs.next() ? rs.getInt(1) : 0);
  //  }
}
