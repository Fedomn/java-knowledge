package com.fedomn.dynamic.proxy;

import java.lang.reflect.Proxy;
import java.util.Map;
import org.junit.Test;

public class ProxyInstanceTest {

  @Test
  public void creatingProxyInstance() {
    Map proxyInstance =
        (Map)
            Proxy.newProxyInstance(
                DynamicClass.class.getClassLoader(),
                new Class[] {Map.class},
                new DynamicInvocationHandler());

    proxyInstance.put(1, 1);
  }

  @Test
  public void invocationHandlerViaLambdaExpressions() {
    Map proxyInstance =
        (Map)
            Proxy.newProxyInstance(
                DynamicClass.class.getClassLoader(),
                new Class[] {Map.class},
                (proxy, method, methodArgs) -> {
                  if (method.getName().equals("get")) {
                    return 42;
                  } else {
                    throw new UnsupportedOperationException(
                        "Unsupported method: " + method.getName());
                  }
                });

    System.out.println(proxyInstance.get("test"));
    proxyInstance.put("1", "1");
  }

  private static class DynamicClass {}
}
