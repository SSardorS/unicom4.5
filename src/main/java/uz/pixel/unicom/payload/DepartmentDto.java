package uz.pixel.unicom.payload;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DepartmentDto {

    private UUID id;

    private String description;

    private String name;

    private String departmentManager;

    private UUID companyId;

}
