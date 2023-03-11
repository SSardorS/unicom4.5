package uz.pixel.unicom.entity;

import lombok.*;
import uz.pixel.unicom.entity.template.AbsUUIDEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "unicom_company")
public class Company extends AbsUUIDEntity {

    @Column(nullable = false)
    private String companyName;

    private String description;

//    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Attachment attachment;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Department> department;



    public Company(String companyName, String description) {
        this.companyName = companyName;
        this.description = description;
//        this.attachment = attachment;
    }
}
