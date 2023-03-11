package uz.pixel.unicom.enam;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum ResponseEnum {

    PASSWORD_IS_NOT_SAME("Password is not Same",400),
    LOGIN_PASSWORD_ARE_WRONG("Login or Password are wrong",409),
    USER_ALREADY_EXIST("User is already exist",409),
    DATA_NOT_FOUND("Data not found",404),
    SUCCESSFULL_ENTERED("Successfull",200),
    ACCAUNT_ACTIVATED("Accaunt activated",202),
    WRONG_CODE("Wrong code",409),
    SUCCESSULL_SENDING("Successfull sending",200) ,
    SUCCESSULL_CHANGED("Successfull changed",200),
    ACCAUNT_IS_NOT_VERIFIED("Accaunt is not verified",401),
    FOR_DIRECTOR("For Director",200),
    SUCCESSULL_GET("Successfull get",200),
    SUCCESFULL_CREATED("Successfull created",201),
    SUCCESSFULL_EDITED("Success edited",200),
    DELETED("Deleted",200),
    EMPTY("Empty",200),

    SOMETHING_IS_WRONG("Something is wrong",409),
    IMG_NOT_DELETED("Img not deleted",409),
    TO_DO_NOT_DELETED("To do not deleted",409),


    NOT_FINISHED("Not finished",409),

    USER_IS_NOT_AVAILABLE("User is not available",400),

    USER_NOT_FOUND("User not found",400),
    TO_DO_NOT_FOUND("To do not found",400),

    FINISHED("Finished",200),

    NOT_AVIABLE("Not aviable",403),
    IMG_NOT_FOUND("Img not found",404),
    YOUR_WORK_TODAY_YES("Your work today",200),

    YOUR_WORK_TODAY_NO("Don't have work today",200),

    ALREADY_EXISTS("Already exists",400),

    COMPANY_NOT_FOUND("Company not found",400),
    MANAGER_NOT_FOUND("Manager not found",400),

    DEPARTMENT_NOT_FOUND("Department not found",400),

    DATE_IS_WRONG_FORMAT("Date is wrong format", 400),
    TELEGRAM_USER_NOT_FOUND("Telegram user not found", 400),
    TASK_IS_IN_PROGRESS("Task is in progress", 200),
    PAGE_NUMBER_PARSE_FAIL("page number parse fail", 400),

    DONT_SEND("Don't send message", 400),

    TASK_NOT_FOUND("Task not found",404);


    final String name;
    final int status;


    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }
}
