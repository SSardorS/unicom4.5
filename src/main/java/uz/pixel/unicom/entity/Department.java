package uz.pixel.unicom.entity;


import lombok.*;
import uz.pixel.unicom.entity.template.AbsUUIDEntity;

import javax.persistence.*;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "unicom_department")
public class Department extends AbsUUIDEntity {

//    @Column(unique = true)
    private String name;

    private String description;

    @OneToMany
    private List<User> departmentManager;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Company company;

    public Department(String name, String description, Company company) {
        this.name = name;        this.description = description;
        this.company = company;
    }
}
