package uz.pixel.unicom.service.miniservice;

import org.springframework.stereotype.Component;
import uz.pixel.unicom.payload.DateParserResponse;
import uz.pixel.unicom.payload.DateSeparate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class DateAndTimeFormatter {


    public DateParserResponse dateParser(String date, String time) {

        String dateFormat = date +"-"+ time;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm");

        DateParserResponse parserResponse = new DateParserResponse();

        try {


            Date parseDate = simpleDateFormat.parse(dateFormat);
            parseDate.setMinutes(0);
            parserResponse.setParse(true);
            parserResponse.setDate(parseDate);
            return parserResponse;

        } catch (Exception e) {

            parserResponse.setParse(false);
            parserResponse.setDate(null);
            return parserResponse;

        }


    }


    public DateSeparate dateSepareteTimeAndDate(Date taskDate){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
        String date = simpleDateFormat.format(taskDate);

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm");
        String time = simpleTimeFormat.format(taskDate);

        return new DateSeparate(date, time);

    }


    public Date dateAddTimeAndDate(String date) throws ParseException {

        Date now = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyyThh:mm");
        Date parse = simpleDateFormat.parse(date);

        if (parse.before(new Date())){
            return null;
        }

        if (now.before(parse)){
            return parse;
        }else {
            return null;
        }


    }

}
