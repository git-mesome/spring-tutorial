package tobyspring.vol1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import tobyspring.vol1.dao.UserDaoJdbc;
import tobyspring.vol1.domain.Level;
import tobyspring.vol1.domain.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(locations = "/applicationContext.xml")
class UserServiceTest {

  @Autowired
  private UserService userService;
  List<User> users;
  @Autowired
  private UserDaoJdbc userDao;

  @BeforeEach
  void setUp() {
    users = List.of(
//        new User("bumjin", "박범진", "p1", Level.BASIC, 49, 0),
        // silver 승급
        new User("joytouch", "강명성", "p2", Level.BASIC, 50, 0),
//        new User("erwins", "신승한", "p3", Level.SILVER, 60, 29),
        // gold 승급
        new User("madnite1", "이상호", "p4", Level.SILVER, 60, 30)
//        new User("green", "오민규", "p5", Level.GOLD, 100, 100)
    );
  }

  @Test
  public void upgradeLevels() {
    userDao.deleteAll();

    for (User user : users) {
      userDao.add(user);
    }

    userService.upgradeLevels();

//    checkLevel(users.get(0), Level.BASIC);
//    checkLevel(users.get(0), Level.SILVER);
//    checkLevel(users.get(2), Level.SILVER);
    checkLevel(users.get(1), Level.GOLD);
//    checkLevel(users.get(4), Level.GOLD);


  }

  private void checkLevel(User user, Level expectedLevel) {
    final User userUpdate = userDao.get(user.getId());
    assertThat(userUpdate.getLevel())
              .isEqualTo(expectedLevel);
  }

  @Test
  public void add() {
    userDao.deleteAll();

    final User userWithLevel = users.get(4); // GOLD 레벨 지정, 초기화 X
    final User userWithoutLevel = users.get(0); // 레벨이 비어있는 사용자, BASIC 초기화 필요
    userWithoutLevel.setLevel(null);

    userService.add(userWithLevel);
    userService.add(userWithoutLevel);

    final User userWithLevelRead = userDao.get(userWithLevel.getId());
    final User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
    assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel());

  }


}