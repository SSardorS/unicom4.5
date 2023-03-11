package uz.pixel.unicom.payload;

import lombok.Data;


import java.util.Date;
import java.util.UUID;

@Data
public class TaskDto {

    private String id;

    private String taskTitle;

    private String description;

    private String status;

    private Integer messageId;

    private UUID manager;

    private UUID user;

    private UUID department;

    private String deadline;
    private String deadTime;

    private Date extraDate;

    private boolean step;


}
