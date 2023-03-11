package uz.pixel.unicom.service.miniservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import uz.pixel.unicom.bot.payload.TelegramResultMessage;
import uz.pixel.unicom.bot.service.TelegramService;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.entity.Tasks;
import uz.pixel.unicom.entity.TelegramUser;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.repository.TaskRepository;

@Service
public class StatusChangerService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TelegramService telegramService;

    @Autowired
    DateAndTimeFormatter dateAndTimeFormatter;





    public void managerToAdminFinished(Tasks tasks){

        tasks.setStatus(Status.FINISHED_MANAGER);
        taskRepository.save(tasks);

    }

    public ResponseMessage managerToSuperAdminBack(Tasks task, User updateUser){

        task.setStatus(Status.NEW);
        task.setStep(true);
        task.setUpdatedBy(updateUser);
        taskRepository.save(task);
        return new ResponseMessage(true, "Status "+Status.SEND_TO_MANAGER+" changed", 201);

    }


    public ResponseMessage sendToManager(Tasks task, User updatedBy) {

        task.setStatus(Status.SEND_TO_MANAGER);
//        task.setManager(manager);
        task.setUpdatedBy(updatedBy);
        task.setStep(true);
        taskRepository.save(task);

        return new ResponseMessage(true, "Status "+Status.SEND_TO_MANAGER+" changed", 201);

    }

    public ResponseMessage managerToUser(Tasks task, User updateUser, User user) {
        String step = "";
        if (task.isStep()){
            step = "\uD83D\uDFE2";
        }else {
            step = "\uD83D\uDD34";
        }



        String text =
                "Id: "+task.getCode()+"\n"+
                "Title: "+ task.getTitle()+"\n"+
                "Description: "+ task.getDescription()+"\n"+
                "Status: NEW\n"+
                "Step: "+step+"\n"+
                "Order: "+task.getManager().getFullName()+"\n"+
                "Task finished day: "+dateAndTimeFormatter.dateSepareteTimeAndDate(task.getTaskExpireDate()).getDate();

        TelegramResultMessage telegramResultMessage = telegramService.sendMessageForTaskInlineKeyBoard(user.getTelegramUser().getChatId(), text, task.getCode());

        if (telegramResultMessage.isOk()){
            Integer chatId = telegramResultMessage.getResult().getMessageId();

            task.setMessgaeId(chatId);
            task.setStatus(Status.SEND_TO_USER);
            task.setStep(true);
            task.setUser(user);
            task.setUpdatedBy(updateUser);
            taskRepository.save(task);
            return new ResponseMessage(true, "Status "+Status.SEND_TO_USER+" changed", 201);
        }

        return new ResponseMessage(true, ResponseEnum.DONT_SEND.getName(), ResponseEnum.DONT_SEND.getStatus());
    }

    public ResponseMessage managerToUserByAdmin(Tasks task, User updateUser, User user) {

        String step = "";
        if (task.isStep()){
            step = ">>>";
        }else {
            step = "<<<";
        }

        String text =
                "Id: "+task.getCode()+"\n"+
                        "Title: "+ task.getTitle()+"\n"+
                        "Description: "+ task.getDescription()+"\n"+
                        "Status: "+task.getStatus()+"\n"+
                        "Step: "+step+"\n"+
                        "Order: "+task.getManager().getFullName()+"\n"+
                        "Task finished day: "+dateAndTimeFormatter.dateSepareteTimeAndDate(task.getTaskExpireDate()).getDate();

        TelegramResultMessage telegramResultMessage = telegramService.sendMessageForTaskInlineKeyBoard(user.getTelegramUser().getChatId(), text, task.getCode());

        if (telegramResultMessage.isOk()){
            Integer chatId = telegramResultMessage.getResult().getMessageId();

            task.setMessgaeId(chatId);
            task.setUser(user);
            task.setUpdatedBy(updateUser);
            taskRepository.save(task);
            return new ResponseMessage(true, "Status "+Status.SEND_TO_USER+" changed", 201);
        }

        return new ResponseMessage(true, ResponseEnum.DONT_SEND.getName(), ResponseEnum.DONT_SEND.getStatus());

    }

    public ResponseMessage finishedUserBack(Tasks task, User updateUser, User user) {

        String text =
                "Your Task finish was refused! \n"+
                "Id: "+task.getCode()+"\n"+
                        "Title: "+ task.getTitle()+"\n"+
                        "Description: "+ task.getDescription()+"\n"+
                        "Status: "+task.getStatus()+"\n"+
                        "Step: \uD83D\uDD34 \n"+
                        "Order: "+task.getManager().getFullName()+"\n"+
                        "Task finished day: "+dateAndTimeFormatter.dateSepareteTimeAndDate(task.getTaskExpireDate()).getDate();

        TelegramResultMessage telegramResultMessage = telegramService.sendMessageForTaskInlineKeyBoard(user.getTelegramUser().getChatId(), text, task.getCode());

        if (telegramResultMessage.isOk()){
            Integer chatId = telegramResultMessage.getResult().getMessageId();

            task.setMessgaeId(chatId);
            task.setUser(user);
            task.setUpdatedBy(updateUser);
            task.setStep(false);
            taskRepository.save(task);
            return new ResponseMessage(true, "Status "+Status.SEND_TO_USER+" changed", 201);
        }

        return new ResponseMessage(true, ResponseEnum.DONT_SEND.getName(), ResponseEnum.DONT_SEND.getStatus());

    }

    public ResponseMessage managerFinishedBack(Tasks task, User companyOwner) {

        task.setStatus(Status.FINISHED_USER);
        task.setStep(false);
        task.setUpdatedBy(companyOwner);
        taskRepository.save(task);
        return new ResponseMessage(true, "Status "+Status.SEND_TO_MANAGER+" changed", 201);

    }
}
