package uz.pixel.unicom.payload;

import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class CompanyDto {

    private UUID id;

    private String companyName;

    private String description;

//    private UUID imgid;

    private List<DepartmentDto> departmentDto;

}
