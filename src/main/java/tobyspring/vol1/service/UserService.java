package tobyspring.vol1.service;

import tobyspring.vol1.dao.UserDao;
import tobyspring.vol1.domain.Level;
import tobyspring.vol1.domain.User;

import java.util.List;

public class UserService  {

  UserDao userDao;
  UserLevelUpgradePolicy upgradePolicy;

  public UserService (final UserDao userDao, final UserLevelUpgradePolicy upgradePolicy) {
    this.userDao = userDao;
    this.upgradePolicy = upgradePolicy;
  }

  public void upgradeLevels() {
    final List<User> users = userDao.getAll();

    for (final User user : users) {

      if (upgradePolicy.canUpgradeLevel(user)) {
        upgradePolicy.upgradeLevels(user);
        userDao.update(user);
      }

    }
  }


  public void add(final User user) {
    if (user.getLevel() == null) {
      user.setLevel(Level.BASIC);
    }
    userDao.add(user);
  }
}
