package uz.pixel.unicom.contoller;


import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.pixel.unicom.payload.CompanyDto;
import uz.pixel.unicom.payload.ManagerDto;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.service.AuthService;
import uz.pixel.unicom.service.CompanyService;

import java.io.IOException;
import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    CompanyService companyService;


    @PreAuthorize("hasAuthority('ADD_EVERY_THINGS')")
    @PostMapping
    public HttpEntity<?> addCompany(@RequestBody CompanyDto companyDto) {

//        Gson gson = new Gson();
//        CompanyDto companydto = gson.fromJson(companyDto, CompanyDto.class);
        ResponseMessage add = companyService.add(companyDto);
        return ResponseEntity.status(add.getStatus()).body(add);

    }


    @PreAuthorize("hasAuthority('GET_EVERY_THINGS')")
    @GetMapping
    public HttpEntity<?> getAll(){

        ResponseMessage add = companyService.getAll();
        return ResponseEntity.status(add.getStatus()).body(add);

    }


    @PreAuthorize("hasAuthority('GET_EVERY_THINGS')")
    @GetMapping("/{id}")
    public HttpEntity<?> getById(@PathVariable String id){

        ResponseMessage add = companyService.getById(UUID.fromString(id));
        return ResponseEntity.status(add.getStatus()).body(add);

    }
}
