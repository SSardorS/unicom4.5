package uz.pixel.unicom.service;

import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.payload.*;


public abstract class AuthServiceAbs {

    public abstract ResponseMessage registerUser(RegisterDto registerDto);

    public abstract ResponseMessage loginUser(LoginDto loginDto);

    public abstract ResponseMessage verifyEmail(String email, int code);

    public abstract ResponseMessage restartPasswordSending(String email);

    public abstract ResponseMessage restartPasswordGetCode(RestartCode restartCode);

    public abstract ResponseMessage deleteAccauntSendingCode();

    public abstract ResponseMessage deleteAccaunt(DeleteAccauntInfo deleteAccauntInfo) throws Exception;

    public abstract ResponseMessage addmanager(ManagerDto managerDto);

    public abstract ResponseMessage findByIdOruserName(String idOrUsername, User createdBy);

}
