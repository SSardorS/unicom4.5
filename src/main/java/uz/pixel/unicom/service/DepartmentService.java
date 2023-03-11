package uz.pixel.unicom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.entity.Company;
import uz.pixel.unicom.entity.Department;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.mapper.implement.DepartmentMapperImpl;
import uz.pixel.unicom.mapper.interfaceMapper.DepartmentMapper;
import uz.pixel.unicom.payload.DepartmentDto;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.repository.CompanyRepository;
import uz.pixel.unicom.repository.DepartmentRepository;
import uz.pixel.unicom.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DepartmentService implements DepartmentInterface {

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentMapperImpl departmentMapper;

    @Override
    public ResponseMessage add(DepartmentDto departmentDto) {
        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(),ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(),ResponseEnum.NOT_AVIABLE.getStatus());

        boolean existsDepartment = departmentRepository.existsByNameAndCompanyId(departmentDto.getName(), departmentDto.getCompanyId());

        if (existsDepartment)
            return new ResponseMessage(false, ResponseEnum.ALREADY_EXISTS.getName(), ResponseEnum.ALREADY_EXISTS.getStatus());


        Optional<Company> companyOptional = companyRepository.findById(departmentDto.getCompanyId());

        if (companyOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.COMPANY_NOT_FOUND.getName(), ResponseEnum.COMPANY_NOT_FOUND.getStatus());

//        UUID managerId = UUID.fromString(departmentDto.getDepartmentManager());


//        Optional<User> managerOptional = userRepository.findById(managerId);
//        if (managerOptional.isEmpty())
//            return new ResponseMessage(false, ResponseEnum.MANAGER_NOT_FOUND.getName(), ResponseEnum.MANAGER_NOT_FOUND.getStatus());

        Department department = new Department(departmentDto.getName(), departmentDto.getDescription(), companyOptional.get());
        department.setCreatedBy(getuser);

        departmentRepository.save(department);

        return new ResponseMessage(true, ResponseEnum.SUCCESFULL_CREATED.getName(), ResponseEnum.SUCCESFULL_CREATED.getStatus());

    }

    @Override
    public ResponseMessage getAll(int pageNumber) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(),ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(),ResponseEnum.NOT_AVIABLE.getStatus());

        PageRequest page = PageRequest.of(pageNumber, 15).withSort(Sort.by(Sort.Direction.DESC, "name"));
        Page<Department> departmentList = departmentRepository.findByCreatedById(getuser.getId(), page);

        if (departmentList.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());


        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(),departmentList.getTotalPages(),departmentMapper.map(departmentList.getContent()));

    }

    @Override
    public ResponseMessage getById(UUID id) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(),ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(),ResponseEnum.NOT_AVIABLE.getStatus());

        Optional<Department> departmentOptional = departmentRepository.findByIdAndCreatedById(id, getuser.getId());

        return departmentOptional.map(department -> new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), departmentMapper.mapOne(department))).orElseGet(() -> new ResponseMessage(false, ResponseEnum.DEPARTMENT_NOT_FOUND.getName(), ResponseEnum.DEPARTMENT_NOT_FOUND.getStatus()));

    }

    @Override
    public ResponseMessage edit(DepartmentDto departmentDto) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(),ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(),ResponseEnum.NOT_AVIABLE.getStatus());

        Optional<Company> companyOptional = companyRepository.findByIdAndCreatedById(departmentDto.getCompanyId(), getuser.getId());

        if (companyOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.COMPANY_NOT_FOUND.getName(), ResponseEnum.COMPANY_NOT_FOUND.getStatus());


        Optional<Department> department = departmentRepository.findByIdAndCreatedById(departmentDto.getId(), getuser.getId());

        if (department.isEmpty())
            return new ResponseMessage(false, ResponseEnum.DEPARTMENT_NOT_FOUND.getName(), ResponseEnum.DEPARTMENT_NOT_FOUND.getStatus());

        Department departmentEdit = department.get();

        departmentEdit.setName(departmentDto.getName());
        departmentEdit.setCompany(companyOptional.get());
        departmentEdit.setUpdatedBy(getuser);

        departmentRepository.save(departmentEdit);

        return new ResponseMessage(true, ResponseEnum.SUCCESSFULL_EDITED.getName(), ResponseEnum.SUCCESSFULL_EDITED.getStatus());

    }

    @Override
    public ResponseMessage delete(UUID id) {
        return null;
    }
}
