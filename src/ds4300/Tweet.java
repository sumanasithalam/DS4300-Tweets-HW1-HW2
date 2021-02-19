package ds4300;

public class Tweet {

  private long tweet_id;
  private long user_id;
  private String tweet_ts;
  private String tweet_text;

  public Tweet(long tweet_id, long user_id, String tweet_ts, String tweet_text) {
    this.tweet_id = tweet_id;
    this.user_id = user_id;
    this.tweet_ts = tweet_ts;
    this.tweet_text = tweet_text;
  }

  // empty constructor since we won't have all of the fields at once but still want to instantiate an object
  public Tweet() {

  }

  @Override
  public String toString() {
    return "Tweet{" +
        "tweet_id=" + tweet_id +
        ", user_id='" + user_id + '\'' +
        ", tweet_ts='" + tweet_ts + '\'' +
        ", tweet_text=" + tweet_text + '\'' +
        '}';
  }

  public long getTweetId() {
    return tweet_id;
  }

  public void setTweetId(long twtId) {
    this.tweet_id = twtId;
  }

  public long getUserId() {
    return user_id;
  }

  public void setUser_id(long uId) {
    this.user_id = uId;
  }

  public String getTweetTimestamp() {
    return tweet_ts;
  }

  public void setTweetTimestamp(String timestamp) {
    this.tweet_ts = timestamp;
  }

  public String getTweetText() {
    return tweet_text;
  }

  public void setTweet_text(String twtText) {
    this.tweet_text = twtText;
  }
}
