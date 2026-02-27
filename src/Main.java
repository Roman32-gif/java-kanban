public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager history = Managers.getDefaultHistory();

        Task task1 = new Task("Сделать уроки", "Написать сочинение");
        int taskId1 = manager.createNewTask(task1);

        Task task2 = new Task("Сделать ремонт", "Поклеить обои");
        int taskId2 = manager.createNewTask(task2);

        Epic epicTask1 = new Epic("Подготовка к новому году", "Украсить дом");
        int epicId1 = manager.createNewEpic(epicTask1);

        Subtask subTask1 = new Subtask("Купить ёлку", "Высота ёлки 1,5 метра, цвет белый", epicId1);
        manager.createNewSubTask(subTask1);
        Subtask subTask2 = new Subtask("Купить новогодние подарки", "5 подарков", epicId1);
        manager.createNewSubTask(subTask2);

        Epic epicTask2 = new Epic("Подготовиться к экзамену", "Выучить все 40 устных вопросов");
        int epicId2 = manager.createNewEpic(epicTask2);
        Subtask subTask3 = new Subtask("Написать ответы на все 40 вопросов", "Взять информацию из учебника", epicId2);
        manager.createNewSubTask(subTask3);

        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());

        task2.setStatus(Status.DONE);
        manager.updateBaseTask(task2);
        subTask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask2);
        subTask3.setStatus(Status.DONE);
        manager.updateSubTask(subTask3);

        history.add(task1);
        history.add(task2);
        history.add(task1);


        System.out.println("Задачи после изменений статусов");
        System.out.println(manager.getAllTasks());
        manager.deleteBasicTask(1);
        manager.deleteEpic(2);
        System.out.println("Все задачи после удалений");
        System.out.println(manager.getAllTasks());
        System.out.println(history.getHistory());
    }
}
