package tobyspring.vol1.learningtest.bean.factory;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {

  String text;

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public Message getObject() throws Exception {
    return Message.newMessage(text);
  }

  // 이 타입으로 빈의 오브젝트 타입이 정의됨 = Message 빈
  @Override
  public Class<?> getObjectType() {
    return Message.class;
  }

  @Override
  public boolean isSingleton() {
    return false;
  }
}
