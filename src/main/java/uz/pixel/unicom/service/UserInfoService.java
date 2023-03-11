package uz.pixel.unicom.service;

import org.glassfish.jersey.server.spi.ResponseErrorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.payload.UserInfoResult;
import uz.pixel.unicom.service.miniservice.UserInfoMiniService;

@Service
public class UserInfoService {



    @Autowired
    UserInfoMiniService userInfoMiniService;

    public UserInfoResult getUserInfo(){

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new UserInfoResult(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());


        if (getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return userInfoMiniService.getForAdmin(getuser);

        if (getuser.getRole().getName().equals(RoleBasic.MANAGER.name()))
            return userInfoMiniService.getForManager(getuser);


        return new UserInfoResult(false, ResponseEnum.EMPTY.getName(),ResponseEnum.EMPTY.getStatus());




    }

    public ResponseMessage getEmployerList(String pageNumber){

        User getuser = User.getuser();

        if (!getuser.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());


        if (getuser.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return userInfoMiniService.getEmployersForAdmin(getuser, pageNumber);

        if (getuser.getRole().getName().equals(RoleBasic.MANAGER.name()))
            return userInfoMiniService.getEmployersForManager(getuser, pageNumber);


        return new ResponseMessage(false, ResponseEnum.EMPTY.getName(),ResponseEnum.EMPTY.getStatus());




    }

}
