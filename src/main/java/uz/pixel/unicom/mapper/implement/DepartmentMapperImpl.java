package uz.pixel.unicom.mapper.implement;

import org.springframework.stereotype.Component;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.entity.Department;
import uz.pixel.unicom.entity.Role;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.mapper.interfaceMapper.DepartmentMapper;
import uz.pixel.unicom.payload.DepartmentDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class DepartmentMapperImpl implements DepartmentMapper {
    @Override
    public List<DepartmentDto> map(List<Department> departments) {

        if (departments.isEmpty())
            return null;

        List<DepartmentDto> departmentDtos = new ArrayList<>();
        for (Department department : departments) {


            DepartmentDto departmentDt= new DepartmentDto();

            departmentDt.setId(department.getId());
            departmentDt.setName(department.getName());
            departmentDt.setDescription(department.getDescription());

            for (User user : department.getDepartmentManager()) {

                if (user.getRole().getName().equals(RoleBasic.MANAGER.name())){

                    departmentDt.setDepartmentManager(user.getId().toString());

                }

            }

            departmentDt.setCompanyId(department.getCompany().getId());
            departmentDtos.add(departmentDt);

        }
        return  departmentDtos;
    }

    @Override
    public DepartmentDto mapOne(Department department) {

        if (department==null)
            return null;

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(department.getId());
        departmentDto.setName(department.getName());

        for (User user : department.getDepartmentManager()) {

            if (user.getRole().getName().equals(RoleBasic.MANAGER.name())){

                departmentDto.setDepartmentManager(user.getId().toString());

            }

        }

        departmentDto.setCompanyId(department.getCompany().getId());
        return departmentDto;

    }
}
