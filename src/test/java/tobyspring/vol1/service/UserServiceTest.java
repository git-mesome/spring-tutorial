package tobyspring.vol1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import tobyspring.vol1.dao.UserDaoJdbc;
import tobyspring.vol1.domain.Level;
import tobyspring.vol1.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static tobyspring.vol1.service.DefaultUserLevelUpgradePolicy.MIN_LOGCOUNT_FOR_SILVER;
import static tobyspring.vol1.service.DefaultUserLevelUpgradePolicy.MIN_RECOMMEND_FOR_GOLD;

@SpringBootTest
@ContextConfiguration(locations = "/applicationContext.xml")
class UserServiceTest {

  @Autowired
  private UserService userService;
  List<User> users;
  @Autowired
  private UserDaoJdbc userDao;
  @Autowired
  private PlatformTransactionManager transactionManager;
  private UserLevelUpgradePolicy upgradePolicy;
  @Autowired
  private MailSender mailSender;

  @BeforeEach
  void setUp() {
    users = List.of(new User("bumjin", "박범진", "p1", "a@email.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
        // silver 승급
        new User("joytouch", "강명성", "p2", "b@email.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
        new User("erwins", "신승한", "p3", "c@email.com", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
        // gold 승급
        new User("madnite1", "이상호", "p4", "d@email.com", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
        new User("green", "오민규", "p5", "e@email.com", Level.GOLD, 100, Integer.MAX_VALUE));

    upgradePolicy = new DefaultUserLevelUpgradePolicy();
    userService.setUserLevelUpgradePolicy(upgradePolicy);
  }

  @Test
  public void upgradeAllOrNothing() throws Exception {
    userDao.deleteAll();

    final UserService testUserService = new TestUserService(users.get(3)
        .getId());
    testUserService.setUserDao(this.userDao);
    testUserService.setTransactionManager(transactionManager);
    testUserService.setUserLevelUpgradePolicy(upgradePolicy);
    testUserService.setMailSender(this.mailSender);

    for (User user : users) {
      userDao.add(user);
    }

    try {
      testUserService.upgradeLevels();
      fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
      e.getMessage();
    }

    checkLevel(users.get(3), false);
  }


  @Test
  @DirtiesContext
  public void upgradeLevels() throws Exception {
    userDao.deleteAll();

    for (User user : users) {
      userDao.add(user);
    }

    MockMailSender mockMailSender = new MockMailSender();
    userService.setMailSender(mockMailSender);

    userService.upgradeLevels();

    checkLevel(users.get(0), false);
    checkLevel(users.get(1), true);
    checkLevel(users.get(2), false);
    checkLevel(users.get(3), true);
    checkLevel(users.get(4), false);

    List<String> request = mockMailSender.getRequest();
    assertThat(request.size()).isEqualTo(2);
    assertThat(request.get(0)).isEqualTo(users.get(1).getEmail());
    assertThat(request.get(1)).isEqualTo(users.get(3).getEmail());


  }

  private void checkLevel(User user, boolean upgraded) {
    final User userUpdate = userDao.get(user.getId());

    if (upgraded) {
      assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel()
          .nextLevel());
    } else {
      assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
    }

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

  static class TestUserService extends UserService {
    private final String failUserId;

    public TestUserService(String failUserId) {
      this.failUserId = failUserId;
    }

    @Override
    protected void upgradeLevels() throws Exception {
      if (failUserId.equals("madnite1")) {
        throw new TestUserServiceException();
      }
      super.upgradeLevels();  // Call the original method

    }

  }


  static class TestUserServiceException extends RuntimeException {
  }

  static class DummyMailSender implements MailSender {

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {

    }
  }

  static class MockMailSender implements MailSender {

    private List<String> requests = new ArrayList<>();

    public List<String> getRequest() {
      return requests;
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
      // 전송 요청 받은 이메일 주소 저장
      requests.add(Objects.requireNonNull(simpleMessage.getTo())[0]);
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }

  }

}