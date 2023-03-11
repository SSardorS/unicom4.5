package uz.pixel.unicom.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pixel.unicom.payload.*;
import uz.pixel.unicom.service.AuthService;

import javax.ws.rs.PUT;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;



    @PostMapping("/register")
    public HttpEntity<?> register(@RequestBody RegisterDto registerDto){
        ResponseMessage responseMessage = authService.registerUser(registerDto);
        return ResponseEntity.status(responseMessage.getStatus()).body(responseMessage);
    }

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDto loginDto){
        ResponseMessage responseMessage = authService.loginUser(loginDto);
        return ResponseEntity.status(responseMessage.getStatus()).body(responseMessage);
    }

    @PostMapping("/verifyEmail")
    public HttpEntity<?> verifyEmail(@RequestBody VerifyEmailDto verifyEmailDto){

        ResponseMessage responseMessage = authService.verifyEmail(verifyEmailDto.getUserName(), verifyEmailDto.getVerifyCode());
        return ResponseEntity.status(responseMessage.getStatus()).body(responseMessage);
    }

    @PostMapping("/forgetPassword")
    public HttpEntity<?> forgetPassword(@RequestParam String username){
        ResponseMessage responseMessage = authService.restartPasswordSending(username);
        return ResponseEntity.status(responseMessage.getStatus()).body(responseMessage);
    }

    @PostMapping("/changedPassword")
    public HttpEntity<?> changedPassword(@RequestBody RestartCode restartCode) {
        ResponseMessage responseMessage = authService.restartPasswordGetCode(restartCode);
        return ResponseEntity.status(responseMessage.getStatus()).body(responseMessage);
    }


}
