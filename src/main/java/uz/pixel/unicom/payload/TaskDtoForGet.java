package uz.pixel.unicom.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class TaskDtoForGet {

    private String id;

    private String taskTitle;

    private String description;

    private String status;

    private UserNameAndIdDto manager;
    private UserNameAndIdDto owner;
    private DepartmentIdAndNameDto department;

    private String startLine;
    private String deadline;
    private String deadTime;

    private Date extraDate;

    private boolean step;

}
