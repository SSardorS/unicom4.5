package uz.pixel.unicom.payload;

import lombok.Data;

import javax.validation.Valid;
import java.util.UUID;

@Data
public class ManagerDto {

    private UUID managerId;


    @Valid
    private String fullName;

    @Valid
    private String username;

    @Valid
    private String telegramuser;

    @Valid
    private String role;

    private UUID companyId;

    private UUID department;

    @Valid
    private String password;

    @Valid
    private String prePassword;

}
