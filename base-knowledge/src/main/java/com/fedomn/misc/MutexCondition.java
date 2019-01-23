package com.fedomn.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class MutexCondition<R> {

  private final List<CondWithSuccessFunc<R>> container;

  MutexCondition(List<CondWithSuccessFunc<R>> container) {
    this.container = container;
  }

  public static <R> MutexCondition<R> builder() {
    return new MutexCondition<>(new ArrayList<>());
  }

  public MutexCondition<R> on(Supplier<Boolean> condition, Supplier<R> successFunc) {
    container.add(new CondWithSuccessFunc<>(condition, successFunc));
    return this;
  }

  private R hit() {
    for (CondWithSuccessFunc<R> condWithSuccessFunc : container) {
      if (condWithSuccessFunc.isTrue()) {
        return condWithSuccessFunc.getSuccessVal();
      }
    }
    return null;
  }

  public R orElseGet(Supplier<? extends R> other) {
    R retVal = hit();
    return retVal != null ? retVal : other.get();
  }

  public <X extends Throwable> R orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    R retVal = hit();
    if (retVal != null) {
      return retVal;
    } else {
      throw exceptionSupplier.get();
    }
  }

  private static final class CondWithSuccessFunc<T> {
    private final Supplier<Boolean> condition;
    private final Supplier<T> successFunc;

    CondWithSuccessFunc(Supplier<Boolean> condition, Supplier<T> successFunc) {
      this.condition = condition;
      this.successFunc = successFunc;
    }

    Boolean isTrue() {
      return condition.get();
    }

    T getSuccessVal() {
      return successFunc.get();
    }
  }
}
