package com.fedomn.override;

/**
 *
 *
 * <pre>
 * (1) 在一个不存在继承的类中：
 * 初始化static变量, 执行static初始化快 -->
 * 初始化普通成员变量(如果有赋值语句),执行普通初始化块 -->
 * 构造方法
 *
 * (2) 在一个存在继承的类中：
 * 初始化父类static成员变量, 运行父类static初始化块 -->
 * 初始化子类static成员变量, 运行子类static初始化块 -->
 * 初始化父类实例成员变量(如果有赋值语句), 执行父类普通初始化块 -->
 * 父类构造方法 -->
 * 初始化子类实例成员变量(如果有赋值语句)及普通初始化块 -->
 * 子类构造方法
 *
 * 父类构造器里调用了子类重写的方法callName，而此时子类里的baseName没有初始化，所有为null
 *
 * </pre>
 */
public class Base {
  private String baseName = "base";

  public Base() {
    callName();
  }

  public static void main(String[] args) {
    Base b = new Sub();
  }

  public void callName() {
    System.out.println(baseName);
  }

  static class Sub extends Base {
    private String baseName = "sub";

    public void callName() {
      System.out.println(baseName); // null
    }
  }
}
