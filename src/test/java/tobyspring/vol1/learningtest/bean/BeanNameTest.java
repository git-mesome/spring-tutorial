package tobyspring.vol1.learningtest.bean;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import tobyspring.vol1.service.UserServiceImpl;


public class BeanTest {

  @Test
  void xmlAppContext() {
    ApplicationContext ac =
        new GenericXmlApplicationContext("applicationContext.xml");

    UserServiceImpl bean = ac.getBean(UserServiceImpl.class);

    String[] beanDefinitionNames = ac.getBeanDefinitionNames();
    for (String beanDefinitionName : beanDefinitionNames) {
      System.out.println("beanDefinitionName = " + beanDefinitionName);

//      if (beanInstance instanceof UserServiceImpl) {
//        System.out.println("beanDefinitionName = " + beanDefinitionName);
//        assertThat(beanDefinitionName).isEqualTo("userServiceImpl");
    }
  }
}
