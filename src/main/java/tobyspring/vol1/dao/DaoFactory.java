package tobyspring.vol1.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {
  @Bean
  public UserDao userDao() {
    UserDao dao = new UserDao();
    userDao().setDataSource(dataSource());
    return dao;
  }

  @Bean
  public ConnectionMaker connectionMaker() {
    ConnectionMaker connectionMaker = new DConnectionMaker();
    return connectionMaker;
  }

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(org.postgresql.Driver.class);
    dataSource.setUrl("jdbc:postgresql://127.0.0.1:15432/springbook");
    dataSource.setUsername("postgres");
    dataSource.setPassword("1LOls91VintrU45");

    return dataSource;
  }
}
