package ds4300.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtilsMySQL {

  private final String url;
  private final String user;
  private final String password;
  private Connection con = null;

  public DBUtilsMySQL(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
    this.con = getConnection();
  }

  public Connection getConnection() {
    if (con == null) {
      try {
        con = DriverManager.getConnection(url, user, password);
        return con;
      } catch (SQLException e) {
        System.err.println(e.getMessage());
        System.exit(1);
      }
    }
    return con;
  }

  public void closeConnection() {
    try {
      con.close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

  public int insertOneRecord(String insertSQL) {

    // System.out.println("INSERT STATEMENT: "+insertSQL);
    int key = -1;
    try {

      // get connection and initialize statement
      Connection con = getConnection();
      Statement stmt = con.createStatement();

      stmt.executeUpdate(insertSQL);

      // Cleanup
      stmt.close();

    } catch (SQLException e) {
      System.err.println("ERROR: Could not insert record: " + insertSQL);
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
    return key;
  }

}
