package ds4300.Redis_HW2;

import ds4300.ITweetDatabaseAPI;
import ds4300.Tweet;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Strategy1RedisImplAPI extends AbstractRedisImplAPI implements ITweetDatabaseAPI {

  /**
   * constructor for the API that takes in the filepaths of the million tweets and followers list
   *
   * @param millTweetsFilepath    filepath for the million tweets
   * @param followersListFilepath filepath for the list of users that users follow
   * @throws FileNotFoundException if either of the files aren't found
   */
  public Strategy1RedisImplAPI(String millTweetsFilepath, String followersListFilepath)
      throws FileNotFoundException {
    super(millTweetsFilepath, followersListFilepath);
    jedis.set("tweetId", "0");
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
}
