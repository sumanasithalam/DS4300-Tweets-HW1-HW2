package ds4300;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

// generate a million random tweets (txt file alternating between a random user id and a random string)
public class TweetGenerator {

  public static int NUM_USERS = 10000;
  public static int TWEET_MIN_LENGTH = 1;
  public static int TWEET_MAX_LENGTH = 50;
  public static int TWEET_BOUND = TWEET_MAX_LENGTH - TWEET_MIN_LENGTH;
  public static String FILE_NAME = "MillionTweets.txt";

  public static void main(String[] args) throws IOException {
    StringBuilder millTweets = new StringBuilder();

    for (int i = 0; i < 1000000; i++) {
      millTweets.append(userIdGenerator());
      millTweets.append("\n");
      millTweets.append(textGenerator());

      if (i != 999999) {
        millTweets.append("\n");
      }
    }

    File f = new File(FILE_NAME);
    if (!f.exists()) {
      f.createNewFile();
    }

    PrintWriter pw = new PrintWriter(FILE_NAME);
    pw.print(millTweets);
    pw.close();
    System.out.println(millTweets);
  }

  private static int userIdGenerator() {
    Random randId = new Random();
    return randId.nextInt(NUM_USERS);
  }

  private static StringBuilder textGenerator() {
    String uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lowercaseChars = "abcdefghijklmnopqrstuvwxyz";
    String numbers = "0123456789";
    String symbols = "~!@#$%^&*()-_=+|]}[{;:/?.>,<";
    String validCharsForPassword = uppercaseChars + lowercaseChars + numbers + symbols;
    int validCharsLength = validCharsForPassword.length();

    Random randTxt = new Random();
    int twtLength = randTxt.nextInt(TWEET_BOUND) + TWEET_MIN_LENGTH;
    StringBuilder txtSoFar = new StringBuilder();

    for (int i = 0; i < twtLength; i++) {
      txtSoFar.append(validCharsForPassword.charAt(randTxt.nextInt(validCharsLength)));
    }
    return txtSoFar;
  }


}