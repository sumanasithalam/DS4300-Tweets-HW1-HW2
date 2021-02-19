package ds4300.Redis_HW2;

import redis.clients.jedis.*;

import java.util.List;

public class RedisTester {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");

        jedis.flushAll();
        jedis.set("hello", "world");
        jedis.set("foo", "10");
        jedis.incr("foo");
        jedis.lpush("friends", "joe");
        jedis.lpush("friends", "mary");
        List<String> friends = jedis.lrange("friends", 0, -1);


        String value = jedis.get("hello");
        String foo = jedis.get("foo");
        System.out.println(value+"\t"+foo+"\t"+friends.toString());
    }

}
