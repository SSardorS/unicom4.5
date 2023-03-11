package uz.pixel.unicom.mapper.interfaceMapper;

import uz.pixel.unicom.entity.TelegramUser;
import uz.pixel.unicom.payload.TelegramUserDto;

public interface TelegramUserMapper {

    TelegramUserDto mapOne(TelegramUser telegramUser);

}
