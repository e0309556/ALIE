package alie;

import alie.task.Deadlines;
import alie.task.Events;
import alie.task.ToDo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Alie {

    public static final String logo =
                      "    /\\       |        |   |‾‾‾‾‾" + System.lineSeparator()
                    + "   /  \\      |        |   |"      + System.lineSeparator()
                    + "  /____\\     |        |   |----"  + System.lineSeparator()
                    + " /      \\    |        |   |"      + System.lineSeparator()
                    + "/        \\ . |_____ . | . |_____ .";
    protected static final int DONE_CMD_LENGTH = 5;
    protected static final int TODO_CMD_LENGTH = 5;
    protected static final int DELETE_CMD_LENGTH = 7;
    protected static final String DEADLINE_DETAIL_DIVIDER = " /by ";
    protected static final String EVENT_DETAILS_DIVIDER = " /at ";
    protected static final String FILEPATH = "storage.txt";

    public static void main(String[] args) {
        printWelcomeMsg();

        TaskManager checkList = null;
        Storage storage = new Storage(FILEPATH);
        Scanner userInput = new Scanner(System.in);

        checkList = getDataFromStorage(checkList, storage);
        while (true) {
            printHeader();
            String cmd = getUserInput(userInput);
            try {
                parseThenExecuteCmd(cmd, checkList);
            } catch (Exception errorMsg) {
                System.out.println(errorMsg);
            }
            saveCheckListToFile(checkList, storage);
        }
    }

    public static void printWelcomeMsg() {
        System.out.println("Hello from\n" + logo);
        printHeader();
        System.out.println("What would you like to do?");
    }

    public static void printHeader() {
        System.out.print("ALIE> ");
    }

    private static String getUserInput(Scanner userInput) {
        return userInput.nextLine();
    }

    private static void parseThenExecuteCmd(String cmd, TaskManager checkList)
            throws InvalidCmdException {
        String[] splitCmds = cmd.split(" ", 2);
        String cmdType = splitCmds[0].toLowerCase();

        switch (cmdType) {
        case "bye":
            //Exiting A.L.I.E
            exitAlie(cmd, splitCmds);
        case "list":
            //Print list with all tasks
            printChecklist(checkList, splitCmds);
            break;
        case "done":
            //Mark task as complete
            markAsDone(cmd, checkList);
            break;
        case "delete":
            //Delete tasks
            deleteTask(cmd, checkList);
            break;
        case "todo":
            //Input format: <task type> <task name>
            addToDoTask(cmd, checkList);
            break;
        case "deadline":
            // Input format 1: <task type> <task name> /by <task details>
            addDeadlineTask(cmd, checkList);
            break;
        case "event":
            // Input format 1: <task type> <task name> /at <task details>
            addEventTask(cmd, checkList);
            break;
        default:
            throw new InvalidCmdException("Unable to execute \"" + cmd +
                    "\". Please try again with valid command.");
        }
    }

    private static void exitAlie(String cmd, String[] splitCmds) throws InvalidCmdException {
        if (splitCmds.length > 1 ) {
            throw new InvalidCmdException("To exit, use cmd: \"bye\".");
        }
        System.out.println("Bye-bye!");
        System.exit(0);
    }

    private static void printChecklist(TaskManager checkList, String[] splitCmds)
            throws InvalidCmdException {
        if (splitCmds.length > 1 ) {
            throw new InvalidCmdException("Unable to append info to cmd: \"list\".");
        }
        checkList.printAllTasksAdded();
        return;
    }

    private static void markAsDone(String cmd, TaskManager checkList) throws InvalidCmdException {
        //Input format: done <task index>
        try {
            int indexOfTask = Integer.parseInt(cmd.substring(DONE_CMD_LENGTH));
            checkList.markTaskCompleted(indexOfTask - 1);
        } catch (NumberFormatException error) {
            throw new InvalidCmdException("INDEX provided is not a number.");
        } catch (IndexOutOfBoundsException | NullPointerException error) {
            throw new InvalidCmdException("INDEX provided is not a valid index.");
        }
        return;
    }

    private static void deleteTask(String cmd, TaskManager checkList) throws InvalidCmdException {
        //Input format: delete <task index>
        try {
            int indexOfTask = Integer.parseInt(cmd.substring(DELETE_CMD_LENGTH));
            checkList.deleteTask(indexOfTask - 1);
        } catch (NumberFormatException error) {
            throw new InvalidCmdException("INDEX provided is not a number.");
        } catch (IndexOutOfBoundsException error) {
            throw new InvalidCmdException("INDEX provided is not a valid index.");
        } catch (NullPointerException error) {
            throw new InvalidCmdException("INDEX provided is not a valid index.");
        }
        return;
    }

    private static void addToDoTask(String cmd, TaskManager checkList) throws InvalidCmdException {
        String taskName;
        try {
            taskName = cmd.substring(TODO_CMD_LENGTH);
            if (taskName.equalsIgnoreCase("")) {
                throw new InvalidCmdException("DESCRIPTION of TODO is missing.");
            }
            checkList.addNewTask(new ToDo(taskName));
        } catch (StringIndexOutOfBoundsException error) {
            throw new InvalidCmdException("DESCRIPTION of TODO is missing.");
        }
        return;
    }

    private static void addDeadlineTask(String cmd, TaskManager checkList)
            throws InvalidCmdException {
        int detailsDividerId;
        String taskName;
        String taskDetails;
        try {
            detailsDividerId = cmd.indexOf(DEADLINE_DETAIL_DIVIDER);
            taskName = cmd.substring(0, detailsDividerId).trim();
            taskDetails = cmd.substring(detailsDividerId +
                    DEADLINE_DETAIL_DIVIDER.length()).trim();
            checkList.addNewTask(new Deadlines(taskName, taskDetails));
        } catch (StringIndexOutOfBoundsException error) {
            throw new InvalidCmdException("DESCRIPTION and DATE of deadline is missing.");
        }
        return;
    }

    private static void addEventTask(String cmd, TaskManager checkList) throws InvalidCmdException {
        int detailsDividerId;
        String taskName;
        String taskDetails;
        try {
            detailsDividerId = cmd.indexOf(EVENT_DETAILS_DIVIDER);
            taskName = cmd.substring(0, detailsDividerId).trim();
            taskDetails = cmd.substring(detailsDividerId +
                    EVENT_DETAILS_DIVIDER.length()).trim();
            checkList.addNewTask(new Events(taskName, taskDetails));
        } catch (StringIndexOutOfBoundsException error) {
            throw new InvalidCmdException("DESCRIPTION and DATE of event is missing.");
        }
    }

    private static TaskManager getDataFromStorage(TaskManager checkList, Storage storage) {
        try {
            checkList = storage.readFromFile();
        } catch (FileNotFoundException e) {
            checkList = new TaskManager();
            System.out.println("File not found");
        } catch (InvalidCmdException e) {

        } finally {
            if (checkList == null) {
                checkList = new TaskManager();
            }
        }
        return checkList;
    }

    private static void saveCheckListToFile(TaskManager checkList, Storage storage) {
        try {
            storage.save(checkList);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
