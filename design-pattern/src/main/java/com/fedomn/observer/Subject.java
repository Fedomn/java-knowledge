package com.fedomn.observer;

import java.util.Vector;

public abstract class Subject {

  private Vector<Observer> observers = new Vector<>();

  public void addObserver(Observer c) {
    this.observers.add(c);
  }

  public void deleteObserver(Observer c) {
    this.observers.remove(c);
  }

  public void notifyObservers() {
    this.observers.forEach(Observer::update);
  }
}
