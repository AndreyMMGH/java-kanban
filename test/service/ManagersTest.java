package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void checkGetDefault() {
        assertNotNull(Managers.getDefault(), "Вернулся Null");
    }

    @Test
    void checkGetDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory(), "Вернулся Null");
    }
}