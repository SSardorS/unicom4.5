package uz.pixel.unicom.contoller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.payload.UserInfoResult;
import uz.pixel.unicom.service.UserInfoService;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/userinfo")
public class UserInfoController {

    @Autowired
    UserInfoService userInfoService;


    @PreAuthorize("hasAuthority('GET_USER')")
    @GetMapping
    public HttpEntity<?> getAllNew(){


        UserInfoResult userInfo = userInfoService.getUserInfo();
        return ResponseEntity.status(userInfo.getStatus()).body(userInfo);

    }

//    @PreAuthorize("hasAuthority('GET_USER')")
//    @GetMapping("/list")
//    public HttpEntity<?> getEmployers(@RequestParam int pageNumber){
//
//
//        ResponseMessage employerList = userInfoService.getEmployerList(pageNumber);
//        return ResponseEntity.status(employerList.getStatus()).body(employerList);
//
//    }


}
