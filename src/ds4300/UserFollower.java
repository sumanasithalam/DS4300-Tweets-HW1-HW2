package ds4300;

public class UserFollower {

  private long user_id;
  private long follows_id;

  public UserFollower(long user_id, long follower_id) {
    this.user_id = user_id;
    this.follows_id = follower_id;
  }

  // empty constructor since we won't have both fields at once but still want to instantiate an object
  public UserFollower() {

  }

  @Override
  public String toString() {
    return "Followers{" +
        "user_id=" + user_id +
        ", follows_id='" + follows_id + '\'' +
        '}';
  }

  public long getUser_id() {
    return user_id;
  }

  public void setUser_id(long user_id) {
    this.user_id = user_id;
  }

  public long getFollows_id() {
    return follows_id;
  }

  public void setFollows_id(long follows_id) {
    this.follows_id = follows_id;
  }
}
