package com.fedomn.pool;

import java.time.Duration;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class CommonPoolTest {

  private static final int TIME_BETWEEN_EVICTION_RUNS_MILLIS = 1;
  private static final int MAX_WAIT_MILLIS = 1000;
  private static final int BORROW_MAX_WAIT_MILLIS = 3000;
  private static final int poolSize = 3;

  public static void main(String[] args) throws Exception {
    GenericObjectPoolConfig<Obj> config = new GenericObjectPoolConfig<>();
    config.setMaxIdle(poolSize);
    config.setMaxTotal(poolSize);
    config.setMinIdle(poolSize);
    config.setTimeBetweenEvictionRuns(Duration.ofMillis(TIME_BETWEEN_EVICTION_RUNS_MILLIS));
    config.setTestOnBorrow(true);
    config.setTestWhileIdle(true);
    config.setTestOnReturn(true);
    config.setFairness(true);
    config.setMaxWait(Duration.ofMillis(MAX_WAIT_MILLIS));
    config.setMinEvictableIdleTime(Duration.ofMillis(1000 * 5));

    GenericObjectPool<Obj> pool = new ConnectionPool(new PoolFactory(), config);

    Obj obj1 = pool.borrowObject(BORROW_MAX_WAIT_MILLIS);
    pool.returnObject(obj1);
    Obj obj2 = pool.borrowObject(BORROW_MAX_WAIT_MILLIS);
    pool.returnObject(obj2);
    Obj obj3 = pool.borrowObject(BORROW_MAX_WAIT_MILLIS);
    pool.returnObject(obj3);

    while (true) {}
  }

  public static class Obj {

    public String file;
  }

  public static class PoolFactory extends BasePooledObjectFactory<Obj> {

    @Override
    public Obj create() throws Exception {
      System.out.println("create");
      return new Obj();
    }

    @Override
    public PooledObject<Obj> wrap(Obj obj) {
      System.out.println("wrap");
      return new DefaultPooledObject<>(obj);
    }
  }

  private static class ConnectionPool extends GenericObjectPool<Obj> {

    public ConnectionPool(PooledObjectFactory<Obj> factory, GenericObjectPoolConfig<Obj> config) {
      super(factory, config);
    }

    @Override
    public Obj borrowObject(long borrowMaxWaitMillis) throws Exception {
      System.out.println("borrowObject");
      return super.borrowObject(borrowMaxWaitMillis);
    }
  }
}
