package tobyspring.vol1.service;

import tobyspring.vol1.domain.User;

import java.util.List;

public interface UserService {
  void add(User user);

  User get(String id);

  List<User> getAll();

  void deleteAll();

  int getCount();

  void update(final User user);

  void upgradeLevels() ;
}
