package uz.pixel.unicom.mapper.interfaceMapper;

import org.mapstruct.Mapper;
import uz.pixel.unicom.entity.Department;
import uz.pixel.unicom.payload.DepartmentDto;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    List<DepartmentDto> map(List<Department> departments);

    DepartmentDto mapOne(Department department);

}
