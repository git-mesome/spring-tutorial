package tobyspring.vol1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import tobyspring.vol1.dao.UserDao;
import tobyspring.vol1.dao.UserDaoJdbc;
import tobyspring.vol1.domain.Level;
import tobyspring.vol1.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static tobyspring.vol1.service.DefaultUserLevelUpgradePolicy.MIN_LOGCOUNT_FOR_SILVER;
import static tobyspring.vol1.service.DefaultUserLevelUpgradePolicy.MIN_RECOMMEND_FOR_GOLD;

@SpringBootTest
@ContextConfiguration(locations = "/applicationContext.xml")
class UserServiceTest {

  @Autowired
  private UserServiceImpl userServiceImpl;
  List<User> users;
  @Autowired
  private UserDaoJdbc userDao;
  @Autowired
  private PlatformTransactionManager transactionManager;
  private UserLevelUpgradePolicy upgradePolicy;
  @Autowired
  private MailSender mailSender;
  @Autowired
  private ApplicationContext applicationContext;

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
    userServiceImpl.setUserLevelUpgradePolicy(upgradePolicy);
  }

  @Test
  public void mockUpgradeLevels() throws Exception {
    UserServiceImpl userService = new UserServiceImpl();

    UserDao mockUserDao = mock(UserDao.class);
    when(mockUserDao.getAll()).thenReturn(users);
    userServiceImpl.setUserDao(mockUserDao);

    MailSender mockMailSender = mock(MailSender.class);
    userServiceImpl.setMailSender(mockMailSender);

    userServiceImpl.upgradeLevels();

    verify(mockUserDao, times(2)).update(any(User.class));
    verify(mockUserDao).update(users.get(1));
    assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
    verify(mockUserDao).update(users.get(3));
    assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

    ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
    verify(mockMailSender, times(2)).send(mailMessageArg.capture());
    List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
    assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
    assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());

  }

  @Test
  @DirtiesContext
  public void upgradeAllOrNothing() throws Exception {

    final TestUserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());

    testUserService.setUserDao(this.userDao);
    testUserService.setUserLevelUpgradePolicy(this.upgradePolicy);
    testUserService.setMailSender(this.mailSender);

    TxProxyFactoryBean txProxyFactoryBean = applicationContext.getBean("&userServiceTx", TxProxyFactoryBean.class);
    txProxyFactoryBean.setTarget(testUserService);

    UserService txUserService = (UserService) txProxyFactoryBean.getObject();

    userDao.deleteAll();

    for (User user : users) {
      userDao.add(user);
    }

    try {
      txUserService.upgradeLevels();
      fail("TestUserServiceException expected");
    } catch (TestUserServiceException e) {
      e.getMessage();
    }

    checkLevelUpgraded(users.get(3), false);
  }

  @Test
  public void upgradeLevels() throws Exception {
    MockUserDao mockUserDao = new MockUserDao(this.users);
    userServiceImpl.setUserDao(mockUserDao);

    // 메일 발송 여부 확인을 위한 목 오브젝트 DI
    MockMailSender mockMailSender = new MockMailSender();
    userServiceImpl.setMailSender(mockMailSender);

    // 테스트 대상 실행
    userServiceImpl.upgradeLevels();

    List<User> updated = mockUserDao.getUpdated();
    assertThat(updated).hasSize(2);
    checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
    checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

    // 목 오브젝트를 이용한 결과 확인
    List<String> request = mockMailSender.getRequest();
    assertThat(request.size()).isEqualTo(2);
    assertThat(request.get(0)).isEqualTo(users.get(1).getEmail());
    assertThat(request.get(1)).isEqualTo(users.get(3).getEmail());


  }

  private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
    assertThat(updated.getId()).isEqualTo(expectedId);
    assertThat(updated.getLevel()).isEqualTo(expectedLevel);
  }

  private void checkLevelUpgraded(User user, boolean upgraded) {
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

    userServiceImpl.add(userWithLevel);
    userServiceImpl.add(userWithoutLevel);

    final User userWithLevelRead = userDao.get(userWithLevel.getId());
    final User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

    assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
    assertThat(userWithoutLevelRead.getLevel()).isEqualTo(userWithoutLevel.getLevel());

  }

  @Component
  static class TestUserServiceImpl extends UserServiceImpl {
    private final String failUserId;

    public TestUserServiceImpl(String failUserId) {
      this.failUserId = failUserId;
    }

    @Override
    public void upgradeLevels() {
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

  static class MockUserDao implements UserDao {
    private List<User> users;
    private List<User> updated = new ArrayList<>();

    private MockUserDao(List<User> users) {
      this.users = users;
    }

    public List<User> getUpdated() {
      return updated;
    }

    @Override
    public void add(User user) {
      throw new UnsupportedOperationException();
    }

    @Override
    public User get(String id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<User> getAll() {
      return this.users;
    }

    @Override
    public void deleteAll() {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getCount() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void update(User user) {
      updated.add(user);
    }
  }

}