package tobyspring.vol1.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {


  public Integer fileReadTemplate(final String filepath, final BufferedReaderCallback callback) throws IOException {

    try (BufferedReader br = new BufferedReader(new FileReader(filepath));) {
      Integer sum = 0;
      String line = null;

      int ret = callback.doSomeThingWithReader(br);

      return ret;

    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw e;
    }

  }

  public <T> T lineReadTemplate(final String filepath, final LineCallback<T> callback, T initVal) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(filepath));) {
      T res = initVal;
      String line = null;

      while ((line = br.readLine()) != null) {
        res = callback.doSomethingWithLine(line, res);
      }

      return res;

    } catch (IOException e) {
      System.out.println(e.getMessage());
      throw e;
    }
  }

  public Integer calcSum(String filepath) throws IOException {
    LineCallback<Integer> sumCallback = new LineCallback<>() {
      public Integer doSomethingWithLine(String line, Integer value) {
        return value + Integer.parseInt(line);
      }
    };
    return lineReadTemplate(filepath, sumCallback, 0);

  }

  public Integer calcMultiply(final String filepath) throws IOException {
    LineCallback<Integer> sumCallback = new LineCallback<>() {
      public Integer doSomethingWithLine(String line, Integer value) {
        return value * Integer.parseInt(line);
      }
    };
    return lineReadTemplate(filepath, sumCallback, 1);
  }

  public String concatenate(final String filepath) throws IOException {
    LineCallback<String> concatenateCallback = new LineCallback<>() {
      public String doSomethingWithLine(String line, String value) {
        return value + line;
      }
    };
    return lineReadTemplate(filepath, concatenateCallback, "");
  }
}
