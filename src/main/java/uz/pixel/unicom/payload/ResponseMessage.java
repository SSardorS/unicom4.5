package uz.pixel.unicom.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseMessage {

    private boolean responseBool;
    private String responseText;

    private int status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int totalPage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object list;









    public ResponseMessage(boolean responseBool, String responseText, int status) {
        this.responseBool = responseBool;
        this.responseText = responseText;
        this.status = status;
    }

    public ResponseMessage(boolean responseBool, String responseText, String token) {
        this.responseBool = responseBool;
        this.responseText = responseText;
        this.token = token;
    }
    


    public ResponseMessage(boolean responseBool, String responseText, Object list) {
        this.responseBool = responseBool;
        this.responseText = responseText;
        this.list = list;
    }



    public ResponseMessage(boolean responseBool, String responseText, int status, String token) {
        this.responseBool = responseBool;
        this.responseText = responseText;
        this.status = status;
        this.token = token;
    }

    public ResponseMessage(boolean responseBool, String responseText, int status, Object list) {
        this.responseBool = responseBool;
        this.responseText = responseText;
        this.status = status;
        this.list = list;
    }

    public ResponseMessage(boolean responseBool, String responseText, int status, int totalPage, Object list) {
        this.responseBool = responseBool;
        this.responseText = responseText;
        this.status = status;
        this.totalPage = totalPage;
        this.list = list;
    }
}
