package tobyspring.vol1.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import tobyspring.vol1.domain.Level;
import tobyspring.vol1.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ContextConfiguration(locations = "/applicationContext.xml")
class UserDaoTest {

  @Autowired
  private UserDaoJdbc dao;
  @Autowired
  private DataSource dataSource;

  private User user1;
  private User user2;
  private User user3;


  @BeforeEach
  public void setUp() {
    this.user1 = new User("gyumee", "박성철", "springno1", "a@email.com", Level.BASIC, 1, 0);
    this.user2 = new User("leegw700", "이길원", "springno2", "b@email.com", Level.SILVER, 55, 10);
    this.user3 = new User("bumjin", "박범진", "springno3", "c@email.com", Level.GOLD, 100, 40);
  }

  @Test
  public void addAndGet() throws SQLException, ClassNotFoundException {

    dao.deleteAll();
    assertThat(dao.getCount(), is(0));


    dao.add(user1);
    dao.add(user2);
    assertThat(dao.getCount(), is(2));

    User userGet1 = dao.get(user1.getId());
    checkSameUser(userGet1, user1);

    User userGet2 = dao.get(user2.getId());
    checkSameUser(userGet2, user2);

  }

  @Test
  public void count() throws SQLException {

    dao.deleteAll();
    assertThat(dao.getCount(), is(0));

    dao.add(user1);
    assertThat(dao.getCount(), is(1));

    dao.add(user2);
    assertThat(dao.getCount(), is(2));

    dao.add(user3);
    assertThat(dao.getCount(), is(3));
  }

  @Test
  public void getAll() throws SQLException {
    dao.deleteAll();

    dao.add(user1);
    List<User> users1 = dao.getAll();
    assertThat(users1.size(), is(1));
    checkSameUser(user1, users1.getFirst());

    dao.add(user2);
    List<User> users2 = dao.getAll();
    assertThat(users2.size(), is(2));
    checkSameUser(user1, users2.getFirst());
    checkSameUser(user2, users2.get(1));


    dao.add(user3);
    List<User> users3 = dao.getAll();
    assertThat(users3.size(), is(3));

    checkSameUser(user1, users3.get(1));
    checkSameUser(user2, users3.get(2));
    checkSameUser(user3, users3.get(0));
  }

  private void checkSameUser(User user1, User user2) {
    assertThat(user1.getId(), is(user2.getId()));
    assertThat(user1.getName(), is(user2.getName()));
    assertThat(user1.getPassword(), is(user2.getPassword()));
    assertThat(user1.getLevel(), is(user2.getLevel()));
    assertThat(user1.getLogin(), is(user2.getLogin()));
    assertThat(user1.getRecommend(), is(user2.getRecommend()));
  }

  @Test
  public void getUserFailure() throws SQLException {


    dao.deleteAll();
    assertThat(dao.getCount(), is(0));

    assertThrows(EmptyResultDataAccessException.class, () ->
    {
      dao.get("unknown_id");
    });

  }

  @Test
  public void duplicateKey() {
    dao.deleteAll();

    dao.add(user1);
    assertThrows(DuplicateKeyException.class, () ->
    {
      dao.add(user1);
    });

  }

  @Test
  public void sqlExceptionTranslate() {
    dao.deleteAll();

    try {
      dao.add(user1);
      dao.add(user1);
    } catch (DuplicateKeyException e) {
      SQLException sqlEx = (SQLException) e.getRootCause();
      SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);

      // 예외 변환
      DataAccessException translatedEx = set.translate(null, null, sqlEx);

      // 변환된 예외가 DuplicateKeyException인지 검증
      assertThat(translatedEx, instanceOf(DuplicateKeyException.class));

    }
  }

  @Test
  public void update() {
    dao.deleteAll();

    dao.add(user1); // 수정할 사용자
    dao.add(user2); // 수정하지 않을 사용자

    user1.setName("오민규");
    user1.setPassword("springno6");
    user1.setLevel(Level.GOLD);
    user1.setLogin(1000);
    user1.setRecommend(999);

    dao.update(user1);

    final User user1update = dao.get(user1.getId());
    checkSameUser(user1, user1update);
    final User user2same = dao.get(user2.getId());
    checkSameUser(user2, user2same);

  }

}



