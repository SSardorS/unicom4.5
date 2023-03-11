package uz.pixel.unicom.entity;

import lombok.*;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Generated;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.enam.Step;
import uz.pixel.unicom.entity.template.AbsUUIDEntity;


import javax.persistence.*;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "unicom_tasks")
public class Tasks extends AbsUUIDEntity {


    @Column(columnDefinition = "serial")
    @Generated(GenerationTime.ALWAYS)
    private Long codeId;

    @Column(unique = true,nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private boolean step;

    private Integer messgaeId;

    private Date extraDate;

    @OneToOne
    private User manager;

    @OneToOne
    private User user;

    @Column(nullable = false)
    private Date taskExpireDate;

}
