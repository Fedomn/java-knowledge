package com.fedomn.lombok;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * lombok annotation @Builder.Default will remove field default value in build class.
 *
 * so lombok will set @Builder.Default field default value in no args constructor.
 *
 * but our customer constructor will not set @Builder.Default field default value that looks like
 * lombok bug at https://github.com/rzwitserloot/lombok/issues/1347.
 *
 */
public class BuilderDefault {
  @Data
  @EqualsAndHashCode(callSuper = false)
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class A {
    @Builder.Default String name = "default_name";
    B b;

    A(B b) {
      this.b = b;
    }
  }

  static class B {}
}
