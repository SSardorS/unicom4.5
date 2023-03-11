package uz.pixel.unicom.enam;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {

    NEW(1, "Create Task by Suoer Admin"),

    SEND_TO_MANAGER(2, "Send to Manager"),

    SEND_TO_USER(3,"Send to User"),

    IN_PROGRESS(4,"In Progress"),

    FINISHED_USER(5, "Finished User"),
    FINISHED_MANAGER(6, "Finished User"),
    FINISHED(7, "Finished User"),

    REJECTED(7,"Rejected");



    int id;
    String description;

}
