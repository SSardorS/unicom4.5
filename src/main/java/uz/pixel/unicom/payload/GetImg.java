package uz.pixel.unicom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetImg {

    private ForHttpServletResponse httpServletResponse;

    private ResponseMessage responseMessage;
}
