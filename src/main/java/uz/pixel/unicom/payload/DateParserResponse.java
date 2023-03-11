package uz.pixel.unicom.payload;

import lombok.Data;

import java.util.Date;

@Data
public class DateParserResponse {

    private boolean parse;
    private Date date;

}
