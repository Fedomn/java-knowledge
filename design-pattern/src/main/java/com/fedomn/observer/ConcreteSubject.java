package com.fedomn.observer;

public class ConcreteSubject extends Subject {
  public void doSomething() {
    System.out.println("I am Subject, I am doing something!");
    super.notifyObservers();
  }
}
