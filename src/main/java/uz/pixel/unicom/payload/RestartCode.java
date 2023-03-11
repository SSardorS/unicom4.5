package uz.pixel.unicom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestartCode {
    @Valid
    private String password;

    @Valid
    private String prePassword;

    @Valid
    private int code;
}