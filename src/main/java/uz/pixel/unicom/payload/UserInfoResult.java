package uz.pixel.unicom.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.pixel.unicom.entity.Company;
import uz.pixel.unicom.entity.TelegramUser;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoResult {

    private boolean responseBool;

    private String responseMessage;

    private int status;

    private UUID userId;

    private String userName;

    private String role;

    private boolean isActive;

    private TelegramUserDto telegramUser;

    private CompanyDto company;

    private int companyUsers;

    private int companyManager;
    private int companyDirector;
    private int allEmployer;

    private int companyAllTasks;

    private int companyNewTasks;

    private int companySuccesfullyTasks;

    private int companyInProgressTasks;

    private int companyRejectedTasks;
    private Object department;



    public UserInfoResult(boolean responseBool, String responseMessage, int status) {
        this.responseBool = responseBool;
        this.responseMessage = responseMessage;
        this.status = status;
    }
}


