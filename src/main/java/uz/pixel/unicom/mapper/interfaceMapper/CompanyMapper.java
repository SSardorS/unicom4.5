package uz.pixel.unicom.mapper.interfaceMapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import uz.pixel.unicom.entity.Company;
import uz.pixel.unicom.payload.CompanyDto;
import uz.pixel.unicom.payload.CompanyTotalDto;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CompanyMapper {

    CompanyTotalDto mapPage(Page<Company> company);
    CompanyDto map(Company company);

    CompanyDto mapOne(Company company);
    CompanyDto mapOneWithoutDepartment(Company company);

    List<CompanyDto> mapList(List<Company> companies);

}
