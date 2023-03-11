package uz.pixel.unicom.bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.pixel.unicom.bot.controller.body.TelegramControllerBody;

@RestController
@RequestMapping("/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramControllerBody telegramControllerBody;
    String token = "5606993700:AAGElb3Fg8j_DKsrJ0C4wLF3Q-2d2pDKAR4";
    @PostMapping
    public void getUpdates(@RequestBody Update update){
        telegramControllerBody.updateService(update);
    }
}
