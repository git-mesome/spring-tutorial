package tobyspring.vol1.learningtest.bean.factory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class FactoryBeanTest {

  @Autowired
  ApplicationContext ac;

  @Test
  void getMessageFromFactoryBean() {
    Message message = ac.getBean("message", Message.class);
    assertThat(message).isInstanceOf(Message.class);
    assertThat(message.getText()).isEqualTo("Factory Bean");
  }

  @Test
  void getFactoryBean() {
    Object factory = ac.getBean("&message", MessageFactoryBean.class);
    assertThat(factory).isInstanceOf(MessageFactoryBean.class);
  }

}
