package uz.pixel.unicom.bot.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pixel.unicom.bot.payload.TelegramConstantValue;
import uz.pixel.unicom.bot.payload.TelegramResultMessage;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.entity.Tasks;
import uz.pixel.unicom.entity.TelegramUser;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.service.miniservice.DateAndTimeFormatter;
import uz.pixel.unicom.payload.DateSeparate;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.payload.TelegramResponse;
import uz.pixel.unicom.repository.TaskRepository;
import uz.pixel.unicom.repository.TelegramUserRepository;
import uz.pixel.unicom.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class TelegramService {
    private final RestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Autowired
    DateAndTimeFormatter dateAndTimeFormatter;

    public TelegramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void whenStart(Update update) {

        String username = "";
        Chat chat = update.getMessage().getChat();
        if (chat.getUserName().isEmpty()){
            username = chat.getFirstName();
        }

        String text = "\uD83D\uDD30 Welcome to our TM_PRO !\n" +
                "⚜️ Dear "+username+"! We're glad you're using this app.\n" +
                "1️⃣ You first click on the registration button. \n" +
                "\uD83D\uDCAC Then we will give you information for registration.";

        SendMessage sendMessage = new SendMessage();

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton register = new InlineKeyboardButton("Register");
        register.setCallbackData("REGISTER");

        row.add(register);
        rowList.add(row);
        markupInline.setKeyboard(rowList);

        sendMessage.setReplyMarkup(markupInline);

        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(text);
        TelegramResultMessage result = restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, sendMessage, TelegramResultMessage.class);

        getMyTasksAndStatistikKeyBoard(sendMessage);
    }


    public void whenRegister(Update update) {

        Optional<TelegramUser> telegramUserOptional = telegramUserRepository.findByChatId(update.getMessage().getChatId());

        if (telegramUserOptional.isEmpty()) {

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("✅" + update.getMessage().getChat().getUserName() + "!\n" +
                    "✅Your telegram has been registered successfully!");
            TelegramResultMessage result = restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, sendMessage, TelegramResultMessage.class);

            if (result.isOk()) {

                String aboutYou = "\uD83C\uDD94 Your User Id:" + update.getMessage().getChatId() + "\n" +
                        "\uD83D\uDE4E\u200D♂️ Your username: " + update.getMessage().getChat().getUserName() + "\n ✅ This help you for registration";

                SendMessage messageAboutMe = new SendMessage();
                messageAboutMe.setChatId(update.getMessage().getChatId());
                messageAboutMe.setText(aboutYou);
                TelegramResultMessage result2 = restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, messageAboutMe, TelegramResultMessage.class);

                TelegramUser telegramUser = new TelegramUser();
                telegramUser.setUserName(update.getMessage().getChat().getUserName());
                telegramUser.setFullName(update.getMessage().getChat().getFirstName()+" "+update.getMessage().getChat().getLastName());
                telegramUser.setActive(false);
                telegramUser.setChatId(update.getMessage().getChatId());
                telegramUserRepository.save(telegramUser);
                return;
            }
        }

        String text = "\uD83E\uDD73 You are already registered \uD83E\uDD73";
        SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()), text);
        TelegramResultMessage resultMessage = restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, sendMessage, TelegramResultMessage.class);

    }

    public void getMyTasksAndStatistikKeyBoard(SendMessage sendMessage){

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirtRow = new KeyboardRow();
        keyboardFirtRow.add(new KeyboardButton("Get My Tasks"));
        keyboardFirtRow.add(new KeyboardButton("Statistic"));
        keyboard.add(keyboardFirtRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

    }

    public ResponseMessage sendverifyCode(Long chatId, int code, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text + ": " + code);
        TelegramResultMessage result = restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, sendMessage, TelegramResultMessage.class);

        if (result.isOk())
            return new ResponseMessage(true, ResponseEnum.SUCCESSULL_SENDING.getName(), ResponseEnum.SUCCESSULL_SENDING.getStatus());

        return new ResponseMessage(false, ResponseEnum.DONT_SEND.getName(), ResponseEnum.DONT_SEND.getStatus());


    }

    public void sendMessageVoid(Long chatId, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        TelegramResultMessage result = restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, sendMessage, TelegramResultMessage.class);

    }

    public TelegramResultMessage sendMessageReturn(Long chatId, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        return  restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, sendMessage, TelegramResultMessage.class);

    }

    public TelegramResultMessage verifyAccaunt(TelegramUser telegramUser){

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramUser.getChatId());

        String text = "Hi again "+telegramUser.getFullName()+"\n"+
                "You are succesful registered!\nAnd Now You have to verify your accunt\n";

        sendMessage.setText(text);

        /**
         * Inline keyboard
         */

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton verifyMyAccaunt = new InlineKeyboardButton("Verify My Accaunt");
        verifyMyAccaunt.setCallbackData("VERIFY_MY_ACCAUNT");

        row.add(verifyMyAccaunt);
        rowList.add(row);
        markupInline.setKeyboard(rowList);

        sendMessage.setReplyMarkup(markupInline);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        return restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, sendMessage, TelegramResultMessage.class);



    }

    public TelegramResultMessage sendMessageForTaskInlineKeyBoard(Long chatId, String text, String code) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        /**
         * Inline keyboard
         */

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> rowInProgressAndFinished = new ArrayList<>();
        List<InlineKeyboardButton> rowRejectedAndCancel = new ArrayList<>();

        InlineKeyboardButton inProgress = new InlineKeyboardButton("\uD83C\uDFC3progress");
        inProgress.setCallbackData("IN_PROGRESS");
//        inProgress.setText(code);

        InlineKeyboardButton finished = new InlineKeyboardButton("✅finished");
        finished.setCallbackData("FINISHED");
//        finished.setText(code);

        InlineKeyboardButton cancel = new InlineKeyboardButton("❌cancel");
        cancel.setCallbackData("CANCEL");
//        cancel.setText(code);

        InlineKeyboardButton rejected = new InlineKeyboardButton("⏳rejected");
        rejected.setCallbackData("REJECTED");

        rowInProgressAndFinished.add(inProgress);
        rowInProgressAndFinished.add(finished);
        rowRejectedAndCancel.add(cancel);
        rowRejectedAndCancel.add(rejected);

        rowList.add(rowInProgressAndFinished);
        rowList.add(rowRejectedAndCancel);

        markupInline.setKeyboard(rowList);

        sendMessage.setReplyMarkup(markupInline);
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        return restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, sendMessage, TelegramResultMessage.class);


    }


    public TelegramResultMessage editTaskInProgressInlineKeyBoard(Long chatId, String text, Integer messageId) {

        SendMessage editMessageText = new SendMessage();
        editMessageText.setChatId(chatId);
        editMessageText.setText(text);
//        editMessageText.setInlineMessageId(String.valueOf(messageId));

        /**
         * Inline keyboard
         */

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();


//        InlineKeyboardButton inProgress = new InlineKeyboardButton("\uD83C\uDFC3progress");
//        inProgress.setCallbackData("inProgress");
//        inProgress.setText(code);

        InlineKeyboardButton finished = new InlineKeyboardButton("✅finished");
        finished.setCallbackData("FINISHED");
//        finished.setText(code);

//        InlineKeyboardButton cancel = new InlineKeyboardButton("❌cancel");
//        cancel.setCallbackData("cancel");
//        cancel.setText(code);

        InlineKeyboardButton rejected = new InlineKeyboardButton("⏳rejected");
        rejected.setCallbackData("REJECTED");

//        rowInProgressAndFinished.add(inProgress);
        row.add(finished);
        row.add(rejected);

        rowList.add(row);

        markupInline.setKeyboard(rowList);

        editMessageText.setReplyMarkup(markupInline);
        editMessageText.setParseMode(ParseMode.MARKDOWN);

        return restTemplate.postForObject(TelegramConstantValue.TELEGRAM_BASE_URL + TelegramConstantValue.BOT_TOKEN + TelegramConstantValue.TELEGRAM_SEND_MESSAGE, editMessageText, TelegramResultMessage.class);


    }

    public void inProgress(Update update) {

//        String code = update.getCallbackQuery().getMessage().getText();

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        TelegramResponse userIsActive = getUserIsActive(chatId);

        if (!userIsActive.getResponseMessage().isResponseBool()) {

            sendMessageVoid(chatId, userIsActive.getResponseMessage().getResponseText());
            return;

        }

        User user = userIsActive.getTelegramUser().getUser();

        if (user==null)
            sendMessageVoid(chatId, "User no found");


        Optional<Tasks> taskOptional = taskRepository.findByUserIdAndMessgaeIdAndStatus(user.getId(), update.getCallbackQuery().getMessage().getMessageId(), Status.SEND_TO_USER);


        if (taskOptional.isEmpty()) {
            String task = "Task not found";
            sendMessageVoid(chatId, task);
            return;
        }
        Tasks tasks = taskOptional.get();

        deleteMessage(tasks.getUser().getTelegramUser().getChatId(), tasks.getMessgaeId());

        String text =
                "Id: "+tasks.getCode()+"\n"+
                        "Title: "+ tasks.getTitle()+"\n"+
                        "Description: "+ tasks.getDescription()+"\n"+
                        "Status: In Progress\n"+
                        "Step: 🟢 \n"+
                        "Order: "+tasks.getManager().getFullName()+"\n"+
                        "Task finished day: "+dateAndTimeFormatter.dateSepareteTimeAndDate(tasks.getTaskExpireDate()).getDate();


        TelegramResultMessage telegramResultMessage = editTaskInProgressInlineKeyBoard(chatId, text, tasks.getMessgaeId());



        tasks.setStatus(Status.IN_PROGRESS);
        tasks.setMessgaeId(telegramResultMessage.getResult().getMessageId());

        taskRepository.save(tasks);



    }

    public void finished(Update update){

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        TelegramResponse userIsActive = getUserIsActive(chatId);


        if (!userIsActive.getResponseMessage().isResponseBool()) {

            sendMessageVoid(chatId, userIsActive.getResponseMessage().getResponseText());
            return;

        }

        TelegramUser telegramUser = userIsActive.getTelegramUser();

        User user = telegramUser.getUser();

        if (user==null)
            sendMessageVoid(chatId, "User no found");


        Optional<Tasks> taskOptional = taskRepository.findByUserIdAndMessgaeIdAndStatus(user.getId(), update.getCallbackQuery().getMessage().getMessageId(), Status.IN_PROGRESS);


        if (taskOptional.isEmpty()){
            String text= "Task not found";
            sendMessageVoid(telegramUser.getChatId(), text);
        }

        Tasks task = taskOptional.get();

        deleteMessage(telegramUser.getChatId(), task.getMessgaeId());

        String text = "Id: "+task.getCode()+"\nSend to "+task.getManager().getFullName()+"\nStatus: Commit Manager";

        TelegramResultMessage telegramResultMessage = sendMessageReturn(telegramUser.getChatId(), text);

        if (telegramResultMessage.isOk()){
            task.setStatus(Status.FINISHED_USER);
            task.setStep(true);
            task.setUpdatedBy(user);
            taskRepository.save(task);
        }



    }



    public void getMyTasks(Update update) {

        Long chatId = update.getMessage().getChatId();

        TelegramResponse userIsActive = getUserIsActive(chatId);

        if (!userIsActive.getResponseMessage().isResponseBool()) {

            sendMessageVoid(chatId, userIsActive.getResponseMessage().getResponseText());
            return;

        }

        List<Tasks> taskList = taskRepository.findByUserIdAndStatus(userIsActive.getTelegramUser().getId(), Status.SEND_TO_USER);

        if (taskList.isEmpty()){

            String text = "You have not task";

            sendMessageVoid(chatId,text);
            return;

        }

        for (Tasks tasks : taskList) {

            Date taskExpireDate = tasks.getTaskExpireDate();

            DateSeparate dateSeparate = dateAndTimeFormatter.dateSepareteTimeAndDate(taskExpireDate);

            String text = "\uD83C\uDD94Id: " +tasks.getCode()+"\n"+
                    "\uD83D\uDCEETitle: "+ tasks.getTitle()+"\n"+
                    "\uD83D\uDCDDDescription: "+tasks.getDescription()+"\n"+
                    "\uD83D\uDC68\u200D\uD83D\uDCBBTask Order Manager: "+tasks.getManager().getFullName()+"\n"+
                    "\uD83D\uDCC5Status: "+tasks.getStatus().getDescription()+"\n"+
                    "\uD83D\uDCC5Dead Line: "+dateSeparate.getDate()+"\n"+
                    "\uD83D\uDD51Dead Time: "+dateSeparate.getTime()+"\n";

            sendMessageForTaskInlineKeyBoard(chatId, text, tasks.getCode());

        }

    }


    public void cancel(Update update){

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        TelegramResponse userIsActive = getUserIsActive(chatId);

        if (!userIsActive.getResponseMessage().isResponseBool()) {

            sendMessageVoid(chatId, userIsActive.getResponseMessage().getResponseText());
            return;

        }

        Optional<Tasks> taskOptional = taskRepository.findByUserIdAndMessgaeId(userIsActive.getTelegramUser().getId(), update.getCallbackQuery().getMessage().getMessageId());

        if (taskOptional.isEmpty()){

            String text = "You have not task";

            sendMessageVoid(chatId,text);
            return;

        }

        Tasks task = taskOptional.get();

        if (!task.getStatus().equals(Status.SEND_TO_USER)){

            String text = "Task is already in "+task.getStatus()+" status";

            sendMessageVoid(chatId,text);
            return;

        }

        task.setStep(false);
        task.setStatus(Status.SEND_TO_MANAGER);
        task.setUpdatedBy(userIsActive.getTelegramUser().getUser());
        taskRepository.save(task);


    }

    public void activatedAccaunt(Update update){

        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        Optional<TelegramUser> telegranUserOptional = telegramUserRepository.findByChatId(chatId);

        if (telegranUserOptional.isEmpty()){

            sendMessageVoid(chatId, "You have not Accaunt in web site");

        }

        TelegramUser telegramUser = telegranUserOptional.get();

        User user = telegramUser.getUser();

        if (user.getRegisterCode()!=update.getCallbackQuery().getMessage().getMessageId()){

            deleteMessage(chatId, user.getRegisterCode());

        }

        telegramUser.setActive(true);

        deleteMessage(chatId, user.getRegisterCode());

        user.setEnabled(true);
        user.setRegisterCode(0);
        user.setTelegramUser(telegramUser);

        telegramUser.setUser(user);

        userRepository.save(user);
        sendMessageVoid(chatId,"Your Accaunt is Activated");


    }

    public void deleteMessage(Long chatId, int messageId){

        Gson gson = new Gson();


        try {

            URL url = new URL("https://api.telegram.org/bot"+TelegramConstantValue.BOT_TOKEN+"/deleteMessage?chat_id="+chatId+"&message_id="+messageId);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            TelegramResultMessage root = gson.fromJson(bufferedReader, TelegramResultMessage.class);
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            bufferedReader.close();
        } catch (IOException e) {
            return;
        }

    }




    /**
     * Telegram Security
     */



    private TelegramResponse getUserIsActive(Long teleramUserId) {

        Optional<TelegramUser> telegramUserOptional = telegramUserRepository.findByChatId(teleramUserId);

        TelegramResponse telegramResponse = new TelegramResponse();

        if (telegramUserOptional.isEmpty()) {
            telegramResponse.setResponseMessage(new ResponseMessage(false, "Telegram user is not activated", 200));
            return telegramResponse;
        }

        TelegramUser telegramUser = telegramUserOptional.get();

        User user = telegramUser.getUser();

        if (!user.isEnabled()) {
            telegramResponse.setTelegramUser(telegramUser);
            telegramResponse.setResponseMessage(new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus()));
            return telegramResponse;
        }


        if (!telegramUserOptional.get().isActive()) {
            telegramResponse.setTelegramUser(telegramUser);
            telegramResponse.setResponseMessage(new ResponseMessage(false, "Telegram user is not verified", 200));
            return telegramResponse;
        }

        return new TelegramResponse(telegramUser, new ResponseMessage(true, "User is Active",200));

    }


    public void rejected(Update update) {

        Long telegramUserId = update.getCallbackQuery().getMessage().getChatId();

        TelegramResponse userIsActive = getUserIsActive(telegramUserId);

        if (!userIsActive.getResponseMessage().isResponseBool()) {

            sendMessageVoid(telegramUserId, userIsActive.getResponseMessage().getResponseText());
            return;

        }

        String rejectedDay = "Enter the day";

        TelegramResultMessage telegramResultMessage = sendMessageReturn(telegramUserId, rejectedDay);

        Message message = update.getCallbackQuery().getMessage();

        String dayReply = message.getText();

        int day;

        try {

            day = Integer.parseInt(dayReply);

        }catch (Exception e){

            sendMessageVoid(message.getChatId(),"Uniable number");
            return;

        }

        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        User user = userIsActive.getTelegramUser().getUser();

        Optional<Tasks> taskOptional = taskRepository.findByUserIdAndMessgaeId(user.getId(), messageId);

        if (taskOptional.isEmpty())
            sendMessageVoid(telegramUserId, "Task not found");

        Tasks task = taskOptional.get();
        Date taskExpireDate = task.getTaskExpireDate();

        LocalDateTime localDate = LocalDateTime.of(taskExpireDate.getYear(),taskExpireDate.getMonth(), taskExpireDate.getDay(), taskExpireDate.getHours(),0,0);

        LocalDateTime localDateTime = localDate.plusDays(day);

        Date extraDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        task.setExtraDate(extraDate);
        task.setStep(false);
        taskRepository.save(task);

        deleteMessage(telegramUserId, messageId);


//        Update updateReply = new Update();
//
//        updateReply.getMessage()


    }

    public Update updateMessage(){

        Gson gson = new Gson();


        try {

            URL url = new URL("https://api.telegram.org/bot"+TelegramConstantValue.BOT_TOKEN+"/getUpdates");
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            Update update = gson.fromJson(bufferedReader, Update.class);
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            bufferedReader.close();
            return update;
        } catch (IOException e) {
            return null;
        }

    }

}
