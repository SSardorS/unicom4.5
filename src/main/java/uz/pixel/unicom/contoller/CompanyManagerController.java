package uz.pixel.unicom.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pixel.unicom.payload.ManagerDto;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.payload.UserInfoResult;
import uz.pixel.unicom.service.AuthService;
import uz.pixel.unicom.service.UserInfoService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/users")
public class CompanyManagerController {

    @Autowired
    AuthService authService;

    @Autowired
    UserInfoService userInfoService;


    @PreAuthorize("hasAuthority('ADD_EVERY_THINGS')")
    @PostMapping
    public HttpEntity<?> addManager(@RequestBody ManagerDto managerDto){

        ResponseMessage add = authService.addmanager(managerDto);
        return ResponseEntity.status(add.getStatus()).body(add);

    }


    @PreAuthorize("hasAuthority('GET_USER')")
    @GetMapping("/info")
    public HttpEntity<?> getUserInfo(){


        UserInfoResult userInfo = userInfoService.getUserInfo();
        return ResponseEntity.status(userInfo.getStatus()).body(userInfo);

    }

    @PreAuthorize("hasAuthority('GET_USER')")
    @GetMapping("/list")
    public HttpEntity<?> getEmployers(@RequestParam String pageNumber){

        ResponseMessage employerList = userInfoService.getEmployerList(pageNumber);
        return ResponseEntity.status(employerList.getStatus()).body(employerList);

    }





}
