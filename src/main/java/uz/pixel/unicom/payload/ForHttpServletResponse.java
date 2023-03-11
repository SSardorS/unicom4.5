package uz.pixel.unicom.payload;


import lombok.Getter;
import lombok.NoArgsConstructor;
import uz.pixel.unicom.entity.Attachment;


@NoArgsConstructor
@Getter
public class ForHttpServletResponse {

    private final String contetmDisposition = "Content-Disposition";

    private String attachmentFile;

    private Attachment attachment;

    private byte[] attachmentContent;



    public void setAttachmentFile(String filename) {

        this.attachmentFile = "attachment; filename=\"" + filename+"\"";
    }

    public void setContentType(Attachment contentType) {
        this.attachment = contentType;
    }

    public void setAttachmentContent(byte[] attachmentContent) {
        this.attachmentContent = attachmentContent;
    }
}
