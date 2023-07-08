package com.fedomn.airlift;

import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

public class SliceMain {
  public static void main(String[] args) {
    Slice slice = Slices.wrappedBuffer(new byte[5]);
    slice.setByte(4, 0xDE);
    slice.setByte(3, 0xAD);
    slice.setByte(2, 0xBE);
    slice.setByte(1, 0xEF);
  }
}
