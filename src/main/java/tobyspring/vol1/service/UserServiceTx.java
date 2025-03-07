package tobyspring.vol1.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tobyspring.vol1.domain.User;

public class UserServiceTx implements UserService {

  private UserService userService;
  private PlatformTransactionManager transactionManager;

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setTransactionManager(final PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  // 메소드 구현과 위임
  @Override
  public void add(User user) {
    this.userService.add(user);
  }

  // 메소드 구현
  @Override
  public void upgradeLevels()  {
    // 부가기능
    TransactionStatus status =
        this.transactionManager.getTransaction(new DefaultTransactionDefinition());

    try {
      // 메소드 위임
      userService.upgradeLevels();
      // 이하 부가기능
      this.transactionManager.commit(status);

    } catch (Exception e) {
      this.transactionManager.rollback(status);
      throw e;
    }
  }

  public UserService getUserService() {
    return userService;
  }
}
