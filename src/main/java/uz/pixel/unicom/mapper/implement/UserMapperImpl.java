package uz.pixel.unicom.mapper.implement;

import org.springframework.stereotype.Component;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.mapper.interfaceMapper.UserMapper;
import uz.pixel.unicom.payload.UserDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public List<UserDto> mapList(List<User> users) {

        List<UserDto> userDtoList = new ArrayList();

        for (User user : users) {

            UserDto userDto = new UserDto();
            userDto.setKey(user.getId());
            userDto.setUsername(user.getUsername());
            userDto.setRole(new ArrayList<>(Arrays.asList(user.getRole().getName())));
            userDto.setFullName(user.getFullName());
            String name;
            try {
                name = user.getDepartment().getName();
            }catch (Exception e){
                name = null;
            }
            userDto.setDepartment(name);

            String telegramUsername;
            try {
                telegramUsername = user.getTelegramUser().getUserName();
            }catch (Exception e){
                telegramUsername = null;
            }
            userDto.setTelegramuser(telegramUsername);

            userDtoList.add(userDto);
        }

        return userDtoList;
    }
}
