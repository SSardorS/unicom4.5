package uz.pixel.unicom.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pixel.unicom.entity.Company;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Page<Company> findByCreatedById(UUID id, Pageable pageable);


    boolean existsByCompanyNameAndCreatedById(String companyName, UUID userId);

    Optional<Company> findByIdAndCreatedById(UUID id,UUID userId);

    Optional<Company> findByCreatedById(UUID id);

}
