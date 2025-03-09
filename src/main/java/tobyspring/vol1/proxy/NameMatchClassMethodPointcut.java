package tobyspring.vol1.proxy;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut {

  public void setMappedClassName(String mappedClassName) {
    this.setClassFilter(new SimpleClassFilter(mappedClassName));
  }

  static class SimpleClassFilter implements ClassFilter {
    String mappedName;

    private SimpleClassFilter(String mappedName) {
      this.mappedName = mappedName;
    }

    @Override
    public boolean matches(Class<?> clazz) {
      // 와일드카드(*) 문자열 비교 : *name, name*, *name*
      return PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName());
    }
  }

}
