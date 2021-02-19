package ds4300;

import java.util.HashMap;
import java.util.List;

public interface ITweetDatabaseAPI {

  /**
   * Insert/post a single tweet
   *
   * @param t the tweet
   */
  void postTweet(Tweet t);

  /**
   * Follow/add to followers table a single user-follower pair
   *
   * @param uf the user and who they are following
   */
  void followUser(UserFollower uf);

  /**
   * Get the 10 most recent tweets that the provided userID follows
   *
   * @param userID the id of the user
   * @return the home timeline containing 10 most recent tweets
   */
  List<Tweet> getSingleTimeline(long userID);

  /**
   * Get timelines for a number of users
   *
   * @param numUsersToGet the number of random users to return a timeline for
   * @return a hashmap containing the userID as the key and their home timelines as the
   * corresponding value
   */
  HashMap<Long, List<Tweet>> getABunchOfTimelines(int numUsersToGet);

  /**
   * Print the timelines for a number of users
   *
   * @param numUsersToGet the number of random users to return a timeline for
   */
  void printABunchOfTimelines(int numUsersToGet);

  /**
   * insert all tweets
   */
  void postAllTweets();

  /**
   * insert all users and who they follow
   */
  void uploadAllUserFollowerPairs();

  /**
   * Set connection settings
   *
   * @param url
   * @param user
   * @param password
   */
  void authenticate(String url, String user, String password);

  /**
   * Close the connection when application finishes
   */
  void closeOrFlush();
}
