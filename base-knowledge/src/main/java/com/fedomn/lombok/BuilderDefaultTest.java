package com.fedomn.lombok;

import static org.assertj.core.api.Assertions.assertThat;

import com.fedomn.lombok.BuilderDefault.B;
import org.junit.Test;

public class BuilderDefaultTest {

  @Test
  public void shouldFindOrSetCacheByKeySuccess() {
    BuilderDefault.A a1 = new BuilderDefault.A();
    assertThat(a1.getName()).isNotNull();
    assertThat(a1.getB()).isNull();

    BuilderDefault.A a2 = new BuilderDefault.A(new B());
    assertThat(a2.getName()).isNull();
    assertThat(a2.getB()).isNotNull();
  }
}
