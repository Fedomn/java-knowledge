package com.fedomn.observer;

public class App {
  public static void main(String[] args) {
    ConcreteSubject subject = new ConcreteSubject();
    ConcreteObserver concreteObserver = new ConcreteObserver();
    subject.addObserver(concreteObserver);
    subject.doSomething();
  }
}
