package uz.pixel.unicom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pixel.unicom.entity.TelegramUser;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TelegramResponse {

    private TelegramUser telegramUser;
    private ResponseMessage responseMessage;

}
