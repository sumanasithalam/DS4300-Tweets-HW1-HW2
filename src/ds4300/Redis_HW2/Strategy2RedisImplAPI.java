package ds4300.Redis_HW2;

import ds4300.ITweetDatabaseAPI;
import ds4300.Tweet;
import ds4300.UserFollower;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Strategy2RedisImplAPI extends AbstractRedisImplAPI implements ITweetDatabaseAPI {

  /**
   * constructor for the API that takes in the filepaths of the million tweets and followers list
   *
   * @param millTweetsFilepath    filepath for the million tweets
   * @param followersListFilepath filepath for the list of users that users follow
   * @throws FileNotFoundException if either of the files aren't found
   */
  public Strategy2RedisImplAPI(String millTweetsFilepath, String followersListFilepath)
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

    String userId = "" + t.getUserId();

    long tsLong = System.currentTimeMillis();
    String timestamp = String.valueOf(tsLong);
    Map<String, String> hm = new HashMap<>();
    hm.put("timestamp", timestamp);
    hm.put("text", t.getTweetText());
    hm.put("userID", "" + userId);
    jedis.hmset(keyTwt, hm);

    // add user/op key in order of when they are posted
    String keyUsr = "USER_TWEETS:" + userId;
    jedis.zadd(keyUsr, tsLong, twtId);

    // post tweet to timeline of user_followed_by
    List<String> allFollowers = jedis.lrange("USER_FOLLOWED_BY:" + userId, 0, -1);

    for (String strFollower : allFollowers) {
      jedis.zadd("TIMELINE:" + strFollower, tsLong, twtId);
    }
  }


  /**
   * add user ids to list of who they are following and who the user id is followed by
   *
   * @param uf the user and who they are following
   */
  @Override
  public void followUser(UserFollower uf) {
    super.followUser(uf);
    jedis.lpush("USER_FOLLOWED_BY:" + uf.getFollows_id(), String.valueOf(uf.getUser_id()));
  }

  /**
   * Get the 10 most recent tweets that the provided userID follows
   *
   * @param userID the id of the user
   * @return the home timeline containing 10 most recent tweets
   */
  @Override
  public List<Tweet> getSingleTimeline(long userID) {

    return compileTL("TIMELINE:" + userID);
  }
}
