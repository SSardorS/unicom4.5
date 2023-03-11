package uz.pixel.unicom.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskChangeStepDto {

    private String id;

    private UUID user;
    private String step;


}
