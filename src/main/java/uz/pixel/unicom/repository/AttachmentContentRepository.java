package uz.pixel.unicom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pixel.unicom.entity.AttachmentContent;

import java.util.Optional;
import java.util.UUID;

public interface AttachmentContentRepository extends JpaRepository<AttachmentContent, UUID> {
    boolean deleteByIdAndCreatedBy_Id(UUID id, UUID createdBy_id);

    Optional<AttachmentContent> findByCreatedBy_Id(UUID createdBy_id);
    
    Optional<AttachmentContent> findByIdAndCreatedBy_Id(UUID id, UUID createdBy_id);

    boolean deleteAllByCreatedBy_Id(UUID createdBy_id);
}
