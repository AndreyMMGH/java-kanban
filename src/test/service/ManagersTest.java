package test.service;

import org.junit.jupiter.api.Test;
import service.InMemoryHistoryManager;
import service.InMemoryTasksManager;
import service.Managers;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ManagersTest {

    @Test
    void returnsAnInstanceOfInMemoryTasksManagerThatIsInitializedAndReadyForUse() {
        assertInstanceOf(InMemoryTasksManager.class, Managers.getDefault());
    }

    @Test
    void returnsAnInstanceOfInMemoryHistoryManagerThatIsInitializedAndReadyForUse() {
        assertInstanceOf(InMemoryHistoryManager.class, Managers.getDefaultHistory());
    }
}