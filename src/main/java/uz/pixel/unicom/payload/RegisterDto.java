package uz.pixel.unicom.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterDto {

    @Valid
    private String fullName;

    @Valid
    private String userName;

    @Valid
    private String telegramUserid;

    @Valid
    private String password;

    @Valid
    private String prePassword;
}
