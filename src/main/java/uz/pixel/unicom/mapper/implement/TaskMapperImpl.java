package uz.pixel.unicom.mapper.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Component;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.entity.Department;
import uz.pixel.unicom.entity.Tasks;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.mapper.interfaceMapper.TaskMapper;
import uz.pixel.unicom.payload.*;
import uz.pixel.unicom.service.miniservice.DateAndTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TaskMapperImpl implements TaskMapper {

    @Autowired
    DateAndTimeFormatter dateAndTimeFormatter;


    @Override
    public List<TaskDtoForGet> map(List<Tasks> tasks) {

        if (tasks.isEmpty())
            return null;

        List<TaskDtoForGet> taskDtos = new ArrayList<>();

        for (Tasks task : tasks) {

            TaskDtoForGet taskDto = new TaskDtoForGet();
//            taskDto.setId(task.getId());
            taskDto.setId(task.getCode());
            taskDto.setTaskTitle(task.getTitle());
            taskDto.setDescription(task.getDescription());
            taskDto.setStatus(task.getStatus().name());

            try {
                User manager = task.getManager();
                taskDto.setManager(new UserNameAndIdDto(manager.getId(), manager.getFullName()));

                Department department = manager.getDepartment();

                taskDto.setDepartment(new DepartmentIdAndNameDto(department.getId(), department.getName()));
            }catch (Exception e){
                taskDto.setDepartment(null);
                taskDto.setManager(null);
            }

            User owner = task.getCreatedBy();

            taskDto.setOwner(new UserNameAndIdDto(owner.getId(), owner.getFullName()));

            taskDto.setStep(task.isStep());

            DateSeparate dateSeparate = dateAndTimeFormatter.dateSepareteTimeAndDate(task.getTaskExpireDate());

            taskDto.setStartLine(dateAndTimeFormatter.dateSepareteTimeAndDate(task.getCreatedAt()).getDate());
            taskDto.setDeadline(dateSeparate.getDate());
            taskDto.setDeadTime(dateSeparate.getTime());


            taskDtos.add(taskDto);

        }

        return taskDtos;

    }

    @Override
    public List<TaskDtoForGet> mapStatus(List<Tasks> tasks, Status status) {

        if (tasks.isEmpty())
            return null;

        List<TaskDtoForGet> taskDtos = new ArrayList<>();

        for (Tasks task : tasks) {

            TaskDtoForGet taskDto = new TaskDtoForGet();
//            taskDto.setId(task.getId());
            taskDto.setId(task.getCode());
            taskDto.setTaskTitle(task.getTitle());
            taskDto.setDescription(task.getDescription());
            taskDto.setStatus(status.name());
//            taskDto.setMessageId(task.getMessgaeId());

            try {
                User manager = task.getManager();
                Department department = manager.getDepartment();

                taskDto.setManager(new UserNameAndIdDto(manager.getId(), manager.getFullName()));

                taskDto.setDepartment(new DepartmentIdAndNameDto(department.getId(), department.getName()));

            }catch (Exception e){

                taskDto.setDepartment(null);
                taskDto.setManager(null);

            }
            taskDto.setStep(task.isStep());

            User owner = task.getCreatedBy();

            taskDto.setOwner(new UserNameAndIdDto(owner.getId(), owner.getFullName()));

            DateSeparate dateSeparate = dateAndTimeFormatter.dateSepareteTimeAndDate(task.getTaskExpireDate());

            taskDto.setStartLine(dateAndTimeFormatter.dateSepareteTimeAndDate(task.getCreatedAt()).getDate());
            taskDto.setDeadline(dateSeparate.getDate());
            taskDto.setDeadTime(dateSeparate.getTime());


            taskDtos.add(taskDto);

        }

        return taskDtos;

    }
}
