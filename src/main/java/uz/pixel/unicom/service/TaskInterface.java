package uz.pixel.unicom.service;

import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.payload.TaskChangeStepDto;
import uz.pixel.unicom.payload.TaskDto;

import java.util.UUID;


public interface TaskInterface {

    ResponseMessage add(TaskDto taskDto);

    ResponseMessage getAllForSuperAdmin(int pageNumber, Status status);

    ResponseMessage getInPtogressForSuperAdmin(int pageNumber);
    ResponseMessage getAllForSuperManager(int pageNumber, Status status);

    ResponseMessage getById(String code);

    ResponseMessage changeStatus(TaskChangeStepDto taskChangeStepDto);


    ResponseMessage deleteById(UUID id);

    ResponseMessage edit(TaskDto taskDto);

    ResponseMessage getAllNew(int pageNumber, Status aNew);

    ResponseMessage getAll(int pageNumber);
}
