package pl.kuezese.quests;

import java.time.Duration;
import java.time.Instant;

public class DurationTest {

    public static void main(String[] args) {
        Instant currentTime = Instant.now().plus(Duration.ofHours(8));
        Instant cooldownExpirationTime = Instant.now().plus(Duration.ofHours(4));

        // Check if the current time is after the cooldown expiration time
        boolean cd = currentTime.isBefore(cooldownExpirationTime);
        System.out.println(cd);
    }
}
