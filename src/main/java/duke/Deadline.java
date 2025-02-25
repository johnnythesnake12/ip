package duke;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class Deadline extends Task {
    private LocalDateTime deadline;


    // constructor of deadline takes in a string, whether it is marked and a deadline
    public Deadline(String name, boolean isMarked, LocalDateTime deadline) {
        super(name, isMarked);
        this.deadline = deadline;
    }


    // gets the deadline of the Deadline object
    public LocalDateTime getDeadline() {

        return deadline;
    }
    // sets the deadline of the Deadline Object
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    // returns a String depending on if it is marked as [D] <marked/or not> <name of deadline task> by:<Date>
    @Override
    public String toString() {
        String status = isMarked ? "[X]" : "[ ]";
        return "[D]" + status + " " + name + " (by: " + deadline.format(DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm")) + ")";
    }
}
