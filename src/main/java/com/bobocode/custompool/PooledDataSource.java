package com.bobocode.custompool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.sql.DataSource;
import lombok.experimental.Delegate;

public class PooledDataSource implements DataSource {

  private static final int DEFAULT_CONNECTIONS_COUNT = 10;
  private final Queue<ConnectionProxy> connectionPool = new ConcurrentLinkedQueue<>();
  @Delegate(excludes = Exclude.class)
  private final DataSource dataSource;

  public PooledDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    initializePool();
  }

  private void initializePool() {
    for (int i = 0; i < DEFAULT_CONNECTIONS_COUNT; i++) {
      try {
        var connection = new ConnectionProxy(dataSource.getConnection(), connectionPool);
        connectionPool.add(connection);
      } catch (SQLException e) {
        throw new RuntimeException("Exception when obtaining a connection from datasource", e);
      }
    }
  }

  @Override
  public Connection getConnection() {
    return connectionPool.poll();
  }

  private interface Exclude {
    Connection getConnection();
  }
}
