package ds4300.Redis_HW2;

import ds4300.ITweetDatabaseAPI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.Instant;

public class MainRedisStrategy2 {

  private static ITweetDatabaseAPI api;

  public static void main(String[] args) throws FileNotFoundException {
    api = new Strategy2RedisImplAPI(
        "/Users/Sumana/Documents/Northeastern/Second Year/Semester 2/Large-Scale Information Storage and Retrieval/TweetsProject/Text Files/MillionTweets.txt",
        "/Users/Sumana/Documents/Northeastern/Second Year/Semester 2/Large-Scale Information Storage and Retrieval/TweetsProject/Text Files/UserFollowerPairs.txt");

    PrintStream ps = new PrintStream(new File("timesStrategy2.txt"));
    PrintStream console = System.out;

    System.setOut(ps);

    //start "timer"
    System.out.println("overall start. before posting all user-follower pairs.");
    System.out.println(Instant.now());

    //insert all million tweets
    api.uploadAllUserFollowerPairs();

    //time the inserts of the million tweets
    System.out.println("\n\nafter posting all user-follower pairs. before inserting all tweets.");
    System.out.println(Instant.now());

    //insert all user-follower pairs
    api.postAllTweets();

    //time the inserts of user-follower pairs
    System.out.println("\n\nafter inserting all tweets. before printing 10000 tl's");
    System.out.println(Instant.now());

    System.setOut(console);
    // print a bunch of timelines (10000 here)
    api.printABunchOfTimelines(10000);

    //time after doing everything
    System.out.println("\n\nafter printing 10000 timelines");
    System.out.println(Instant.now());

//    api.closeConnection();
  }

}
