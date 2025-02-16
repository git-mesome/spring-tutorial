package tobyspring.vol1.service;

import tobyspring.vol1.domain.User;

public interface UserLevelUpgradePolicy {
  Boolean canUpgradeLevel(final User user);
  void upgradeLevels(User user);
}
