package service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTasksManagerTest extends TasksManagerTest<InMemoryTasksManager> {

    @BeforeEach
    public void beforeEach() {

        manager = new InMemoryTasksManager();
    }


}
