package com.fedomn.observer;

public class ConcreteObserver implements Observer {

  public void update() {
    System.out.println("I am observer, I can see you Subject!");
  }
}
