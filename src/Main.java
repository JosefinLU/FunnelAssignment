import Event.Event;
import jdk.jfr.StackTrace;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        // args[0]... replace the path, fromDateString, toDateString

        if(args.length < 3) {
            System.out.println("Pls include: file path, from date and to date");
            System.exit(1);
        }

        // /Users/josefinlundquist/Desktop/log.txt 2019-03-01 09:00:00UTC 2019-03-02 11:59:59UTC

        String path = args[0]; //"/Users/josefinlundquist/Desktop/log.txt";
        String fromDateString = args[1]; //"2019-03-01 09:00:00UTC";
        String toDateString = args[2]; //"2019-03-02 11:59:59UTC";

        Date fromDate = null;
        Date toDate = null;

        try {
            fromDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").parse(fromDateString);
            toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").parse(toDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Scanner sc = null;
        File file = new File(path);
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //To stop execution if it catch an exception
            System.exit(1);
        }
        if (sc.hasNextLine()){
            //Call nextLine to start on next line
            sc.nextLine();
        }

        List<Event> events = new ArrayList();

        // check if there is a next line
        while (sc.hasNextLine()){
            // Split the line, we read the whole line/row for every event
            //store every line and split it by |

            //nextLine() is the one that moves the pointer
            String singleLine = sc.nextLine();
            singleLine = singleLine.substring(1);
            List<String> listOfSections = Arrays.asList(singleLine.split("[|]"));
            String timeStamp = listOfSections.get(0).trim();
            String url = listOfSections.get(1).trim();
            String userId = listOfSections.get(2).trim();


            Event event = null;
            try {
                event = new Event(timeStamp, url, userId);
            } catch (ParseException e) {
                e.printStackTrace();
                System.exit(1);
            }

            if((fromDate.compareTo(event.date) < 0 || fromDate.compareTo(event.date) == 0) && (toDate.compareTo(event.date) > 0 || toDate.compareTo(event.date) == 0)){
                events.add(event);
            }
        }

        HashMap<String, ArrayList<String>> urlVisits = new HashMap<>();
        for (Event event: events) {
            ArrayList<String> userIds = urlVisits.get(event.url);
            if (userIds == null) {
                userIds = new ArrayList<String>();
            }

            userIds.add(event.userId);
            urlVisits.put(event.url, userIds);
        }

        printRow("url", "page views", "visitors");
        for(String key: urlVisits.keySet()){
            ArrayList<String> userIds = urlVisits.get(key);
            int pageViews = userIds.size();
            Set<String> set = new HashSet<>(userIds);
            int uniqVisitors = set.size();
            printRow(key,Integer.toString(pageViews), Integer.toString(uniqVisitors));
        }

    }

    static void printRow(String a, String b, String c){
        System.out.printf("|%-16s|%-11s|%-8s|\n", a, b, c);
    }
}
