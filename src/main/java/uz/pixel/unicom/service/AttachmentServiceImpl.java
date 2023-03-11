package uz.pixel.unicom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.entity.Attachment;
import uz.pixel.unicom.entity.AttachmentContent;
import uz.pixel.unicom.entity.User;
import uz.pixel.unicom.payload.ForHttpServletResponse;
import uz.pixel.unicom.payload.GetImg;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.repository.AttachmentContentRepository;
import uz.pixel.unicom.repository.AttachmentRepository;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    @Override
    public ResponseMessage add(MultipartFile  logo) throws IOException {

        User user = User.getuser();

        if (!user.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(),ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!user.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(),ResponseEnum.NOT_AVIABLE.getStatus());


        if (logo != null) {
            Attachment attachment = new Attachment();
            attachment.setOriginalName(logo.getOriginalFilename());
            attachment.setName(UUID.randomUUID().toString());
            attachment.setContentType(logo.getContentType());
            attachment.setSize(logo.getSize());
            attachment.setCreatedBy(user);
            attachment.setOwner(user);

            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setAttachmentContent(logo.getBytes());
            attachmentContent.setCreatedBy(user);
            attachmentContent.setAttachment(attachment);

            attachment.setAttachmentContent(attachmentContent);

            attachmentRepository.save(attachment);

            return new ResponseMessage(true, ResponseEnum.SUCCESFULL_CREATED.getName(), ResponseEnum.SUCCESFULL_CREATED.getStatus());
        }
        return new ResponseMessage(false, ResponseEnum.NOT_FINISHED.getName(), ResponseEnum.NOT_FINISHED.getStatus());
    }


    public Attachment add(MultipartFile  logo, User user) throws IOException {



        if (logo != null) {
            Attachment attachment = new Attachment();
            attachment.setOriginalName(logo.getOriginalFilename());
            attachment.setName(UUID.randomUUID().toString());
            attachment.setContentType(logo.getContentType());
            attachment.setSize(logo.getSize());
            attachment.setCreatedBy(user);
            attachment.setOwner(user);

            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setAttachmentContent(logo.getBytes());
            attachmentContent.setCreatedBy(user);
            attachmentContent.setAttachment(attachment);

            attachment.setAttachmentContent(attachmentContent);

            return attachment;


        }
        return null;
    }

    @Override
    public ResponseMessage delete(String id) {

        User user = User.getuser();

        if (!user.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(),ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!user.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(),ResponseEnum.NOT_AVIABLE.getStatus());

        UUID uuid = UUID.fromString(id);

//        Optional<AttachmentContent> byIdAndCreatedBy_id = attachmentContentRepository.findByIdAndCreatedBy_Id(uuid, user.getId());
//
//        if (byIdAndCreatedBy_id.isEmpty())
//            return new ResponseMessage(false, ResponseEnum.IMG_NOT_FOUND.getName(), ResponseEnum.IMG_NOT_FOUND.getStatus());
//
//        AttachmentContent attachmentContent = byIdAndCreatedBy_id.get();
//        attachmentContentRepository.deleteById(attachmentContent.getId());


         boolean basicAttachament = attachmentRepository.deleteByIdAndCreatedBy_Id(uuid, user.getId());

         if (!basicAttachament)
             return new ResponseMessage(false, ResponseEnum.IMG_NOT_FOUND.getName(), ResponseEnum.IMG_NOT_FOUND.getStatus());
        
//        Attachment attachment = attachmentContent.getAttachment();
//
//        if (attachment==null)
//            return new ResponseMessage(false, ResponseEnum.IMG_NOT_FOUND.getName(), ResponseEnum.IMG_NOT_FOUND.getStatus());
//
//        attachmentRepository.deleteById(attachment.getId());

        return new ResponseMessage(true, ResponseEnum.DELETED.getName(), ResponseEnum.DELETED.getStatus());
    }

    @Override
    public GetImg get(String id) {

//         User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//         if (!user.isEnabled()) {
//             ResponseMessage responseMessage = new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());
//             return new GetImg(null, responseMessage);
//         }

        UUID uuid = UUID.fromString(id);

        Optional<AttachmentContent> attachmentContent = attachmentContentRepository.findById(uuid);

        if (!attachmentContent.isPresent()) {
            ResponseMessage responseMessage = new ResponseMessage(false, ResponseEnum.IMG_NOT_FOUND.getName(), ResponseEnum.IMG_NOT_FOUND.getStatus());
            return new GetImg(null, responseMessage);
        }

        AttachmentContent attachmentContent1 = attachmentContent.get();

        Attachment attachmentId = attachmentContent1.getAttachment();

        Optional<Attachment> byId = attachmentRepository.findById(attachmentId.getId());

        if (!byId.isPresent()) {
            ResponseMessage responseMessage = new ResponseMessage(false, ResponseEnum.IMG_NOT_FOUND.getName(), ResponseEnum.IMG_NOT_FOUND.getStatus());
            return new GetImg(null, responseMessage);
        }
        Attachment attachment = byId.get();

        ForHttpServletResponse forHttpServletResponse = new ForHttpServletResponse();
        forHttpServletResponse.setAttachmentFile(attachment.getOriginalName());
        forHttpServletResponse.setContentType(attachment);
        forHttpServletResponse.setAttachmentContent(attachmentContent.get().getAttachmentContent());

        ResponseMessage responseMessage = new ResponseMessage(true, ResponseEnum.SUCCESSULL_GET.getName(), ResponseEnum.SUCCESSULL_GET.getStatus(), byId.get());

        return new GetImg(forHttpServletResponse, responseMessage);


    }


    @Transactional(rollbackFor = Exception.class)
    public boolean deletedAtachamment(UUID userId) throws Exception {

        Optional<Attachment> byCreatedBy_id = attachmentRepository.findByCreatedBy_Id(userId);

        if (!byCreatedBy_id.isPresent())
            return true;

        Attachment attachment = byCreatedBy_id.get();

        try {
            attachmentContentRepository.deleteById(attachment.getAttachmentContent().getId());

            attachmentRepository.delete(attachment);

            return true;
        }catch (Exception e){
            throw new Exception("Img not deleted");
        }



    }

}
