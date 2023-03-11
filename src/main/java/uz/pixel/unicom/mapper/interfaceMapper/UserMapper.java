package uz.pixel.unicom.mapper.interfaceMapper;

import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.payload.UserDto;

import java.util.List;

public interface UserMapper {

    List<UserDto> mapList(List<User> users);

}
