package ds4300.MySQL_HW1;

//public class MainMySQL {
//
//}


import ds4300.ITweetDatabaseAPI;
import java.io.FileNotFoundException;
import java.time.Instant;

// inserts the million tweets and user-follower pairs, as well as returning 5000 users' home timelines.
public class MainMySQL {

  private static ITweetDatabaseAPI api;

  public static void main(String[] args) throws FileNotFoundException {
    api = new MySQLImplAPI(
        "/Users/Sumana/Documents/Northeastern/Second Year/Semester 2/Large-Scale Information Storage and Retrieval/TweetsProject/Text Files/MillionTweets.txt",
        "/Users/Sumana/Documents/Northeastern/Second Year/Semester 2/Large-Scale Information Storage and Retrieval/TweetsProject/Text Files/UserFollowerPairs.txt");

    // Authenticate your access to the server.
    String url = "jdbc:mysql://localhost:3306/TwitterDB?serverTimezone=EST5EDT";
    String user = "twtUser";
    String password = "twtUser";

    api.authenticate(url, user, password);

    //start "timer"
    System.out.println("overall start. before posting all tweets.");
    System.out.println(Instant.now());

    //insert all million tweets
    api.postAllTweets();

    //time the inserts of the million tweets
    System.out.println("\n\nafter posting all tweets. before inserting user-follower pairs.");
    System.out.println(Instant.now());

    //insert all user-follower pairs
    api.uploadAllUserFollowerPairs();

    //time the inserts of user-follower pairs
    System.out.println("\n\nafter inserting all user-follower pairs. before printing 5000 tl's");
    System.out.println(Instant.now());

    // print a bunch of timelines (5000 here)
    api.printABunchOfTimelines(5000);

    //time after doing everything
    System.out.println("\n\nafter printing 5000 timelines");
    System.out.println(Instant.now());

    api.closeConnection();
  }
}

