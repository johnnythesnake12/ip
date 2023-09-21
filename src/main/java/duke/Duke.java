package duke;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Duke {

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;

    public Duke(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = storage.load();
        } catch (DukeException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }


    public void run() {
        ui.showWelcomeMessage();

        while (true) {
            String userInput = ui.getUserInput();
            Command command = Parser.parseCommand(userInput);
            String description = Parser.parseDescription(userInput);
            switch (command) {
                //if user wants to exit, tasks are saved and exit message is shown
                case EXIT:
                    handleExit();
                    return;
                // lists all the tasks out
                case LIST:
                    handleList();
                    break;
                case FIND:
                    handleFind(description);
                    break;
                // unmarks task
                case UNMARK:
                    handleUnmark(description);
                    break;
                //marks the task
                case MARK:
                    handleMark(description);
                    break;
                // if user wants to add a todo object
                case TODO:
                    handleTodo(description);
                    break;
                // if user wants to input deadline
                case DEADLINE:
                    handleDeadline(description);
                    break;
                // if user wants to input an event
                case EVENT:
                    handleEvent(description);
                    break;
                // if user wants to delete existing task
                case DELETE:
                    handleDelete(description);
                    break;
                // if user just enters a completely invalid command
                case INVALID:
                    handleInvalid();
                    break;
            }
        }
    }

    private void handleExit() {
        storage.save(tasks, "tasks.txt");
        ui.closeScanner();
        ui.showExitMessage();
    }

    private void handleList() {
        ui.showTaskList(tasks);
    }

    private void handleFind(String description) {
        tasks.findTasksContainingKeyword(description);
    }

    private void handleUnmark(String description) {
        // if user inputs task number, check if it is even an integer, and whether it is within range
        try {
            int taskNumber = Integer.parseInt(description) - 1;
            if (taskNumber >= 0 && taskNumber < tasks.size()) {
                tasks.unmarkTask(taskNumber);
            } else {
                ui.showError("Task number out of range.");
            }

        } catch (NumberFormatException e) {
            ui.showError("Invalid task number. Please provide a valid integer.");
        }
    }

    private void handleMark(String description) {
        // if user inputs task number, check if it is even an integer, and whether it is within range
        try {
            int taskNumber = Integer.parseInt(description) - 1;
            if (taskNumber >= 0 && taskNumber < tasks.size()) {
                tasks.markTaskAsDone(taskNumber);
            } else {
                ui.showError("Task number out of range.");
            }
        } catch (NumberFormatException e) {
            ui.showError("Invalid task number. Please provide a valid integer.");
        }
    }
    private void handleTodo(String description) {
        try {
            if (description.isEmpty()) {
                throw new EmptyTodoException();
            }
            Todo todo = new Todo(description, false);
            tasks.addTask(todo);

        } catch (EmptyTodoException e) {
            System.out.println(e.getMessage());
        }
    }
    private void handleDeadline(String description) {
        if (description.isEmpty()) {
            try {
                throw new EmptyDeadlineException();
            } catch (EmptyDeadlineException e) {
                System.out.println(e.getMessage());
            }
        } else {
            // Find the index of the deadline separator "/"
            int separatorIndex = description.indexOf('/');

            if (separatorIndex != -1) { // Ensure the separator exists in the input
                // Extract the task description and deadline

                String descriptionString = description.substring(0, separatorIndex).trim();
                String deadline = description.substring(separatorIndex + 4).trim();
                String pattern = "\\d{4}/\\d{2}/\\d{2}";
                Pattern datePattern = Pattern.compile(pattern);
                Matcher matcher = datePattern.matcher(deadline);
                if (matcher.find()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    LocalDate localDateDeadline = LocalDate.parse(deadline, formatter);
                    Deadline deadlineTask = new Deadline(descriptionString, false, localDateDeadline);
                    tasks.addTask(deadlineTask);

                } else {
                    System.out.println("Please input your deadline in YYYY/MM/DD format");
                }
            } else {
                System.out.println("Invalid input format for deadline. Please input in the following format: <deadline> <description> /by <YYYY/MM/DD> ");
            }
        }
    }

    private void handleEvent(String description) {
        if (description.isEmpty()) {
            try {
                throw new EmptyEventException();
            } catch (EmptyEventException e) {
                System.out.println(e.getMessage());
            }
        } else {
            // Find the indices of the time separators
            int fromIndex = description.indexOf("/from");
            int toIndex = description.indexOf("/to");

            if (fromIndex != -1 && toIndex != -1) {
                // Extract the task description, startTime, and endTime
                String descriptionString = description.substring(0, fromIndex).trim();
                String startTime = description.substring(fromIndex + 5, toIndex).trim();
                String endTime = description.substring(toIndex + 3).trim();

                // Create a new Event object
                Event eventTask = new Event(descriptionString, false, startTime, endTime);
                tasks.addTask(eventTask);

            } else {
                System.out.println("Invalid input format for event command.");
            }
        }
    }

    private void handleDelete(String description) {
        try {
            int taskNumber = Integer.parseInt(description) - 1;
            if (taskNumber >= 0 && taskNumber < tasks.size()) {
                tasks.deleteTask(taskNumber);
            } else {
                ui.showError("Task number out of range.");
            }
        } catch (NumberFormatException e) {
            ui.showError("Invalid task number. Please provide a valid integer.");
        }
    }

    private void handleInvalid() {
        ui.showError("Invalid command. Please try again.");
    }

    public static void main(String[] args) {
        new Duke("tasks.txt").run();
    }
}

