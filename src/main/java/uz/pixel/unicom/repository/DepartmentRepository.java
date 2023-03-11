package uz.pixel.unicom.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pixel.unicom.entity.Department;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;


public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    boolean existsByNameAndCompanyId(String name, UUID company_id);

    Page<Department> findByCreatedById(UUID id,Pageable pageable);

    Optional<Department> findByIdAndCreatedById(UUID id, UUID createdBy_id);

    Optional<Department> findByDepartmentManagerIdAndDepartmentManager_Role_Name(UUID departmentManager_id, String departmentManager_role_name);


}
