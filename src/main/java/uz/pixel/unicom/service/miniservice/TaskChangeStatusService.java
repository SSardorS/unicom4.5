package uz.pixel.unicom.service.miniservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.enam.Step;
import uz.pixel.unicom.entity.Company;
import uz.pixel.unicom.entity.Tasks;
import uz.pixel.unicom.entity.TelegramUser;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.payload.TaskChangeStepDto;
import uz.pixel.unicom.repository.TaskRepository;
import uz.pixel.unicom.repository.TelegramUserRepository;
import uz.pixel.unicom.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class TaskChangeStatusService {


    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Autowired
    StatusChangerService statusChangerService;
    @Autowired
    private UserRepository userRepository;


    public Status getStatus(int id) {

        Status[] values = Status.values();

        for (Status value : values) {

            if (value.getId() == id) {
                return value;
            }
        }
        return null;

    }


    public ResponseMessage managerChangedStatus(TaskChangeStepDto taskChangeStepDto) {

        User updateUser = User.getuser();

        Optional<Tasks> taskOptional = taskRepository.findByCodeAndManager_Department_CompanyId(taskChangeStepDto.getId(), updateUser.getDepartment().getCompany().getId());

        if (taskOptional.isEmpty()) {
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());
        }

        Tasks task = taskOptional.get();


        Company company = updateUser.getDepartment().getCompany();

//        if (!task.getCreatedBy().getDepartment().getCompany().getId().equals(company.getId()))
//            return new ResponseMessage(false, ResponseEnum.DATA_NOT_FOUND.getName(), ResponseEnum.DATA_NOT_FOUND.getStatus());

        Step step = Step.valueOf(taskChangeStepDto.getStep());

        Optional<User> userOptional = userRepository.findByIdAndDepartment_CompanyIdAndRoleName(taskChangeStepDto.getUser(), company.getId(), RoleBasic.USER.name());

        if (userOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());

        User user = userOptional.get();

        if (user.getTelegramUser() == null)
            return new ResponseMessage(false, ResponseEnum.TELEGRAM_USER_NOT_FOUND.getName(), ResponseEnum.TELEGRAM_USER_NOT_FOUND.getStatus());

        switch (step.name()) {
            case "STEP_FORWARD" -> {

                switch (task.getStatus().name()) {
                    case "SEND_TO_MANAGER" -> {
                        return statusChangerService.managerToUser(task, updateUser, user);
                    }
                    default->{

                        return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

                    }
                }

            }
            case "STEP_BACK" -> {

                switch (task.getStatus().name()) {
                    case "SEND_TO_MANAGER"->{

                        return statusChangerService.managerToSuperAdminBack(task, updateUser);

                    }
                    case "FINISHED_USER" ->{

                        return statusChangerService.finishedUserBack(task, updateUser, user);
                    }
                    default->{

                        return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

                    }
                }

            }

        }

        return new ResponseMessage(false, "Status not found", 404);

    }


    public ResponseMessage superAdmin(TaskChangeStepDto taskChangeStepDto) {

//        if (taskChangeStepDto.getUser() == null)
//            return new ResponseMessage(false, ResponseEnum.MANAGER_NOT_FOUND.getName(), ResponseEnum.MANAGER_NOT_FOUND.getStatus());

        User companyOwner = User.getuser();

        Optional<Tasks> taskOptional = taskRepository.findByCodeAndCreatedById(taskChangeStepDto.getId(), companyOwner.getId());

        if (taskOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.TASK_NOT_FOUND.getName(), ResponseEnum.TASK_NOT_FOUND.getStatus());

//        Optional<User> managerOptional = userRepository.findByIdAndCreatedByIdAndRole_Name(taskChangeStepDto.getUser(), companyOwner.getId(), RoleBasic.MANAGER.name());
//
//        if (managerOptional.isEmpty())
//            return new ResponseMessage(false, ResponseEnum.MANAGER_NOT_FOUND.getName(), ResponseEnum.MANAGER_NOT_FOUND.getStatus());

        Tasks task = taskOptional.get();

        Step step = Step.valueOf(taskChangeStepDto.getStep());

        switch (step.name()) {
            case "STEP_FORWARD" -> {

                switch (task.getStatus().name()) {
                    case "NEW" -> {
                        return statusChangerService.sendToManager(task, companyOwner);
                    }
                    default->{

                        return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

                    }
                }

            }
            case "STEP_BACK" -> {

                switch (task.getStatus().name()) {
                    case "SEND_TO_MANAGER" -> {
                        return statusChangerService.managerToSuperAdminBack(task, companyOwner);
                    }
                    case "FINISHED_MANAGER" ->{
                        return statusChangerService.managerFinishedBack(task, companyOwner);
                    }
                    default->{

                        return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

                    }
                }

            }

        }

        return new ResponseMessage(false, "Status not found", 200);


    }
}
