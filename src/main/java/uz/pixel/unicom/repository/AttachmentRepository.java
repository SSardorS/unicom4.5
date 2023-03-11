package uz.pixel.unicom.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uz.pixel.unicom.entity.Attachment;

import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    boolean deleteByIdAndCreatedBy_Id(UUID id, UUID createdBy_id);

    Optional<Attachment> findByIdAndCreatedBy_Id(UUID id, UUID createdBy_id);

    boolean deleteAllByCreatedBy_Id(UUID createdBy_id);

    Optional<Attachment> findByCreatedBy_Id(UUID createdBy_id);


}
