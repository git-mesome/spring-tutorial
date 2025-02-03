package tobyspring.vol1.learningtest.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalcSumTest {

  Calculator calculator;
  String numFilepath;

  @BeforeEach
  public void setUp() {
    this.calculator = new Calculator();
    this.numFilepath = getClass().getResource("/numbers.txt")
        .getPath();
  }

  @Test
  public void sumOfNumbers() throws IOException {
    assertEquals(10, calculator.calcSum(this.numFilepath));
  }

  @Test
  public void multiplyOfNumbers() throws IOException {
    assertEquals(24, calculator.calcMultiply(this.numFilepath));
  }

  @Test
  public void concatenateStrings() throws IOException {
    assertEquals("1234", calculator.concatenate(this.numFilepath));
  }
}
