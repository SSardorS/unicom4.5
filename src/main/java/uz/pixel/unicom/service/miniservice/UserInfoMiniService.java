package uz.pixel.unicom.service.miniservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.entity.Company;
import uz.pixel.unicom.entity.Department;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.mapper.implement.UserMapperImpl;
import uz.pixel.unicom.mapper.interfaceMapper.CompanyMapper;
import uz.pixel.unicom.mapper.interfaceMapper.DepartmentMapper;
import uz.pixel.unicom.mapper.interfaceMapper.TelegramUserMapper;
import uz.pixel.unicom.payload.*;
import uz.pixel.unicom.repository.CompanyRepository;
import uz.pixel.unicom.repository.DepartmentRepository;
import uz.pixel.unicom.repository.TaskRepository;
import uz.pixel.unicom.repository.UserRepository;

import java.util.*;

@Service
public class UserInfoMiniService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentMapper departmentMapper;

    @Autowired
    CompanyMapper companyMapper;

    @Autowired
    TelegramUserMapper telegramUserMapper;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    UserMapperImpl userMapper;


    public UserInfoResult getForAdmin(User user) {

        TelegramUserDto telegramUserDto = telegramUserMapper.mapOne(user.getTelegramUser());

        Optional<Company> companyOptional = companyRepository.findByCreatedById(user.getId());

        if (companyOptional.isEmpty())
            return new UserInfoResult(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        Company company = companyOptional.get();
        CompanyDto companyDto = companyMapper.mapOneWithoutDepartment(company);

        List<DepartmentDto> departmentList = departmentMapper.map(company.getDepartment());


        int managerQuantity = userRepository.countByDepartment_CompanyIdAndRole_Name(company.getId(), RoleBasic.MANAGER.name());
        int directorQuantity = userRepository.countByDepartment_CompanyIdAndRole_Name(company.getId(), RoleBasic.DIRECTOR.name());

        int userQuantity = userRepository.countByDepartment_CompanyIdAndRole_Name(company.getId(), RoleBasic.USER.name());

        int allQuantity = managerQuantity + directorQuantity + userQuantity;

//        for (Department departmentForOne : departmentList) {

//            User departmentManager = departmentForOne.getDepartmentManager();

//            try {
//                if (departmentManager.getRole().equals(RoleBasic.MANAGER.name())){
//
//                    managerQuantity +=1;
//
//                }
//                if (departmentManager.getRole().equals(RoleBasic.USER.name())){
//
//                    userQuantity+=1;
//
//                }
//            }catch (Exception e){
//                managerQuantity=0;
//                userQuantity=0;
//                break;
//            }


//        }

        List<Status> inProgressList = new ArrayList<>(
                Arrays.asList(Status.FINISHED, Status.REJECTED, Status.NEW, Status.FINISHED_MANAGER)
        );

//
//        List<Status> finished = new ArrayList<>(
//                Arrays.asList(Status.FINISHED, Status.FINISHED_USER, Status.FINISHED_MANAGER, Status.IN_PROGRESS)
//        );

        int countFinished = taskRepository.countByManager_Department_CompanyIdAndStatus(company.getId(), Status.FINISHED);

        int countRejected = taskRepository.countByManager_Department_CompanyIdAndStatus(company.getId(), Status.REJECTED);


        int inProgress = taskRepository.countByManager_Department_CompanyIdAndStatusNotIn(company.getId(), inProgressList);

        int allTask = taskRepository.countByManager_Department_CompanyId(company.getId());

        int newTasks = allTask - (countFinished + countRejected + inProgress);


        return new UserInfoResult(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), user.getId(), user.getUsername(), user.getRole().getName(), user.isEnabled(),
                telegramUserDto, companyDto, userQuantity, managerQuantity, directorQuantity, allQuantity, allTask, newTasks, countFinished, inProgress, countRejected, departmentList);


    }


    public UserInfoResult getForManager(User user) {

        TelegramUserDto telegramUserDto = telegramUserMapper.mapOne(user.getTelegramUser());

        Department department = user.getDepartment();

        if (department == null)
            return new UserInfoResult(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        DepartmentDto departmentDto = departmentMapper.mapOne(department);

        Company company = department.getCompany();

        CompanyDto companyDto = companyMapper.mapOneWithoutDepartment(company);


        int managerQuantity = userRepository.countByDepartmentIdAndRole_Name(department.getId(), RoleBasic.MANAGER.name());
        int userQuantity = userRepository.countByDepartmentIdAndRole_Name(department.getId(), RoleBasic.USER.name());

        int allQuantity = managerQuantity + userQuantity;

//        for (Department departmentForOne : departmentList) {

//            User departmentManager = departmentForOne.getDepartmentManager();

//            try {
//                if (departmentManager.getRole().equals(RoleBasic.MANAGER.name())){
//
//                    managerQuantity +=1;
//
//                }
//                if (departmentManager.getRole().equals(RoleBasic.USER.name())){
//
//                    userQuantity+=1;
//
//                }
//            }catch (Exception e){
//                managerQuantity=0;
//                userQuantity=0;
//                break;
//            }


//        }

        List<Status> inProgress = new ArrayList<>(
                Arrays.asList(Status.FINISHED, Status.FINISHED_MANAGER, Status.FINISHED_USER, Status.REJECTED, Status.NEW)
        );

//
//        List<Status> finished = new ArrayList<>(
//                Arrays.asList(Status.FINISHED, Status.FINISHED_USER, Status.FINISHED_MANAGER, Status.IN_PROGRESS)
//        );

        int countFinished = taskRepository.countByManager_DepartmentIdAndStatus(department.getId(), Status.FINISHED);

        int countRejected = taskRepository.countByManager_DepartmentIdAndStatus(department.getId(), Status.REJECTED);

        int countInprogress = taskRepository.countByManager_DepartmentIdAndStatusNotIn(department.getId(), inProgress);

        int allCount = taskRepository.countByManager_DepartmentId(department.getId());

        int newTask = allCount - (countFinished + countRejected + countInprogress);


        return new UserInfoResult(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), user.getId(), user.getUsername(), user.getRole().getName(), user.isEnabled(),
                telegramUserDto, companyDto, userQuantity, managerQuantity, 0, allQuantity, allCount, newTask,countFinished, countInprogress, countRejected, departmentDto);


    }

    public ResponseMessage getEmployersForAdmin(User user, String pageNumber) {

        if (pageNumber.equals("all")) {

            List<User> userListByComapny = userRepository.findByDepartment_Company_CreatedById(user.getId());

            return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), userMapper.mapList(userListByComapny));

        }

        int page;
        try {
            page = Integer.parseInt(pageNumber);
        } catch (Exception e) {
            return new ResponseMessage(false, ResponseEnum.PAGE_NUMBER_PARSE_FAIL.getName(), ResponseEnum.PAGE_NUMBER_PARSE_FAIL.getStatus());
        }
        PageRequest pageable = PageRequest.of(page, 10).withSort(Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userListByComapny = userRepository.findByDepartment_Company_CreatedById(user.getId(), pageable);


        if (userListByComapny.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        List<UserDto> userDtos = userMapper.mapList(userListByComapny.getContent());

        if (userDtos.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), userListByComapny.getTotalPages(), userDtos);

    }

    public ResponseMessage getEmployersForManager(User user, String pageNumber) {

        UUID departmentId = user.getDepartment().getId();

        if (departmentId == null) {
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());
        }


        if (pageNumber.equals("all")) {

            List<User> userListByDepartment = userRepository.findByDepartmentId(departmentId);

            return new ResponseMessage(false, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), userMapper.mapList(userListByDepartment));
        }

        int page;

        try {
            page = Integer.parseInt(pageNumber);
        } catch (Exception e) {
            return new ResponseMessage(false, ResponseEnum.PAGE_NUMBER_PARSE_FAIL.getName(), ResponseEnum.PAGE_NUMBER_PARSE_FAIL.getStatus());
        }

        PageRequest pageable = PageRequest.of(page, 10).withSort(Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<User> userListByDepartment = userRepository.findByDepartmentId(departmentId, pageable);

        if (userListByDepartment.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        List<UserDto> userDtos = userMapper.mapList(userListByDepartment.getContent());

        if (userDtos.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        return new ResponseMessage(false, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), userListByDepartment.getTotalPages(), userDtos);

    }

}
