package com.fedomn.spi.spitest;

import com.fedomn.spi.Spi;
import java.util.ServiceLoader;

public class SpiTest {

  public static void main(String[] args) {
    ServiceLoader<Spi> load = ServiceLoader.load(Spi.class);
    Spi next = load.iterator().next();
    next.test();
  }

}
