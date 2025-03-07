package tobyspring.vol1.service;

import tobyspring.vol1.domain.User;

public interface UserService {
  void add(User user);

  void upgradeLevels() ;
}
