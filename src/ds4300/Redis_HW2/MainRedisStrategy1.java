package ds4300.Redis_HW2;

import ds4300.ITweetDatabaseAPI;
import java.io.FileNotFoundException;
import java.time.Instant;

public class MainRedisStrategy1 {

  private static ITweetDatabaseAPI api;

  public static void main(String[] args) throws FileNotFoundException {
    api = new Strategy1RedisImplAPI(
        "/Users/Sumana/Documents/Northeastern/Second Year/Semester 2/Large-Scale Information Storage and Retrieval/TweetsProject/Text Files/MillionTweets.txt",
        "/Users/Sumana/Documents/Northeastern/Second Year/Semester 2/Large-Scale Information Storage and Retrieval/TweetsProject/Text Files/UserFollowerPairs.txt");

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

//    api.closeConnection();
  }

}
