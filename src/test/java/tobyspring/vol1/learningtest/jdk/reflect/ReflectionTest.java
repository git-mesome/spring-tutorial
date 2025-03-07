package tobyspring.vol1.learningtest.jdk.reflect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionTest {

  @Test
  public void invokeMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String name = "Spring";

    assertThat(name).hasSize(6);

    Method lengthMethod = String.class.getMethod("length");
    assertThat(lengthMethod.invoke(name)).isEqualTo(6);

    assertThat(name.charAt(0)).isEqualTo('S');

    Method charAtMethod = String.class.getMethod("charAt", int.class);
    assertThat(charAtMethod.invoke(name,0)).isEqualTo('S');
  }
}
