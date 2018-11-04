package com.fedomn.dynamic.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicInvocationHandler implements InvocationHandler {

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) {
    System.out.println("invoke method: " + method.getName());
    return null;
  }
}
