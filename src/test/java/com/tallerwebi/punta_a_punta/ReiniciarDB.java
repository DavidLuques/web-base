package com.tallerwebi.punta_a_punta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.mindrot.jbcrypt.BCrypt;

public class ReiniciarDB {

  public static void limpiarBaseDeDatos() {
    String passwordHasheada = BCrypt.hashpw("test", BCrypt.gensalt());
    String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
    String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
    String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
    String dbPassword = System.getenv("DB_PASSWORD") != null
      ? System.getenv("DB_PASSWORD")
      : "user";

    String url = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);

    try (
      Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
      Statement stmt = conn.createStatement()
    ) {
      stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
      stmt.execute("DELETE FROM mascota");
      stmt.execute("DELETE FROM Usuario");
      stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
      stmt.execute("ALTER TABLE mascota AUTO_INCREMENT = 1");
      stmt.execute("ALTER TABLE Usuario AUTO_INCREMENT = 1");
      stmt.execute(
        "INSERT INTO Usuario(id, email, password, rol, activo) VALUES(null, 'test@unlam.edu.ar', 'test', 'USER', true)"
      );
      stmt.execute("DELETE FROM Usuario");
      stmt.execute("ALTER TABLE Usuario AUTO_INCREMENT = 1");
      stmt.execute(
        "INSERT INTO Usuario(id, email, password, rol, activo) " +
        "VALUES(null, 'test@unlam.edu.ar', '" +
        passwordHasheada +
        "', 'USER', true)"
      );

      System.out.println("Base de datos limpiada exitosamente");
    } catch (SQLException e) {
      System.err.println("Error limpiando la base de datos: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
