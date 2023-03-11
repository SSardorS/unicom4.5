package uz.pixel.unicom.mapper.implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import uz.pixel.unicom.entity.Company;
import uz.pixel.unicom.mapper.interfaceMapper.CompanyMapper;
import uz.pixel.unicom.payload.CompanyDto;
import uz.pixel.unicom.payload.CompanyTotalDto;
import uz.pixel.unicom.payload.DepartmentDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CompanyMapperImpl implements CompanyMapper {

    @Autowired
    DepartmentMapperImpl departmentMapper;

    @Override
    public CompanyTotalDto mapPage(Page<Company> companyPage) {


        List<Company> content = companyPage.getContent();

        List<CompanyDto> companyDtos = new ArrayList<>();

        for (Company company : content) {

            CompanyDto companyDto = new CompanyDto();
            companyDto.setId(company.getId());
            companyDto.setCompanyName(company.getCompanyName());
            companyDto.setDescription(company.getDescription());


            List<DepartmentDto> map = departmentMapper.map(company.getDepartment());

            companyDto.setDepartmentDto(map);
            companyDtos.add(companyDto);

        }


        if (companyDtos.isEmpty())
            return null;

        return new CompanyTotalDto(companyPage.getTotalPages(), companyDtos);

    }

    @Override
    public CompanyDto map(Company company) {

        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(company.getId());
        companyDto.setCompanyName(company.getCompanyName());
        companyDto.setDescription(company.getDescription());


        List<DepartmentDto> map = departmentMapper.map(company.getDepartment());

        companyDto.setDepartmentDto(map);

        return companyDto;
    }

    @Override
    public CompanyDto mapOne(Company company) {

        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(company.getId());
        companyDto.setCompanyName(company.getCompanyName());
        companyDto.setDescription(company.getDescription());

        companyDto.setDepartmentDto(departmentMapper.map(company.getDepartment()));

        return companyDto;

    }

    @Override
    public CompanyDto mapOneWithoutDepartment(Company company) {


        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(company.getId());
        companyDto.setCompanyName(company.getCompanyName());
        companyDto.setDescription(company.getDescription());

//        companyDto.setDepartmentDto(departmentMapper.map(company.getDepartment()));

        return companyDto;

    }

    @Override
    public List<CompanyDto> mapList(List<Company> companies) {

        List<CompanyDto> companyDtos = new ArrayList<>();

        for (Company company : companies) {

            CompanyDto companyDto = new CompanyDto();
            companyDto.setId(company.getId());
            companyDto.setCompanyName(company.getCompanyName());
            companyDto.setDescription(company.getDescription());


            List<DepartmentDto> map = departmentMapper.map(company.getDepartment());

            companyDto.setDepartmentDto(map);
            companyDtos.add(companyDto);

        }


        if (companyDtos.isEmpty())
            return null;

        return companyDtos;

    }
}
