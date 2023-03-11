package uz.pixel.unicom.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.entity.Tasks;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TaskRepository extends JpaRepository<Tasks, UUID> {


    Page<Tasks> findByCreatedByIdAndStatus(UUID createdBy_id, Status status, Pageable pageable);

    Page<Tasks> findByManagerIdAndStatus(UUID managerId, Status status, Pageable pageable);

    Optional<Tasks> findByCreatedBy_IdAndCode(UUID createdBy_id, String code);

    Optional<Tasks> findByIdAndCreatedById(UUID id, UUID createdBy_id);

    Optional<Tasks> findByCodeAndCreatedById(String code, UUID createdBy);
    Optional<Tasks> findByCode(String code);

    List<Tasks> findByUserIdAndStatus(UUID id, Status status);

//    Optional<Tasks> findByUserIdAndCode(UUID userId, String code);
    Page<Tasks> findByManagerIdAndStatusIn(UUID manager_id, List<Status> status, Pageable pageable);
    Page<Tasks> findByManagerIdAndStatusNotIn(UUID manager_id, List<Status> status, Pageable pageable);

    Optional<Tasks> findByIdAndManagerId(UUID id, UUID manager);

    Optional<Tasks> findByManagerIdAndCode(UUID manager_id, String code);

    Page<Tasks> findByManager_Department_IdAndStatus(UUID manager_department_id, Status status, Pageable pageable);

    Page<Tasks> findByManager_Department_CompanyIdAndStatusIn(UUID manager_department_company_id, Collection<Status> status, Pageable pageable);

    int countByManager_Department_CompanyIdAndStatus(UUID manager_department_company_id, Status status);

    Optional<Tasks> findByCodeAndManager_Department_CompanyId(String code, UUID manager_department_company_id);

    int countByManager_DepartmentIdAndStatus(UUID manager_department_id, Status status);

    int countByManager_Department_CompanyIdAndStatusNotIn(UUID manager_department_company_id, List<Status> status);

    int countByManager_Department_CompanyId(UUID manager_department_company_id);
    int countByManager_DepartmentIdAndStatusNotIn(UUID manager_department_company_id, List<Status> status);
    int countByManager_DepartmentId(UUID manager_department_company_id);
    Page<Tasks> findByManager_DepartmentIdAndStatusNotIn(UUID manager_department_company_id, Collection<Status> status, Pageable pageable);

    Page<Tasks> findByManager_Department_CompanyIdAndStatusNotIn(UUID manager_department_company_id, Collection<Status> status, Pageable pageable);

//    int countByUser_department_companyIdAndStatusNotIn(UUID companyId, String status);

    Optional<Tasks> findByUserIdAndMessgaeIdAndStatus(UUID user_id, Integer messgaeId, Status status);
    Optional<Tasks> findByUserIdAndMessgaeId(UUID user_id, Integer messgaeId);


    Page<Tasks> findByManager_Department_CompanyId(UUID manager_department_company_id, Pageable pageable);

    Page<Tasks> findByManager_DepartmentId(UUID manager_department_id, Pageable pageable);

    Page<Tasks> findByManager_Department_CompanyIdAndStatus(UUID id, Status finished, Pageable pageable);
}
