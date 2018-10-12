package com.fedomn.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public enum CacheUtil {
  INSTANCE;

  private final Map<Class<?>, CacheMetadata> classToDBEnumMetadata = new ConcurrentHashMap<>();

  public <T> T findOrSetCacheByKey(
      Class<T> type, String cacheKey, Consumer<CacheMetadata> onRefresh) {

    CacheMetadata cacheMetadata = classToDBEnumMetadata.get(type);

    if (Objects.isNull(cacheMetadata)) {
      cacheMetadata = CacheMetadata.buildDefault();
    }

    if (cacheMetadata.cacheMapIsEmpty() || Objects.isNull(cacheMetadata.getCache(cacheKey))) {
      try {
        cacheMetadata.lock();

        if (!cacheMetadata.cacheMapIsEmpty()) {
          return type.cast(cacheMetadata.getCache(cacheKey));
        }

        onRefresh.accept(cacheMetadata);

        classToDBEnumMetadata.put(type, cacheMetadata);
      } finally {
        cacheMetadata.unlock();
      }
    }

    return type.cast(cacheMetadata.getCache(cacheKey));
  }

  public static class CacheMetadata {
    private final Map<String, Object> cacheMap;
    private final Lock lock;

    private CacheMetadata(Map<String, Object> cacheMap, Lock lock) {
      this.cacheMap = cacheMap;
      this.lock = lock;
    }

    static CacheMetadata buildDefault() {
      return new CacheMetadata(new HashMap<>(), new ReentrantLock());
    }

    boolean cacheMapIsEmpty() {
      return cacheMap.isEmpty();
    }

    void lock() {
      lock.lock();
    }

    void unlock() {
      lock.unlock();
    }

    void setCache(String key, Object value) {
      cacheMap.put(key, value);
    }

    Object getCache(String key) {
      return cacheMap.get(key);
    }
  }
}
