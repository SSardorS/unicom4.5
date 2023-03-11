package uz.pixel.unicom.mapper.interfaceMapper;

import org.mapstruct.Mapper;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.entity.Tasks;
import uz.pixel.unicom.payload.TaskDto;
import uz.pixel.unicom.payload.TaskDtoForGet;

import java.util.List;

@Mapper
public interface TaskMapper {

    List<TaskDtoForGet> map(List<Tasks> tasks);

    List<TaskDtoForGet> mapStatus(List<Tasks> tasks, Status status);



}
