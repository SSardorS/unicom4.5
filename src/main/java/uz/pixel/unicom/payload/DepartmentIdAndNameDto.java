package uz.pixel.unicom.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class DepartmentIdAndNameDto {

    private UUID id;

    private String name;

}
