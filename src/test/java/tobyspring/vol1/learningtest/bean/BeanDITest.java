package tobyspring.vol1.learningtest.bean;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import tobyspring.vol1.service.UserServiceImpl;
import tobyspring.vol1.service.UserServiceTx;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanDITest {
  @Test
  void dependencyTest() {
    ApplicationContext ac =
        new GenericXmlApplicationContext("applicationContext.xml");

    UserServiceTx userServiceTx = ac.getBean("userService", UserServiceTx.class);
    UserServiceImpl userServiceImpl = ac.getBean("userServiceImpl", UserServiceImpl.class);
    System.out.println("userServiceImpl = " + userServiceImpl);
    System.out.println("userServiceTx.getUserService = " + userServiceTx.getUserService());
    assertThat(userServiceTx.getUserService()).isInstanceOf(UserServiceImpl.class);

  }

}
