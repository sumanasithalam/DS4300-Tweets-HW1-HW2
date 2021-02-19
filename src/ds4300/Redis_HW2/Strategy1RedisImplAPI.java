package ds4300.Redis_HW2;

import ds4300.ITweetDatabaseAPI;
import ds4300.Tweet;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Strategy1RedisImplAPI extends AbstractRedisImplAPI implements ITweetDatabaseAPI {

//  Scanner millTweetsScan;
//  Scanner userfollowersScan;
//  Jedis jedis;

  /**
   * constructor for the API that takes in the filepaths of the million tweets and followers list
   *
   * @param millTweetsFilepath    filepath for the million tweets
   * @param followersListFilepath filepath for the list of users that users follow
   * @throws FileNotFoundException if either of the files aren't found
   */
  public Strategy1RedisImplAPI(String millTweetsFilepath, String followersListFilepath)
      throws FileNotFoundException {
//    this.millTweetsScan = new Scanner(new File(millTweetsFilepath));
//    this.userfollowersScan = new Scanner(new File(followersListFilepath));
//    authenticate("localhost", "root", "root");
    super(millTweetsFilepath, followersListFilepath);
  }

  /**
   * Insert/post a single tweet
   *
   * @param t the tweet
   */
  @Override
  public void postTweet(Tweet t) {
    // add tweet key
    String twtId = jedis.get("tweetId");
    String keyTwt = "TWEET:" + twtId;
    jedis.incr("tweetId");

    long tsLong = System.currentTimeMillis();
    String timestamp = String.valueOf(tsLong);
    Map<String, String> hm = new HashMap<>();
    hm.put("timestamp", timestamp);
    hm.put("text", t.getTweetText());
    hm.put("userID", "" + t.getUserId());
    jedis.hmset(keyTwt, hm);

    // add user/op key in order of when they are posted
    String keyUsr = "USER_TWEETS:" + t.getUserId();
    jedis.zadd(keyUsr, tsLong, twtId);
  }

//  /**
//   * add user ids to list of who they are following and who the user id is followed by
//   *
//   * @param uf the user and who they are following
//   */
//  @Override
//  public void followUser(UserFollower uf) {
//    jedis.lpush("USER_FOLLOWS:" + uf.getUser_id(), String.valueOf(uf.getFollows_id()));
////    jedis.lpush("USER_FOLLOWED_BY:" + uf.getFollows_id(), String.valueOf(uf.getUser_id()));
//  }

  /**
   * Get the 10 most recent tweets that the provided userID follows
   *
   * @param userID the id of the user
   * @return the home timeline containing 10 most recent tweets
   */
  @Override
  public List<Tweet> getSingleTimeline(long userID) {
    String usrKey = "USER_FOLLOWS:" + userID;
    List<String> whoUserFollows = jedis.lrange(usrKey, 0, -1);

    String tlKey = "TIMELINE:" + userID;

    int numUsers = whoUserFollows.size();
    StringBuilder sb = new StringBuilder();
    boolean isFirst = true;
    for (String strFollowingId : whoUserFollows) {
      String user = "USER_TWEETS:" + strFollowingId;
      if (isFirst) {
        sb.append(user);
        isFirst = false;
      } else {
        sb.append(" " + user);
      }
    }

    jedis.zunionstore(tlKey, sb.toString().split(" "));

    return compileTL(tlKey);
  }

//  /**
//   * Get timelines for a number of users
//   *
//   * @param numUsersToGet the number of random users to return a timeline for
//   * @return a hashmap containing the userID as the key and their home timelines as the
//   * corresponding value
//   */
//  @Override
//  public HashMap<Long, List<Tweet>> getABunchOfTimelines(int numUsersToGet) {
//    HashMap<Long, List<Tweet>> hm = new HashMap<>();
//    Random rand = new Random();
//    for (int i = 0; i < numUsersToGet; i++) {
//      long randomID = rand.nextInt(10000);
//      while (hm.containsKey(randomID)) {
//        randomID = rand.nextInt(10000);
//      }
//      List<Tweet> singleTL = getSingleTimeline(randomID);
//      hm.put(randomID, singleTL);
//    }
//    return hm;
//  }

//  /**
//   * Print a number of user timelines
//   *
//   * @param numUsersToGet the number of random users to return a timeline for
//   */
//  @Override
//  public void printABunchOfTimelines(int numUsersToGet) {
//    HashMap<Long, List<Tweet>> randomTLs = getABunchOfTimelines(numUsersToGet);
//
//    for (long uID : randomTLs.keySet()) {
//      System.out.println("\n\nUser #" + uID + "'s Home Timeline:");
//      for (Tweet t : randomTLs.get(uID)) {
//        System.out.println(t.toString());
//      }
//    }
//  }

//  /**
//   * insert all tweets
//   */
//  @Override
//  public void postAllTweets() {
//    int count = 0;
//    Tweet t  = new Tweet();
//    while (millTweetsScan.hasNextLine()) {
//      if (count % 2 == 0) {
//        String shouldBeLong = millTweetsScan.next();
//        t.setUser_id(Long.valueOf(shouldBeLong));
//        count++;
//      }
//      else if (count % 2 != 0) {
//        String shouldBeText = millTweetsScan.next();
//        t.setTweet_text(shouldBeText);
//        count++;
//        postTweet(t);
//        t = new Tweet();
//      }
//    }
//  }

//  /**
//   * insert all users and who they follow
//   */
//  @Override
//  public void uploadAllUserFollowerPairs() {
//    int count = 0;
//    UserFollower uf  = new UserFollower();
//    while (userfollowersScan.hasNextLine()) {
//      if (count % 2 == 0) {
//        uf.setUser_id(Long.valueOf(userfollowersScan.next()));
//        count++;
//      }
//      else if (count % 2 != 0) {
//        uf.setFollows_id(Long.valueOf(userfollowersScan.next()));
//        count++;
//        followUser(uf);
//        uf = new UserFollower();
//      }
//    }
//  }

//  /**
//   * Set connection settings
//   *
//   * @param url
//   * @param user
//   * @param password
//   */
//  @Override
//  public void authenticate(String url, String user, String password) {
//    jedis = new Jedis("localhost");
//    jedis.set("tweetId", "0");
//  }
//
//  /**
//   * Flush all
//   */
//  @Override
//  public void closeConnection() {
//    jedis.flushAll();
//  }
}
