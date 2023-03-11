package uz.pixel.unicom.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;


import java.util.List;

@AllArgsConstructor
@Getter
public class CompanyTotalDto {

    private int totalpage;

    private List<CompanyDto> companyDtos;

}
