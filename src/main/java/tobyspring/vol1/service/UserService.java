package tobyspring.vol1.service;

import org.springframework.transaction.annotation.Transactional;
import tobyspring.vol1.domain.User;

import java.util.List;

@Transactional
public interface UserService {
  void add(User user);

  @Transactional(readOnly = true)
  User get(String id);

  @Transactional(readOnly = true)
  List<User> getAll();

  void deleteAll();

  @Transactional(readOnly = true)
  int getCount();

  void update(final User user);

  void upgradeLevels() ;
}
