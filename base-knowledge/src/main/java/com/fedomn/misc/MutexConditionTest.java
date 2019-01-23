package com.fedomn.misc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Test;

public class MutexConditionTest {

  @Test
  public void testNoName() {
    String one = MutexCondition.<String>builder().on(() -> true, () -> "1").orElseGet(() -> null);
    assertThat(one).isEqualTo("1");

    String two =
        MutexCondition.<String>builder()
            .on(() -> false, () -> "1")
            .on(() -> true, () -> "2")
            .orElseGet(() -> null);
    assertThat(two).isEqualTo("2");

    String three =
        MutexCondition.<String>builder()
            .on(() -> false, () -> "1")
            .on(() -> false, () -> "2")
            .orElseGet(() -> null);
    assertThat(three).isEqualTo(null);

    assertThatExceptionOfType(Exception.class)
        .isThrownBy(
            () ->
                MutexCondition.<String>builder()
                    .on(() -> false, () -> "1")
                    .on(() -> false, () -> "2")
                    .orElseThrow(() -> new Exception("123")));
  }
}
