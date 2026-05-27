package com.tallerwebi.dominio;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class BcryptTest {

  @Test
  public void generateHashes() {
    System.out.println("HASH_TEST: " + BCrypt.hashpw("test", BCrypt.gensalt()));
    System.out.println("HASH_PRUEBA: " + BCrypt.hashpw("prueba", BCrypt.gensalt()));
  }
}
