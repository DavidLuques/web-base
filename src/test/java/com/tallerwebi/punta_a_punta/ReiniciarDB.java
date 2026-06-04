package com.tallerwebi.punta_a_punta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.mindrot.jbcrypt.BCrypt;

public class ReiniciarDB {

  public static void limpiarBaseDeDatos() {
    String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
    String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
    String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
    String dbPassword = System.getenv("DB_PASSWORD") != null
      ? System.getenv("DB_PASSWORD")
      : "user";

    String url = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);
    String passwordHasheada = BCrypt.hashpw("test", BCrypt.gensalt());

    try (
      Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
      Statement stmt = conn.createStatement()
    ) {
      stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
      System.out.println("FK checks off");
      stmt.execute("DELETE FROM alerta");
      System.out.println("alerta borrada");
      stmt.execute("DELETE FROM Actividad");
      System.out.println("Actividad borrada");
      stmt.execute("DELETE FROM Analisis");
      System.out.println("Analisis borrada");
      stmt.execute("DELETE FROM registro_sueno");
      System.out.println("registro_sueno borrado");
      stmt.execute("DELETE FROM vallado");
      System.out.println("vallado borrado");
      stmt.execute("DELETE FROM mascota");
      System.out.println("mascota borrada");
      stmt.execute("DELETE FROM Usuario");
      System.out.println("Usuario borrada");
      stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
      stmt.execute("ALTER TABLE mascota AUTO_INCREMENT = 1");
      stmt.execute("ALTER TABLE Usuario AUTO_INCREMENT = 1");

      stmt.execute(
        "INSERT INTO Usuario(id, email, password, rol, activo) " +
        "VALUES(null, 'test@unlam.edu.ar', '" +
        passwordHasheada +
        "', 'USER', true)"
      );

      ResultSet rs = stmt.executeQuery("SELECT id FROM Usuario WHERE email = 'test@unlam.edu.ar'");
      rs.next();
      long idUsuario = rs.getLong("id");
      System.out.println("Usuario insertado con id: " + idUsuario);

      stmt.execute(
        "INSERT INTO mascota(nombre, estado_actual, tamano, raza, genero, tipo, peso, esteril, usuario_id, activo) " +
        "VALUES('Firulais', 'CAMINANDO', 'MEDIANO', 'Labrador', 'Macho', 'Perro', 10.5, true, " +
        idUsuario +
        ", true)"
      );
      System.out.println("Mascota insertada");

      stmt.execute(
        "INSERT INTO vallado(id_mascota, latitud_centro, longitud_centro, radio_metros) " +
        "VALUES(1, -34.7222, -58.5250, 150.0)"
      );
      System.out.println("Vallado insertado");

      System.out.println("Base de datos limpiada exitosamente");
    } catch (SQLException e) {
      System.err.println("Error limpiando la base de datos: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
