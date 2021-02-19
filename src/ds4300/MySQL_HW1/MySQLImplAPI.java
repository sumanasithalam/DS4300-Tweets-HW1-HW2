package ds4300.MySQL_HW1;

import ds4300.ITweetDatabaseAPI;
import ds4300.Tweet;
import ds4300.UserFollower;
import ds4300.database.DBUtilsMySQL;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MySQLImplAPI implements ITweetDatabaseAPI {

  // For demonstration purposes. Better would be a constructor that takes a file path
  // and loads parameters dynamically.
  DBUtilsMySQL dbu;
  Scanner millTweetsScan;
  Scanner userfollowersScan;

  /**
   * constructor for the API that takes in the filepaths of the million tweets and followers list
   *
   * @param millTweetsFilepath    filepath for the million tweets
   * @param followersListFilepath filepath for the list of users that users follow
   * @throws FileNotFoundException if either of the files aren't found
   */
  public MySQLImplAPI(String millTweetsFilepath, String followersListFilepath)
      throws FileNotFoundException {
    this.millTweetsScan = new Scanner(new File(millTweetsFilepath));
    this.userfollowersScan = new Scanner(new File(followersListFilepath));
  }

  /**
   * insert a tweet into the database
   *
   * @param t the tweet
   */
  @Override
  public void postTweet(Tweet t) {
    SimpleDateFormat sdf = new SimpleDateFormat("'YYYY-MM-DD HH:mm:ss'");

    String sql = "INSERT INTO Tweets (user_id,tweet_ts,tweet_text) VALUES "
        + "(" + t.getUserId() + ", " + sdf.format(new Date()) + ", " + "'" + t.getTweetText()
        + "')";
    dbu.insertOneRecord(sql);
  }

  /**
   * insert one user-following pair into the database
   *
   * @param uf the user and who they are following
   */
  @Override
  public void followUser(UserFollower uf) {

    String sql = "INSERT INTO Followers (user_id, follows_id) VALUES "
        + "(" + uf.getUser_id() + ", " + uf.getFollows_id() + ")";

    dbu.insertOneRecord(sql);
  }

  /**
   * insert all the tweets in the provided tweets filepath into the database in batches of 1000
   */
  @Override
  public void postAllTweets() {
    int indivIdx = 0;
    int pairIdx = 0;
    int count = 0;
    Tweet[] twtObjArr = new Tweet[1000];
    Tweet t = new Tweet();
    while (millTweetsScan.hasNextLine()) {
      if (count % 2000 == 0 && count != 0) {
        postAllTweetsIncrementally(twtObjArr);
        twtObjArr = new Tweet[1000];
        indivIdx = 0;
        pairIdx = 0;
        count = 0;
      } else if (indivIdx % 2 == 0) {
        String shouldBeLong = millTweetsScan.next();
        t.setUser_id(Long.valueOf(shouldBeLong));
        indivIdx++;
        count++;
      } else if (!(indivIdx % 2 == 0)) {
        String shouldBeText = millTweetsScan.next();
        t.setTweet_text(shouldBeText);
        twtObjArr[pairIdx] = t;
        t = new Tweet();
        indivIdx++;
        pairIdx++;
        count++;
      }
    }
    postAllTweetsIncrementally(twtObjArr);
  }

  /**
   * insert each batch of tweets into the database
   *
   * @param tweetArr the batch of tweets to insert
   */
  private void postAllTweetsIncrementally(Tweet[] tweetArr) {
    String sql = "INSERT INTO Tweets (user_id,tweet_ts,tweet_text) VALUES (?,?,?)";

    try {
      Connection con = dbu.getConnection();
      PreparedStatement pstmt = con.prepareStatement(sql);

      for (Tweet t : tweetArr) {
        pstmt.setLong(1, t.getUserId());
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String ts2 = sdf.format(new Date());
        pstmt.setObject(2, ts2);
        pstmt.setString(3, t.getTweetText());
        pstmt.execute();
      }
      pstmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * create a giant arraylist of user-follower objects and inserts every one into the database
   */
  @Override
  public void uploadAllUserFollowerPairs() {
    int indivIdx = 0;
    List<UserFollower> ufList = new ArrayList<>();
    UserFollower ufPair = new UserFollower();
    while (userfollowersScan.hasNextLine()) {
      if (indivIdx % 2 == 0) {
        String shouldBeLong = userfollowersScan.next();
        ufPair.setUser_id(Long.valueOf(shouldBeLong));
        indivIdx++;
      } else if (!(indivIdx % 2 == 0)) {
        String shouldBeLong = userfollowersScan.next();
        ufPair.setFollows_id(Long.valueOf(shouldBeLong));
        ufList.add(ufPair);
        ufPair = new UserFollower();
        indivIdx++;
      }
    }
    uploadUserFollowerPairs(ufList);
  }

  /**
   * insert all the user-followers from the provided list into the database
   *
   * @param ufList list of user-follower objects
   */
  private void uploadUserFollowerPairs(List<UserFollower> ufList) {

    // chose to ignore duplicate random pairs here instead of in the generation
    String sql = "INSERT IGNORE INTO Followers (user_id,follows_id) VALUES (?,?)";

    try {
      Connection con = dbu.getConnection();
      PreparedStatement pstmt = con.prepareStatement(sql);

      for (UserFollower pair : ufList) {
        pstmt.setLong(1, pair.getUser_id());
        pstmt.setLong(2, pair.getFollows_id());
        pstmt.execute();
      }
      pstmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * generate the 10 most recent tweets of users that the provided user follows
   *
   * @param userID the id of the user
   * @return a list of tweets in the user's timeline
   */
  @Override
  public List<Tweet> getSingleTimeline(long userID) {
    List<Tweet> tl = new ArrayList<>();

    String sql =
        "SELECT t2.user_id AS authorID, t2.tweet_id AS tweetID, t2.tweet_ts AS dateAndTime, t2.tweet_text AS tweetText "
            + "FROM Tweets t2 RIGHT OUTER JOIN (SELECT DISTINCT f.follows_id as posterID "
            + "FROM Tweets t INNER JOIN Followers f ON t.user_id = f.user_id "
            + "WHERE t.user_id = " + userID + ") q ON t2.user_id = q.posterID "
            + "ORDER BY dateAndTime DESC "
            + "LIMIT 10";

    try {
      // get connection and initialize statement
      Connection con = dbu.getConnection();
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next() != false) {
        tl.add(new Tweet(rs.getLong("tweetID"), rs.getLong("authorID"), rs.getString("dateAndTime"),
            rs.getString("tweetText")));
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }

    return tl;
  }

  /**
   * generate timelines for a provided number of random users
   *
   * @param numUsersToGet the number of random users to return a timeline for
   * @return a hashmap containing each of the user id's and their corresponding timelines
   */
  @Override
  public HashMap<Long, List<Tweet>> getABunchOfTimelines(int numUsersToGet) {
    HashMap<Long, List<Tweet>> hm = new HashMap<>();
    Random rand = new Random();
    for (int i = 0; i < numUsersToGet; i++) {
      long uID = rand.nextInt(10000);
      while (hm.containsKey(uID)) {
        uID = rand.nextInt(10000);
      }
      List<Tweet> singularTL = getSingleTimeline(uID);
      hm.put(uID, singularTL);
    }
    return hm;
  }

  /**
   * Print a number of user timelines
   *
   * @param numUsersToGet the number of random users to return a timeline for
   */
  @Override
  public void printABunchOfTimelines(int numUsersToGet) {
    HashMap<Long, List<Tweet>> randomTLs = getABunchOfTimelines(numUsersToGet);

    for (long uID : randomTLs.keySet()) {
      System.out.println("\n\nUser #" + uID + "'s Home Timeline:");
      for (Tweet t : randomTLs.get(uID)) {
        System.out.println(t.toString());
      }
    }
  }

  /**
   * Set connection settings
   *
   * @param url
   * @param user
   * @param password
   */
  @Override
  public void authenticate(String url, String user, String password) {
    dbu = new DBUtilsMySQL(url, user, password);
  }

  /**
   * Close the connection when application finishes
   */
  @Override
  public void closeOrFlush() {
    dbu.closeConnection();
  }
}
