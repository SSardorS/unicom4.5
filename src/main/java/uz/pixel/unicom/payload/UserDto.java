package uz.pixel.unicom.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDto {

    private UUID key;

    private String username;

    private List<String> role;

    private String fullName;

    private String department;

    private String telegramuser;

}
