package tobyspring.vol1.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tobyspring.vol1.dao.UserDao;
import tobyspring.vol1.domain.Level;
import tobyspring.vol1.domain.User;

import java.util.List;
import java.util.Properties;

public class UserService {

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
    Properties props = new Properties();
    props.put("mail.smtp.host", "mail.ksug.org");
    Session session = Session.getDefaultInstance(props, null);

    MimeMessage message = new MimeMessage(session);
    try {
      message.setFrom(new InternetAddress("useradmin@ksug.org"));
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
      message.setSubject("Upgrade 안내");
      message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다.");
    } catch (AddressException e) {
      throw new RuntimeException(e);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }

  }


  public void add(final User user) {
    if (user.getLevel() == null) {
      user.setLevel(Level.BASIC);
    }
    userDao.add(user);
  }

}
