package uz.pixel.unicom.entity;


import lombok.*;
import uz.pixel.unicom.enam.Permission;
import uz.pixel.unicom.entity.template.AbsLongEntity;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "unicom_role")
public class Role extends AbsLongEntity {
    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(value = EnumType.STRING)
    @ElementCollection
    private List<Permission> roleList;

    @Column(columnDefinition = "text")
    private String description;
    
}
