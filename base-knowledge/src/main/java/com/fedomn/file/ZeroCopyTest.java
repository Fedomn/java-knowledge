package com.fedomn.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class ZeroCopyTest {

  public static void main(String[] args) throws IOException {
    RandomAccessFile sourceFile = new RandomAccessFile("sourceFile.txt", "rw");
    FileChannel fromChannel = sourceFile.getChannel();

    RandomAccessFile targetFile = new RandomAccessFile("targetFile.txt", "rw");
    FileChannel toChannel = targetFile.getChannel();

    fromChannel.transferTo(0, fromChannel.size(), toChannel);
  }
}
