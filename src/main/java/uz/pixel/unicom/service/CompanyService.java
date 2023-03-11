package uz.pixel.unicom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.entity.*;
import uz.pixel.unicom.mapper.implement.CompanyMapperImpl;
import uz.pixel.unicom.payload.CompanyDto;
import uz.pixel.unicom.payload.CompanyTotalDto;
import uz.pixel.unicom.payload.DepartmentDto;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.repository.CompanyRepository;

import java.io.IOException;
import java.util.*;

@Service
public class CompanyService implements CompanyInterface {
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    AttachmentServiceImpl attachmentService;

    @Autowired
    AuthService authService;

    @Autowired
    CompanyMapperImpl companyMapper;


//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public ResponseMessage add(MultipartFile logo, CompanyDto companyDto) throws IOException {
//
//        User getuser = User.getuser();
//
//        if (!getuser.isEnabled())
//            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());
//
//        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
//            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());
//
//        Optional<Company> companyOptional = companyRepository.findByCreatedById(getuser.getId());
//
//        if (companyOptional.isPresent())
//            return new ResponseMessage(false, ResponseEnum.ALREADY_EXISTS.getName(), ResponseEnum.ALREADY_EXISTS.getStatus());
//
//
//
//        /**
//         *
//         * Company saved
//         *
//         */
//
//        Attachment attachment = attachmentService.add(logo, getuser);
//        Company company = new Company(companyDto.getCompanyName(), companyDto.getDescription(), attachment);
//
////        List<Department> departmentList = new ArrayList<>();
////
////        for (DepartmentDto departmentDto : companyDto.getDepartmentDto()) {
////
////            ResponseMessage managerOptional = authService.findByIdOruserName(departmentDto.getDepartmentManager(), getuser);
////
////            User manager = (User) managerOptional.getList();
////
////            if (manager == null)
////                return new ResponseMessage(false, departmentDto.getName()+" Manager not found", 200);
////
////            Department department = new Department();
////
////            department.setName(departmentDto.getName());
////            department.setDepartmentManager(manager);
////            department.setCreatedBy(getuser);
////            department.setCompany(company);
////            departmentList.add(department);
////
////        }
////        company.setDepartment(departmentList);
//
//        attachment.setCompany(company);
//
//        company.setCreatedBy(getuser);
//        companyRepository.save(company);
//
//        return new ResponseMessage(true, ResponseEnum.SUCCESFULL_CREATED.getName(), ResponseEnum.SUCCESFULL_CREATED.getStatus());
//
//
//    }
//

    @Override
    public ResponseMessage add(CompanyDto companyDto){

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        Optional<Company> companyOptional = companyRepository.findByCreatedById(getuser.getId());

        if (companyOptional.isPresent())
            return new ResponseMessage(false, ResponseEnum.ALREADY_EXISTS.getName(), ResponseEnum.ALREADY_EXISTS.getStatus());

        Company company = new Company(companyDto.getCompanyName(), companyDto.getDescription());
        company.setCreatedBy(getuser);
        companyRepository.save(company);

        return new ResponseMessage(true, ResponseEnum.SUCCESFULL_CREATED.getName(), ResponseEnum.SUCCESFULL_CREATED.getStatus());

    }
    @Override
    public ResponseMessage getAll() {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());


//        PageRequest page = PageRequest.of(pageNumber, 15).withSort(Sort.by(Sort.Direction.DESC, "companyName"));

        Optional<Company> companyPage = companyRepository.findByCreatedById(getuser.getId());

        if (companyPage.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.name(), ResponseEnum.EMPTY.getStatus());

        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.name(), ResponseEnum.SUCCESSULL_GET.getStatus(), companyMapper.map(companyPage.get()));
    }

    @Override
    public ResponseMessage getById(UUID id) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        Optional<Company> companyOptional = companyRepository.findByIdAndCreatedById(id, getuser.getId());

        if (companyOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.name(), ResponseEnum.EMPTY.getStatus());

        CompanyDto companyDto = companyMapper.mapOne(companyOptional.get());

        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), companyDto);

    }

    @Override
    public ResponseMessage edit(CompanyDto companyDto) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        Optional<Company> companyEdit = companyRepository.findByIdAndCreatedById(companyDto.getId(), getuser.getId());

        if (companyEdit.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        Company company = companyEdit.get();

        company.setCompanyName(companyDto.getCompanyName());
        company.setDescription(companyDto.getDescription());

        companyRepository.save(company);

        return new ResponseMessage(false, ResponseEnum.SUCCESSULL_CHANGED.getName(), ResponseEnum.SUCCESSULL_CHANGED.getStatus());

    }

    @Override
    public ResponseMessage delete(UUID id) {
        return null;
    }
}
