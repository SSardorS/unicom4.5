package uz.pixel.unicom.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import uz.pixel.unicom.payload.GetImg;
import uz.pixel.unicom.payload.ResponseMessage;

import java.io.IOException;
import java.util.UUID;

public interface AttachmentService {
    ResponseMessage add(MultipartFile request) throws IOException;

    ResponseMessage delete(String id);

    GetImg get(String id);

    boolean deletedAtachamment(UUID userId) throws Exception;
}
