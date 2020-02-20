package alie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import alie.task.Deadlines;
import alie.task.Events;
import alie.task.Task;
import alie.task.ToDo;

public class Storage {
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public TaskManager readFromFile() throws FileNotFoundException, InvalidFileFormatException {
        File f = new File(filePath);
        TaskManager taskManager = new TaskManager();
        Scanner s = new Scanner(f);

        while (s.hasNextLine()) {
             String encodedTasks = s.nextLine();
             switch (encodedTasks.charAt(0)) {
             case 'T':
                 taskManager.addStorageTask(ToDo.decodeTask(encodedTasks));
                 break;
             case 'D':
                 taskManager.addStorageTask(Deadlines.decodeTask(encodedTasks));
                 break;
             case 'E':
                 taskManager.addStorageTask(Events.decodeTask(encodedTasks));
                 break;
             default:
                 throw new InvalidFileFormatException("File format is not correct.");
             }
         }
         return taskManager;
    }

    public void save(TaskManager taskManager) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        StringBuilder allTasks = new StringBuilder();
        for (Task task : taskManager.taskList) {
            allTasks.append(task.encodeTask());
            allTasks.append(System.lineSeparator());
        }
        fw.write(allTasks.toString());
        fw.close();
    }
}
