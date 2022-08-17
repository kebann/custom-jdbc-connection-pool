package com.bobocode.custompool;

import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.postgresql.ds.PGSimpleDataSource;

public class DemoApp {

  @SneakyThrows
  public static void main(String[] args) {
    var dataSource = pooledDataSource(postgresDataSource());
    var total = 0.0;
    var start = System.nanoTime();
    for (int i = 0; i < 50; i++) {
      try (var connection = dataSource.getConnection()) {
        connection.setAutoCommit(false);
        try (var statement = connection.createStatement()) {
          var rs = statement.executeQuery("select random() from PRODUCTS");
          rs.next();
          total += rs.getDouble(1);
        }
        connection.rollback();
      }
    }

    System.out.println((System.nanoTime() - start) / 1000_000 + " ms");
    System.out.println(total);
  }

  static DataSource postgresDataSource() {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setURL("jdbc:postgresql://localhost:5432/postgres");
    dataSource.setUser("baeldung");
    dataSource.setPassword("baeldung");

    return dataSource;
  }

  static DataSource pooledDataSource(DataSource dataSource) {
    return new PooledDataSource(dataSource);
  }
}
