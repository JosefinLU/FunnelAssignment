package Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
    // 2019-03-01 09:00:00UTC
    // ("MM/dd/yyyy'T'HH:mm:ss:SSS z")
    public String timeStamp;
    public String url;
    public String userId;
    public Date date;

    public Event(String timeStamp, String url, String userId) throws ParseException {
        this.timeStamp = timeStamp;
        this.url = url;
        this.userId = userId;
        this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").parse(timeStamp);
/*
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stringDate = simpleDateFormatter.format(date);


        System.out.println(timeStamp+"\t"+date + " " + stringDate);
*/
    }

}
