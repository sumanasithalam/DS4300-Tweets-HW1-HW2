package ds4300.Redis_HW2;

import ds4300.ITweetDatabaseAPI;
import ds4300.Tweet;
import ds4300.UserFollower;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import redis.clients.jedis.Jedis;

abstract public class AbstractRedisImplAPI implements ITweetDatabaseAPI {

  Scanner millTweetsScan;
  Scanner userfollowersScan;
  Jedis jedis;

  /**
   * constructor for the API that takes in the filepaths of the million tweets and followers list
   *
   * @param millTweetsFilepath    filepath for the million tweets
   * @param followersListFilepath filepath for the list of users that users follow
   * @throws FileNotFoundException if either of the files aren't found
   */
  public AbstractRedisImplAPI(String millTweetsFilepath, String followersListFilepath)
      throws FileNotFoundException {
    this.millTweetsScan = new Scanner(new File(millTweetsFilepath));
    this.userfollowersScan = new Scanner(new File(followersListFilepath));
    authenticate("localhost", "root", "root");
  }

  /**
   * Insert/post a single tweet
   *
   * @param t the tweet
   */
  @Override
  abstract public void postTweet(Tweet t);

  /**
   * add user ids to list of who they are following and who the user id is followed by
   *
   * @param uf the user and who they are following
   */
  @Override
  public void followUser(UserFollower uf) {
    jedis.lpush("USER_FOLLOWS:" + uf.getUser_id(), String.valueOf(uf.getFollows_id()));
  }

  public List<Tweet> compileTL(String timelineKey) {
    Set<String> topTenStringIDs = jedis.zrange(timelineKey, 0, 9);
    List<Tweet> tweetIds = new ArrayList<>();
    for (String str : topTenStringIDs) {
      Tweet t = new Tweet();
      String keyName = "TWEET:" + str;

      t.setTweet_text(jedis.hget(keyName, "text"));
      t.setUser_id(Long.parseLong(jedis.hget(keyName, "userID")));
      t.setTweetId(Long.parseLong(str));

      String millisecondsTS = jedis.hget(keyName, "timestamp");
      Date dateTS = new Date(Long.valueOf(millisecondsTS));
      SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
      String stringFormattedTS = sdf.format(dateTS);
      t.setTweetTimestamp(stringFormattedTS);

      tweetIds.add(t);
    }

    return tweetIds;
  }

  /**
   * Get timelines for a number of users
   *
   * @param numUsersToGet the number of random users to return a timeline for
   * @return a hashmap containing the userID as the key and their home timelines as the
   * corresponding value
   */
  @Override
  public HashMap<Long, List<Tweet>> getABunchOfTimelines(int numUsersToGet) {
    HashMap<Long, List<Tweet>> hm = new HashMap<>();
    for (int i = 0; i < numUsersToGet; i++) {
      Random rand = new Random();
      long randomID = rand.nextInt(10000);
      while (hm.containsKey(randomID)) {
        randomID = rand.nextInt(10000);
      }
      List<Tweet> singleTL = getSingleTimeline(randomID);
      hm.put(randomID, singleTL);
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
   * insert all tweets
   */
  @Override
  public void postAllTweets() {
    int count = 0;
    Tweet t = new Tweet();
    while (millTweetsScan.hasNextLine()) {
      if (count % 2 == 0) {
        String shouldBeLong = millTweetsScan.next();
        t.setUser_id(Long.valueOf(shouldBeLong));
        count++;
      } else if (count % 2 != 0) {
        String shouldBeText = millTweetsScan.next();
        t.setTweet_text(shouldBeText);
        count++;
        postTweet(t);
        t = new Tweet();
      }
    }
  }

  /**
   * insert all users and who they follow
   */
  @Override
  public void uploadAllUserFollowerPairs() {
    int count = 0;
    UserFollower uf = new UserFollower();
    while (userfollowersScan.hasNextLine()) {
      if (count % 2 == 0) {
        uf.setUser_id(Long.valueOf(userfollowersScan.next()));
        count++;
      } else if (count % 2 != 0) {
        uf.setFollows_id(Long.valueOf(userfollowersScan.next()));
        count++;
        followUser(uf);
        uf = new UserFollower();
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
    jedis = new Jedis("localhost");
    jedis.set("tweetId", "0");
  }

  /**
   * Flush all
   */
  @Override
  public void closeOrFlush() {
    jedis.flushAll();
  }
}
