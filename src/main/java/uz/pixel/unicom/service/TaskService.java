package uz.pixel.unicom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.entity.Company;
import uz.pixel.unicom.entity.Role;
import uz.pixel.unicom.entity.Tasks;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.mapper.implement.TaskMapperImpl;
import uz.pixel.unicom.payload.*;
import uz.pixel.unicom.repository.CompanyRepository;
import uz.pixel.unicom.repository.DepartmentRepository;
import uz.pixel.unicom.repository.TaskRepository;
import uz.pixel.unicom.repository.UserRepository;
import uz.pixel.unicom.service.miniservice.DateAndTimeFormatter;
import uz.pixel.unicom.service.miniservice.TaskChangeStatusService;
import uz.pixel.unicom.service.miniservice.TaskMiniService;

import java.util.*;


@Service
public class TaskService implements TaskInterface {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DateAndTimeFormatter dateAndTimeFormatter;

    @Autowired
    TaskChangeStatusService taskChangeStatusService;

    @Autowired
    TaskMapperImpl taskMapper;
    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    TaskMiniService taskMiniService;
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public ResponseMessage add(TaskDto taskDto) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (getuser.getRole().getName().equals(RoleBasic.USER.name()) || getuser.getRole().getName().equals(RoleBasic.DIRECTOR.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        DateParserResponse expiredDate = dateAndTimeFormatter.dateParser(taskDto.getDeadline(), taskDto.getDeadTime());

        if (!expiredDate.isParse())
            return new ResponseMessage(false, ResponseEnum.DATE_IS_WRONG_FORMAT.getName(), ResponseEnum.DATE_IS_WRONG_FORMAT.getStatus());

//        Optional<Department> departmentOptional = departmentRepository.findById(taskDto.getDepartment());
//
//        if (departmentOptional.isEmpty())
//            return new ResponseMessage(false, ResponseEnum.DEPARTMENT_NOT_FOUND.getName(), ResponseEnum.DEPARTMENT_NOT_FOUND.getStatus());

        Company company = null;

        if (getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name())) {

            Optional<Company> companyOptional = companyRepository.findByCreatedById(getuser.getId());

            if (companyOptional.isEmpty()) {
                return new ResponseMessage(false, "You have not company", 400);
            }

            company = companyOptional.get();

        }

        if (getuser.getRole().getName().equals(RoleBasic.MANAGER.name())) {

            company = getuser.getDepartment().getCompany();

        }

        Optional<User> managerOptional = userRepository.findByIdAndDepartment_Company_IdAndRoleName(taskDto.getManager(), company.getId(), RoleBasic.MANAGER.name());


        if (managerOptional.isEmpty()) {
            return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());
        }




        if (getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name())) {

            return taskMiniService.addTaskForAdmin(taskDto, managerOptional.get(), expiredDate, getuser);

        }

        if (getuser.getRole().getName().equals(RoleBasic.MANAGER.name())) {

            Optional<User> telegramUserOptional = userRepository.findByIdAndDepartment_Company_IdAndRoleName(taskDto.getUser(),company.getId(), RoleBasic.USER.name());

            if (telegramUserOptional.isEmpty()) {
                return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());
            }

            return taskMiniService.addTaskForManager(taskDto, telegramUserOptional.get(), expiredDate, getuser);

        }

        return new ResponseMessage(false, ResponseEnum.SOMETHING_IS_WRONG.getName(), ResponseEnum.SOMETHING_IS_WRONG.getStatus());

    }

    @Override
    public ResponseMessage getAllForSuperAdmin(int pageNumber, Status status) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        PageRequest pageable = PageRequest.of(pageNumber, 15).withSort(Sort.by(Sort.Direction.DESC, "createdAt"));


        Optional<Company> companyOptional = companyRepository.findByCreatedById(getuser.getId());

        if (companyOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.COMPANY_NOT_FOUND.getName(), ResponseEnum.COMPANY_NOT_FOUND.getStatus());

        Page<Tasks> page = taskRepository.findByManager_Department_CompanyIdAndStatus(companyOptional.get().getId(), Status.NEW, pageable);

        if (page.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), page.getTotalPages(), taskMapper.map(page.getContent()));

    }

    @Override
    public ResponseMessage getInPtogressForSuperAdmin(int pageNumber) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        PageRequest pageable = PageRequest.of(pageNumber, 15).withSort(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Status> statusList = new ArrayList<>(
                Arrays.asList(Status.REJECTED, Status.NEW, Status.FINISHED)
        );

        Optional<Company> companyOptional = companyRepository.findByCreatedById(getuser.getId());

        if (companyOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.COMPANY_NOT_FOUND.getName(), ResponseEnum.COMPANY_NOT_FOUND.getStatus());

        Page<Tasks> tasksInProgress = taskRepository.findByManager_Department_CompanyIdAndStatusNotIn(companyOptional.get().getId(), statusList, pageable);

        if (tasksInProgress.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), tasksInProgress.getTotalPages(), taskMapper.mapStatus(tasksInProgress.getContent(), Status.IN_PROGRESS));

    }

    @Override
    public ResponseMessage getAllForSuperManager(int pageNumber, Status status) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.MANAGER.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        PageRequest pageable = PageRequest.of(pageNumber, 15).withSort(Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Tasks> page = taskRepository.findByManagerIdAndStatus(getuser.getId(), status, pageable);

        if (page.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), page.getTotalPages(), page.getContent());

    }

    @Override
    public ResponseMessage getById(String code) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        Optional<Tasks> taskByIdOptional = taskRepository.findByCreatedBy_IdAndCode(getuser.getId(), code);

        return taskByIdOptional.map(tasks -> new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), tasks)).orElseGet(() -> new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus()));

    }

    @Override
    public ResponseMessage changeStatus(TaskChangeStepDto taskChangeStepDto) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        Role role = User.getuser().getRole();

        if (role.getName().equals(RoleBasic.USER.name()) || role.getName().equals(RoleBasic.DIRECTOR.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());


        if (role.getName().equals(RoleBasic.SUPER_ADMIN.name())) {

            return taskChangeStatusService.superAdmin(taskChangeStepDto);

        }

        if (role.getName().equals(RoleBasic.MANAGER.name())) {

            return taskChangeStatusService.managerChangedStatus(taskChangeStepDto);

        }

        return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

    }


    @Override
    public ResponseMessage deleteById(UUID id) {
        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        Optional<Tasks> tasksOptional = taskRepository.findByIdAndCreatedById(id, getuser.getId());

        if (tasksOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.DATA_NOT_FOUND.getName(), 200);

        taskRepository.deleteById(id);

        return new ResponseMessage(true, ResponseEnum.DELETED.getName(), ResponseEnum.DELETED.getStatus());
    }

    @Override
    public ResponseMessage edit(TaskDto taskDto) {
        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());


        Optional<Tasks> tasksOptional = taskRepository.findByCodeAndCreatedById(taskDto.getId(), getuser.getId());

        Date expireDate = null;

        try {
            expireDate = dateAndTimeFormatter.dateAddTimeAndDate(taskDto.getDeadline() + "T" + taskDto.getDeadTime());
        } catch (Exception e) {
            new ResponseMessage(false, "Date is wrong format or Date is old", 200);
        }


        if (tasksOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.DATA_NOT_FOUND.getName(), 200);

        if (!tasksOptional.get().getStatus().name().equals(Status.NEW.name()))
            return new ResponseMessage(false, ResponseEnum.TASK_IS_IN_PROGRESS.getName(), ResponseEnum.TASK_IS_IN_PROGRESS.getStatus());

        if (getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name())) {

            Optional<Company> companyOptional = companyRepository.findByCreatedById(getuser.getId());

            if (companyOptional.isEmpty())
                return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());

            List<User> byDepartmentCompanyCreatedById = userRepository.findByDepartment_Company_CreatedById(getuser.getId());

            Optional<User> managerOptional = userRepository.findByIdAndDepartment_Company_IdAndRoleId(taskDto.getManager(), companyOptional.get().getId(), 2L);

            if (managerOptional.isEmpty())
                return new ResponseMessage(false, ResponseEnum.MANAGER_NOT_FOUND.getName(), ResponseEnum.MANAGER_NOT_FOUND.getStatus());

            return taskMiniService.editTaskForAdmin(taskDto, tasksOptional.get(), managerOptional.get(), getuser, expireDate);

        }

        if (getuser.getRole().getName().equals(RoleBasic.MANAGER.name())) {

            return taskMiniService.editTaskForManager(taskDto, tasksOptional.get(), getuser, expireDate);

        }

        return new ResponseMessage(false, ResponseEnum.SOMETHING_IS_WRONG.getName(), ResponseEnum.SOMETHING_IS_WRONG.getStatus());

    }

    @Override
    public ResponseMessage getAllNew(int pageNumber, Status aNew) {
        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name())) {

            return getAllForSuperAdmin(pageNumber, Status.NEW);

        }
        if (getuser.getRole().getName().equals(RoleBasic.MANAGER.name())) {

            return taskMiniService.getNewForManager(pageNumber, getuser);

        }
        return new ResponseMessage(false, ResponseEnum.SOMETHING_IS_WRONG.getName(), ResponseEnum.SOMETHING_IS_WRONG.getStatus());
    }

    @Override
    public ResponseMessage getAll(int pageNumber) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        PageRequest pageable = PageRequest.of(pageNumber, 15).withSort(Sort.by(Sort.Direction.DESC, "createdAt"));

        if (getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name())) {

            Optional<Company> comapnyOptional = companyRepository.findByCreatedById(getuser.getId());

            if (comapnyOptional.isEmpty())
                return new ResponseMessage(false, ResponseEnum.COMPANY_NOT_FOUND.getName(), ResponseEnum.COMPANY_NOT_FOUND.getStatus());

            Page<Tasks> taskPage = taskRepository.findByManager_Department_CompanyId(comapnyOptional.get().getId(), pageable);

            if (taskPage.isEmpty())
                return new ResponseMessage(true, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

            return new ResponseMessage(false, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), taskPage.getTotalPages(), taskMapper.map(taskPage.getContent()));
        }
        if (getuser.getRole().getName().equals(RoleBasic.MANAGER.name())) {

            Page<Tasks> taskPage = taskRepository.findByManager_DepartmentId(getuser.getDepartment().getId(), pageable);

            if (taskPage.isEmpty())
                return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

            return new ResponseMessage(false, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), taskPage.getTotalPages(), taskMapper.map(taskPage.getContent()));

        }

        if (getuser.getRole().getName().equals(RoleBasic.DIRECTOR.name())) {

            UUID id = getuser.getDepartment().getCompany().getId();

            Page<Tasks> taskPage = taskRepository.findByManager_Department_CompanyId(id, pageable);

            if (taskPage.isEmpty())
                return new ResponseMessage(true, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

            return new ResponseMessage(false, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), taskPage.getTotalPages(), taskMapper.map(taskPage.getContent()));
        }

        return new ResponseMessage(false, ResponseEnum.SOMETHING_IS_WRONG.getName(), ResponseEnum.SOMETHING_IS_WRONG.getStatus());

    }



    public ResponseMessage getInProgress(int pageNumber) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name())) {

            return getInPtogressForSuperAdmin(pageNumber);

        }
        if (getuser.getRole().getName().equals(RoleBasic.MANAGER.name())) {

            return taskMiniService.getInProgress(pageNumber, getuser);

        }

        return new ResponseMessage(false, ResponseEnum.SOMETHING_IS_WRONG.getName(), ResponseEnum.SOMETHING_IS_WRONG.getStatus());

    }

    public ResponseMessage getFinishedAll(int pageNumber, Status finished) {

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name())) {

            return getAllForSuperAdmin(pageNumber, Status.FINISHED);

        }
        if (getuser.getRole().getName().equals(RoleBasic.MANAGER.name())) {

            return taskMiniService.getFinishedForManager(pageNumber, getuser);

        }

        return new ResponseMessage(false, ResponseEnum.SOMETHING_IS_WRONG.getName(), ResponseEnum.SOMETHING_IS_WRONG.getStatus());


    }
}
