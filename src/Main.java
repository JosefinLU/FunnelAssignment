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

        // Split up the string for every line (in the while loop)
        // every event is an object with properties timestamp, url and userId
        // then we add it in the list of events of the type Event!

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

            // creates object of Event
            Event event = null;
            try {
                event = new Event(timeStamp, url, userId);
            } catch (ParseException e) {
                e.printStackTrace();
                System.exit(1);
            }

            // Make sure the time interval is in the range:
            if((fromDate.compareTo(event.date) < 0 || fromDate.compareTo(event.date) == 0) && (toDate.compareTo(event.date) > 0 || toDate.compareTo(event.date) == 0)){
                events.add(event);
            }
        }

        // string - key = url, List of string = userids - we are creating new arralist for EVERY new url, contact has one list
        // FIRST here, where we declar the hashMap it is empty [ : ]
        HashMap<String, ArrayList<String>> urlVisits = new HashMap<>();
        for (Event event: events) {
            // here we are looking for the value for this key(urlVisits.get(event.url), the first time their is no value so we continue and createing a new list of userIds
            ArrayList<String> userIds = urlVisits.get(event.url);
            if (userIds == null) {
                userIds = new ArrayList<String>();
            }

            // Here we add userId in the List of userIds
            userIds.add(event.userId);
            //  here we put the value/userId to the related key (url) in the HASHMAP urlVisits!!!
            urlVisits.put(event.url, userIds); // contact.hmtl : [12345]


            /*
            The Flow:
            - first the hashmap is empty [:]
            - so we create a List of userIds that will be assosiated with keys(url)
            - then we add the userId in the list userIds [12345]
            - then we add in the hashmap urlVisit key and value [contact.html : 12345]

            1 BEFORE first itheration of events the hashmap is empty [:] and there is NO list
            for every done ithertion the list of userIds get adds: [12345] and the HashMap with key and value
            AFTER the first itheration [contact.html : [12345]] AND userIds = [12345]
            2 BEFORE: userIds = [12345] and [contact.html : [12345]]
             AFTER: List userIds [12345, 12346]
            HashMap urlVisits [contact.html : [12345, 12346]] (varje gång jag be om key contact.html så får jag en lista med alla userids)
            3 BEFORE: List userIds [12345, 12346] and HashMap urlVisits [contact.html : [12345, 12346]]
            AFTER: List userIds [12345, 12346, 12345]
            HashMap urlVisits [contact.html : [12345, 12346, 12345]]
            4 BEFORE: There is no List assosiated with that key (home), so we create a new one and urlVisits [ : []]
            AFTER: List userIds [12347]
            HashMap urlVisits [home.html : 12347]
            5 BEFORE: List userIds [12345, 12346, 12345] and urlVisits [contact.html : [12345, 12346, 12345]]
            AFTER: List userIds [12345, 12346, 12345, 12347]
            HashMap [contact.html : [12345, 12346, 12345, 12347]]
             */
        }

        // using keySet to get ut uniq visitors,
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

/*
Output
|timestamp              |url           |userid|
|2019-03-01 09:00:00UTC |/contact.html |12345 |
|2019-03-01 09:00:00UTC |/contact.html |12346 |
|2019-03-01 10:00:00UTC |/contact.html |12345 |
|2019-03-01 10:30:00UTC |/home.html    |12347 |
|2019-03-01 11:00:00UTC |/contact.html |12347 |
|2019-03-02 11:00:00UTC |/contact.html |12348 |
|2019-03-02 12:00:00UTC |/home.html    |12348 |
|2019-03-03 13:00:00UTC |/home.html    |12349 |
 */
