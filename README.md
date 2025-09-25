**Kanban**

📌 **Описание проекта**  
**Kanban** – трекер, позволяющий эффективно организовать совместную работу над задачами.
 <img width="974" height="586" alt="image" src="https://github.com/user-attachments/assets/97845702-6ebe-46b4-bbeb-72c4d737d496" />
Приложение поддерживает **три типа задач**:  
&nbsp;&nbsp;●&nbsp;**Task** — обычная задача.  
&nbsp;&nbsp;●&nbsp;**Epic** — крупная задача, состоящая из подзадач.  
&nbsp;&nbsp;●&nbsp;**Subtask** — подзадача, принадлежащая конкретному эпику.  
Задачи имеют название, описание, статус выполнения (**NEW, IN_PROGRESS, DONE**), а также время начала и продолжительность.  
Статус эпика вычисляется автоматически на основе статусов его подзадач.

📂 **Архитектура**  
Проект построен с использованием принципа разделения обязанностей:  
&nbsp;&nbsp;●&nbsp;**Task / Epic / Subtask** — модели задач.  
&nbsp;&nbsp;●&nbsp;**TasksManager** — интерфейс менеджера задач.  
&nbsp;&nbsp;●&nbsp;**InMemoryTasksManager** — реализация менеджера задач в памяти.  
&nbsp;&nbsp;●&nbsp;**FileBackedTaskManager** — менеджер задач с сохранением данных в CSV.  
&nbsp;&nbsp;●&nbsp;**HistoryManager** – интерфейс менеджера истории просмотров.  
&nbsp;&nbsp;●&nbsp;**InMemoryHistoryManager** — реализация менеджера истории просмотров.  
&nbsp;&nbsp;●&nbsp;**HttpTaskServer** — HTTP-сервер для работы с задачами.  


⚙️ **Функционал**
<img width="974" height="1056" alt="image" src="https://github.com/user-attachments/assets/226ea721-0c4f-4646-bae8-a5cdd6c611ac" />
 
🛠️ **Стек технологий**  
&nbsp;&nbsp;●&nbsp;Java  
&nbsp;&nbsp;●&nbsp;Collections API  
&nbsp;&nbsp;●&nbsp;HTTP Server  
&nbsp;&nbsp;●&nbsp;Gson (для работы с JSON)  
&nbsp;&nbsp;●&nbsp;JUnit (для тестов)  
