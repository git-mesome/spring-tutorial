package tobyspring.vol1.service;

import tobyspring.vol1.dao.UserDao;
import tobyspring.vol1.domain.Level;
import tobyspring.vol1.domain.User;

import java.util.List;

public class UserService {
  UserDao userDao;

  public void setUserDao(UserDao userDao) {
    this.userDao = userDao;
  }

  public void upgradeLevels() {
    final List<User> users = userDao.getAll();

    for (final User user : users) {

      if (canUpgradeLevel(user)) {
        user.upgradeLevel();
        userDao.update(user);

      }

    }
  }

  private static Boolean canUpgradeLevel(final User user) {
    final Level currentLevel = user.getLevel();

    switch (currentLevel) {
      case BASIC -> {
        return user.getLogin() >= 50;
      }
      case SILVER -> {
        return user.getRecommend() >= 30;
      }
      case GOLD -> {
        return false;
      }
      default -> throw new IllegalArgumentException("Unknown Level value: " + currentLevel);
    }

  }


  public void add(final User user) {
    if (user.getLevel() == null) {
      user.setLevel(Level.BASIC);
    }
    userDao.add(user);
  }
}
