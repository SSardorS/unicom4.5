package uz.pixel.unicom.payload;

import lombok.Data;

@Data
public class VerifyEmailDto {
    private String userName;
    private int verifyCode;
}
