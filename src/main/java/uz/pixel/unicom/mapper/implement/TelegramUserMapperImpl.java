package uz.pixel.unicom.mapper.implement;

import uz.pixel.unicom.entity.TelegramUser;
import uz.pixel.unicom.mapper.interfaceMapper.TelegramUserMapper;
import uz.pixel.unicom.payload.TelegramUserDto;
import org.springframework.stereotype.Component;

@Component
public class TelegramUserMapperImpl implements TelegramUserMapper {
    @Override
    public TelegramUserDto mapOne(TelegramUser telegramUser) {

        if (telegramUser==null)
            return null;

        TelegramUserDto telegramUserDto = new TelegramUserDto();
        telegramUserDto.setUserName(telegramUser.getUserName());
        telegramUserDto.setActive(telegramUser.isActive());
        telegramUserDto.setChatId(telegramUser.getChatId());
        telegramUserDto.setFullName(telegramUser.getFullName());

        return telegramUserDto;

    }
}
