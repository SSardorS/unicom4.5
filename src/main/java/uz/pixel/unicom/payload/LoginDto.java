package uz.pixel.unicom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDto {

    @Valid
    private String userName;

    @Valid
    private String password;
}
