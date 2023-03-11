package uz.pixel.unicom.entity.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import uz.pixel.unicom.config.AuditingListener;
import uz.pixel.unicom.entity.User;


import javax.persistence.*;
import java.sql.Timestamp;


@MappedSuperclass
@Data
@EntityListeners(value = AuditingListener.class)
public class AbsLongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @JsonIgnore
    @UpdateTimestamp
    private Timestamp updatedAt;

    @JsonIgnore
    @CreatedBy
    @JoinColumn(updatable = false)
    @ManyToOne
    private User createdBy;

    @JsonIgnore
    @LastModifiedBy
    @ManyToOne
    private User updatedBy;
}
