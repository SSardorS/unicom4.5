package uz.pixel.unicom.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uz.pixel.unicom.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, UUID> {


    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByRestartPasswordCode(int restartPasswordCode);

    Optional<User> findByDeleteAccauntCodeAndUsername(int deleteAccauntCode, String username);

    Optional<User> findByIdAndCreatedById(UUID id, UUID createdBy_id);

    Optional<User> findByIdOrUsernameAndCreatedById(UUID id, String username, UUID createdBy);

    int countByDepartment_CompanyIdAndRole_Name(UUID department_company_id, String role_name);

    int countByDepartmentIdAndRole_Name(UUID department_id, String role_name);

    Page<User> findByDepartment_Company_CreatedById(UUID department_company_createdBy_id, Pageable pageable);
    List<User> findByDepartment_Company_CreatedById(UUID department_company_createdBy_id);

    Optional<User> findByIdAndDepartment_CompanyIdAndRoleName(UUID id, UUID department_company_id, String role_name);

    Optional<User> findByIdAndCreatedByIdAndRole_Name(UUID id, UUID createdBy_id, String role_name);


    Page<User> findByDepartmentId(UUID department_id, Pageable pageable);

    List<User> findByDepartmentId(UUID department_id);

    Optional<User> findByDepartmentIdAndDepartment_DepartmentManagerId(UUID department_id, UUID manager);

    Optional<User> findByIdAndDepartment_Company_IdAndRoleName(UUID id, UUID department_company_id, String role_name);
    Optional<User> findByIdAndDepartment_Company_IdAndRoleId(UUID id, UUID department_company_id, Long role_id);

}
