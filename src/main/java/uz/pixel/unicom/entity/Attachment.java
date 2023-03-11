package uz.pixel.unicom.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import uz.pixel.unicom.entity.template.AbsUUIDEntity;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "unicom_attachment")
public class Attachment extends AbsUUIDEntity {

    private String name;

    private String originalName;

    private Long size;

    private String contentType;

    @JoinColumn(unique = true)
    @OneToOne
    private User owner;

    @OneToOne
    private Company company;

    @JsonIgnore
    @OneToOne(mappedBy = "attachment", cascade = CascadeType.ALL)
    private AttachmentContent attachmentContent;

}
