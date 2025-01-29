package tobyspring.vol1.learningtest.junit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootConfiguration
public class ApplicationContextTest {
  @Autowired
  private ApplicationContext context;

  static Set<ApplicationContextTest> testObjects = new HashSet<>();
  static ApplicationContext contextObject = null;


  @Test
  public void test1() {
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);

    assertThat(contextObject).isNull();
    contextObject = this.context;
  }

  @Test
  public void test2() {
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);

    assertThat(contextObject==null|| contextObject == this.context).isTrue();
  }

  @Test
  public void test3() {
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);

    assertThat(contextObject == null || contextObject == this.context);
  }
}
