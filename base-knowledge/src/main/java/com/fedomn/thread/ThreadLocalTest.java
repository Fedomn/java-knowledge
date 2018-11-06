package com.fedomn.thread;

/** 创建一个线程里共享的context 即：只要在同一个线程里，就可以获取ThreadLocal里的内容 */
public class ThreadLocalTest {

  private static final ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

  public static void main(String[] args) {
    new Thread(new OrderHandler("OrderHandler1")).start();
    new Thread(new OrderHandler("OrderHandler2")).start();
  }

  public static class OrderHandler implements Runnable {
    private static OrderService orderService = new OrderService();

    private final String name;

    OrderHandler(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      String value = String.format("%s-%s", Thread.currentThread().getName(), name);
      threadLocal.set(value);
      System.out.println(String.format("Set thread local value: %s", value));

      orderService.get();
    }
  }

  private static class OrderService {
    void get() {
      String value = threadLocal.get();
      System.out.println(Thread.currentThread().getName() + " Get thread local value: " + value);
    }
  }
}
