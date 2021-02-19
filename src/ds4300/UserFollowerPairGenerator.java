package ds4300;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

// generates between 1 and 40 users that each user follows, and returns as a text file with a user and who they follow alternating
public class UserFollowerPairGenerator {

  private static final Random rand = new Random();
  public static int NUM_USERS = 10000;
  public static int MAX_NUM_FOLLOWERS = 40;
  public static String FILE_NAME = "UserFollowerPairs.txt";

  public static void main(String[] args) throws IOException {
    StringBuilder userFoll = new StringBuilder();

    // prepend before loop to avoid the new line character at the end
    userFoll.append(0);
    userFoll.append("\n");
    userFoll.append(userIdGenerator(0));

    for (int u = 0; u < 10000; u++) {
      for (int f = 0; f < rand.nextInt(MAX_NUM_FOLLOWERS) + 1; f++) {
        userFoll.append("\n");
        userFoll.append(u);
        userFoll.append("\n");
        userFoll.append(userIdGenerator(u));
      }
    }

    File f = new File(FILE_NAME);
    if (!f.exists()) {
      f.createNewFile();
    }

    PrintWriter pw = new PrintWriter(FILE_NAME);
    pw.print(userFoll);
    pw.close();
    System.out.println(userFoll);
  }

  private static int userIdGenerator(int u) {
    int f = rand.nextInt(NUM_USERS);
    if (f != u) {
      return f;
    } else {
      return userIdGenerator(u);
    }
  }
}