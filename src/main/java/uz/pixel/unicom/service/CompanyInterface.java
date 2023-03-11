package uz.pixel.unicom.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.pixel.unicom.payload.CompanyDto;
import uz.pixel.unicom.payload.ResponseMessage;

import java.io.IOException;
import java.util.UUID;

public interface CompanyInterface {

//    ResponseMessage add(MultipartFile logo, CompanyDto companyDto) throws IOException;

    ResponseMessage add(CompanyDto companyDto);
    ResponseMessage getAll();
    ResponseMessage getById(UUID id);
    ResponseMessage edit(CompanyDto companyDto);
    ResponseMessage delete(UUID id);
}
