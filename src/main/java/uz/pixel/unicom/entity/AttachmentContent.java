package uz.pixel.unicom.entity;

import lombok.*;
import uz.pixel.unicom.entity.template.AbsUUIDEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "unicom_attachment_content")
public class AttachmentContent extends AbsUUIDEntity {

    private byte[] attachmentContent;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Attachment attachment;
}

