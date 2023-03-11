package uz.pixel.unicom.service;

import uz.pixel.unicom.payload.DepartmentDto;
import uz.pixel.unicom.payload.ResponseMessage;

import java.util.UUID;

public interface DepartmentInterface {

    ResponseMessage add(DepartmentDto departmentDto);

    ResponseMessage getAll(int pageNumber);

    ResponseMessage getById(UUID id);

    ResponseMessage edit(DepartmentDto departmentDto);

    ResponseMessage delete(UUID id);

}
