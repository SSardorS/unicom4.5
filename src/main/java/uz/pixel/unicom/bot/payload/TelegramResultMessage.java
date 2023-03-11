package uz.pixel.unicom.bot.payload;


import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
public class TelegramResultMessage {

    private boolean ok;
    private Message result;


}
