package tobyspring.vol1.service;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tobyspring.vol1.dao.UserDao;
import tobyspring.vol1.domain.Level;
import tobyspring.vol1.domain.User;

import javax.sql.DataSource;
import java.util.List;

public class UserService  {

  private UserDao userDao;
  private UserLevelUpgradePolicy upgradePolicy;
  private PlatformTransactionManager transactionManager;

  public void setUserDao(final UserDao userDao) {
    this.userDao = userDao;
  }

  public void setUserLevelUpgradePolicy(final UserLevelUpgradePolicy upgradePolicy) {
    this.upgradePolicy = upgradePolicy;
  }

  public void setTransactionManager(final PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  protected void upgradeLevels() throws Exception {

    TransactionStatus status =
        this.transactionManager.getTransaction(new DefaultTransactionDefinition());

    try {
      final List<User> users = userDao.getAll();

      for (final User user : users) {

        if (upgradePolicy.canUpgradeLevel(user)) {
          upgradePolicy.upgradeLevels(user);
          userDao.update(user);
        }

      }
      this.transactionManager.commit(status);

    }catch (Exception e){
      this.transactionManager.rollback(status);
      throw e;
    }
  }


  public void add(final User user) {
    if (user.getLevel() == null) {
      user.setLevel(Level.BASIC);
    }
    userDao.add(user);
  }

}
