package uz.pixel.unicom.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.pixel.unicom.payload.GetImg;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.repository.AttachmentContentRepository;
import uz.pixel.unicom.repository.AttachmentRepository;
import uz.pixel.unicom.service.AttachmentServiceImpl;

import java.io.IOException;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    @Autowired
    AttachmentServiceImpl attachmentService;


    @PreAuthorize("hasAuthority('ADD_EVERY_THINGS')")
    @PostMapping
    public HttpEntity<?> add(@RequestParam("logo") MultipartFile request) throws IOException {
        ResponseMessage add = attachmentService.add(request);
        return ResponseEntity.status(add.getStatus()).body(add);
    }

    @PreAuthorize("hasAuthority('DELETE_EVERY_THINGS')")
    @DeleteMapping("/{id}")
    public HttpEntity<?> delete(@PathVariable String id){
        ResponseMessage delete = attachmentService.delete(id);
        return ResponseEntity.status(delete.getStatus()).body(delete);
    }

//    @PreAuthorize("hasAuthority('ADD_ATTACHMENT')")
    @GetMapping("/{attachmentId}")
    public HttpEntity<?> get(@PathVariable String attachmentId) {

        GetImg getImg =  attachmentService.get(attachmentId);

        if (getImg.getHttpServletResponse().getAttachmentFile().isEmpty())
            return ResponseEntity.status(getImg.getResponseMessage().getStatus()).body(getImg.getResponseMessage());

        return ResponseEntity.status(getImg.getResponseMessage().getStatus())
                .contentType(MediaType.valueOf(getImg.getHttpServletResponse().getAttachment().getContentType()))
                .body(getImg.getHttpServletResponse().getAttachmentContent());

//        if (getImg.getHttpServletResponse().getAttachmentFile().isEmpty())
//            return ResponseEntity.status(getImg.getResponseMessage().getStatus()).body(getImg.getResponseMessage());
//
//        ForHttpServletResponse httpServletResponse = getImg.getHttpServletResponse();
//        response.setHeader(httpServletResponse.getContetmDisposition(),httpServletResponse.getAttachmentFile());
//        response.setContentType(httpServletResponse.getAttachment().getContentType());
//
//        FileCopyUtils.copy(httpServletResponse.getAttachmentContent(),response.getOutputStream());
//
//        return ResponseEntity.status(getImg.getResponseMessage().getStatus()).body(getImg.getResponseMessage());





//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//
//        Optional<AttachmentContent> attachmentContent = attachmentContentRepository.findByCreatedBy_Id(user.getId());
//
//
//        AttachmentContent attachmentContent1 = attachmentContent.get();
//
//        Attachment attachmentId = attachmentContent1.getAttachment();
//
//        Optional<Attachment> byId = attachmentRepository.findById(attachmentId.getId());
//
//
//        Attachment attachment = byId.get();
//
//
//        response.setHeader("Content-Disposition","attachment; filename=\"" + attachment.getOriginalName()+"\"");
//        response.setContentType(attachment.getContentType());
//
//        FileCopyUtils.copy(attachmentContent1.getAttachmentContent(),response.getOutputStream());

    }
}
