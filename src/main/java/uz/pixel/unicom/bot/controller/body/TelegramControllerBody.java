package uz.pixel.unicom.bot.controller.body;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pixel.unicom.bot.service.TelegramService;

@Service
@RequiredArgsConstructor
public class TelegramControllerBody {

    private final TelegramService telegramService;


    public void updateService(Update update) {

        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            switch (text) {
                case "/start":
                    telegramService.whenStart(update);
                    break;

                case "Get may tasks":
                    telegramService.getMyTasks(update);
                    break;
            }
        }

        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();

            switch (data) {
                case "IN_PROGRESS":

                    telegramService.inProgress(update);
                    break;

                case "FINISHED":

                    telegramService.finished(update);

                    break;

                case "CANCEL":

                    telegramService.cancel(update);

                    break;
                case "VERIFY_MY_ACCAUNT":

                    telegramService.activatedAccaunt(update);

                    break;
                case "REGISTER":
                    telegramService.whenRegister(update);
                    break;
                case "REJECTED":
                    telegramService.rejected(update);
                    break;

            }

        }

    }

}
