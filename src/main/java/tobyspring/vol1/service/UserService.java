package tobyspring.vol1.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tobyspring.vol1.dao.UserDao;
import tobyspring.vol1.domain.Level;
import tobyspring.vol1.domain.User;

import java.util.List;

public class UserService {

  private UserDao userDao;
  private UserLevelUpgradePolicy upgradePolicy;
  private PlatformTransactionManager transactionManager;
  private MailSender mailSender;

  public void setUserDao(final UserDao userDao) {
    this.userDao = userDao;
  }

  public void setUserLevelUpgradePolicy(final UserLevelUpgradePolicy upgradePolicy) {
    this.upgradePolicy = upgradePolicy;
  }

  public void setTransactionManager(final PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  public void setMailSender(final MailSender mailSender) {
    this.mailSender = mailSender;
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
          sendUpgradeEmail(user);
        }

      }
      this.transactionManager.commit(status);

    } catch (Exception e) {
      this.transactionManager.rollback(status);
      throw e;
    }

  }

  // 한글 인코딩 생략
  private void sendUpgradeEmail(User user) {

    SimpleMailMessage mailMessage = new SimpleMailMessage();

    mailMessage.setTo(user.getEmail());
    mailMessage.setFrom("useradmin@ksug.org");
    mailMessage.setSubject("Upgrade 안내");
    mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다.");

    this.mailSender.send(mailMessage);
  }


  public void add(final User user) {
    if (user.getLevel() == null) {
      user.setLevel(Level.BASIC);
    }
    userDao.add(user);
  }

}
