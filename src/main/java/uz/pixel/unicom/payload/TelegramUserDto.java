package uz.pixel.unicom.payload;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TelegramUserDto {

    private String userName;
    private String fullName;

    private Long chatId;

    private boolean active;
}
