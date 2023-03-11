package uz.pixel.unicom.service.miniservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uz.pixel.unicom.bot.service.TelegramService;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.entity.Department;
import uz.pixel.unicom.entity.Tasks;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.mapper.implement.TaskMapperImpl;
import uz.pixel.unicom.payload.DateParserResponse;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.payload.TaskDto;
import uz.pixel.unicom.payload.TaskDtoForGet;
import uz.pixel.unicom.repository.TaskRepository;
import uz.pixel.unicom.repository.UserRepository;

import java.net.ResponseCache;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Service
public class TaskMiniService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskMapperImpl taskMapper;

    @Autowired
    TelegramService telegramService;

    @Autowired
    DateAndTimeFormatter dateAndTimeFormatter;

    public ResponseMessage addTaskForAdmin(TaskDto taskDto, User manager, DateParserResponse expiredDate, User getuser){

        Tasks tasks = new Tasks();

        String dateAndIdFormatCode = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        tasks.setCode("");
        tasks.setTitle(taskDto.getTaskTitle());
        tasks.setDescription(taskDto.getDescription());
        tasks.setStatus(Status.NEW);
        tasks.setManager(manager);
        tasks.setTaskExpireDate(expiredDate.getDate());
        tasks.setCreatedBy(getuser);
        tasks.setStep(true);

//        Tasks save = taskRepository.save(tasks);

        Tasks taskSaved = taskRepository.save(tasks);

        String code = dateAndIdFormatCode + "-" + tasks.getCodeId();

        taskSaved.setCode(code);

        taskRepository.save(taskSaved);

        return new ResponseMessage(true, ResponseEnum.SUCCESFULL_CREATED.getName(), ResponseEnum.SUCCESFULL_CREATED.getStatus());

    }
    public ResponseMessage addTaskForManager(TaskDto taskDto, User user, DateParserResponse expiredDate, User getuser){

        Tasks tasks = new Tasks();

        String dateAndIdFormatCode = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        tasks.setCode("");
        tasks.setTitle(taskDto.getTaskTitle());
        tasks.setDescription(taskDto.getDescription());
        tasks.setStatus(Status.SEND_TO_USER);
        tasks.setUser(user);
//        tasks.setManager(manager);
        tasks.setTaskExpireDate(expiredDate.getDate());
        tasks.setCreatedBy(getuser);
        tasks.setStep(true);

//        Tasks save = taskRepository.save(tasks);

        Tasks saveTask = taskRepository.save(tasks);

        String code = dateAndIdFormatCode + "-" + tasks.getCodeId();

        saveTask.setCode(code);

        Tasks task = taskRepository.save(saveTask);


        String text =
                "Id: "+task.getCode()+"\n"+
                        "Title: "+ task.getTitle()+"\n"+
                        "Description: "+ task.getDescription()+"\n"+
                        "Status: NEW\n"+
                        "Step: \uD83D\uDFE2 \n"+
                        "Order: "+task.getCreatedBy().getFullName()+"\n"+
                        "Task finished day: "+dateAndTimeFormatter.dateSepareteTimeAndDate(task.getTaskExpireDate()).getDate();

        telegramService.sendMessageForTaskInlineKeyBoard(user.getTelegramUser().getChatId(), text, code);



        return new ResponseMessage(true, ResponseEnum.SUCCESFULL_CREATED.getName(), ResponseEnum.SUCCESFULL_CREATED.getStatus());

    }


    public ResponseMessage editTaskForAdmin(TaskDto taskDto, Tasks tasks,User manager, User getuser, Date expireDate){


        tasks.setTitle(taskDto.getTaskTitle());
        tasks.setDescription(taskDto.getDescription());
        tasks.setManager(manager);
        tasks.setUpdatedBy(getuser);
        tasks.setExtraDate(expireDate);
        taskRepository.save(tasks);
        return new ResponseMessage(true, ResponseEnum.SUCCESSFULL_EDITED.getName(), ResponseEnum.SUCCESSFULL_EDITED.getStatus());

    }

    public ResponseMessage editTaskForManager(TaskDto taskDto, Tasks tasks, User getuser, Date expireDate){

        tasks.setTitle(taskDto.getTaskTitle());
        tasks.setDescription(taskDto.getDescription());
        tasks.setExtraDate(expireDate);
        tasks.setUpdatedBy(getuser);
        taskRepository.save(tasks);

        return new ResponseMessage(true, ResponseEnum.SUCCESSFULL_EDITED.getName(), ResponseEnum.SUCCESSFULL_EDITED.getStatus());

    }


    public ResponseMessage getNewForManager(int pageNumber, User manager) {

        PageRequest pageable = PageRequest.of(pageNumber, 15).withSort(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Status> statusNew = new ArrayList<>(
                Arrays.asList(Status.NEW,Status.FINISHED_MANAGER)
        );

        Page<Tasks> taskNewPage = taskRepository.findByManagerIdAndStatusIn(manager.getId(), statusNew, pageable);

        if (taskNewPage.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), taskNewPage.getTotalPages(),taskMapper.mapStatus(taskNewPage.getContent(), Status.NEW));

    }

    public ResponseMessage getInProgress(int pageNumber, User getuser) {

        List<Status> inprogress = new ArrayList<>(
                Arrays.asList(Status.REJECTED,Status.NEW,Status.FINISHED, Status.FINISHED_USER)
        );


        PageRequest pageable = PageRequest.of(pageNumber, 15).withSort(Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Tasks> tasksPage = taskRepository.findByManagerIdAndStatusNotIn(getuser.getId(), inprogress, pageable);

        if (tasksPage.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), tasksPage.getTotalPages(),taskMapper.mapStatus(tasksPage.getContent(),Status.NEW));


    }

    public ResponseMessage getFinishedForManager(int pageNumber, User getuser) {

        List<Status> finished = new ArrayList<>(
                Arrays.asList(Status.FINISHED, Status.FINISHED_USER, Status.FINISHED_MANAGER, Status.FINISHED)
        );

        PageRequest pageable = PageRequest.of(pageNumber, 15).withSort(Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Tasks> taskPage = taskRepository.findByManagerIdAndStatusIn(getuser.getId(), finished, pageable);

        if (taskPage.isEmpty())
            return new ResponseMessage(false, ResponseEnum.EMPTY.getName(), ResponseEnum.EMPTY.getStatus());

        List<TaskDtoForGet> taskDtos = taskMapper.map(taskPage.getContent());

        return new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), taskPage.getTotalPages(), taskDtos);

    }
}
