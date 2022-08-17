package com.bobocode.custompool;

import java.sql.Connection;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@RequiredArgsConstructor
public class ConnectionProxy implements Connection {

  @Delegate(excludes = Exclude.class)
  private final Connection connection;
  private final Queue<ConnectionProxy> connectionPool;

  @Override
  public void close() {
    connectionPool.add(this);
  }

  private interface Exclude {
    void close();
  }
}
