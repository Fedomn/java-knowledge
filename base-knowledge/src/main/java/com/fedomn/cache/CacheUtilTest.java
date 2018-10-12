package com.fedomn.cache;

import static org.assertj.core.api.Assertions.assertThat;

import com.fedomn.cache.CacheUtil.CacheMetadata;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

public class CacheUtilTest {
  @Test
  public void shouldFindOrSetCacheByKeySuccess() {
    Consumer<CacheMetadata> onRefresh =
        cacheMetadata -> {
          cacheMetadata.setCache("key1", new A("val1"));
          cacheMetadata.setCache("key2", new A("val2"));
        };

    A findVal = CacheUtil.INSTANCE.findOrSetCacheByKey(A.class, "key2", onRefresh);
    assertThat(findVal.getVal()).isEqualTo("val2");
  }

  @Data
  @AllArgsConstructor
  class A {
    private String val;
  }
}
