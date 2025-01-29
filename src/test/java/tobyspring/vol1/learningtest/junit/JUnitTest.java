package tobyspring.vol1.learningtest.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class JUnitTest {
  static Set<JUnitTest> testObjects = new HashSet<>();


  @Test
  public void test1() {
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
  }

  @Test
  public void test2() {
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
  }

  @Test
  public void test3() {
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
  }
}
