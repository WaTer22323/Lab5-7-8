package com.example.mvcwebdemo.model; // เช็คว่าไฟล์อยู่ในโฟลเดอร์ model แล้วนะ

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import jakarta.persistence.PersistenceException; // เปลี่ยนมาใช้อันนี้

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class UserEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testSaveAndGetUser() {
        User newUser = new User("somchai", "password123", "USER");
        User savedUser = entityManager.persistAndFlush(newUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("somchai");
        assertThat(savedUser.getRole()).isEqualTo("USER");
    }

    @Test
    public void testDuplicateUsername_ShouldThrowException() {
        User user1 = new User("duplicateUser", "pass1", "USER");
        entityManager.persistAndFlush(user1);

        User user2 = new User("duplicateUser", "pass2", "ADMIN");

        // แก้เป็น PersistenceException
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(user2);
        });
    }

    @Test
    public void testUsernameNull_ShouldThrowException() {
        User user = new User(null, "password", "USER");

        // แก้เป็น PersistenceException
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(user);
        });
    }

    @Test
    public void testPasswordNull_ShouldThrowException() {
        User user = new User("userNoPass", null, "USER");

        // แก้เป็น PersistenceException
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(user);
        });
    }
}