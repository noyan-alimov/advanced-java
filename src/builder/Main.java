package builder;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

public class Main {
    public static void main(String[] args) {
        UdacisearchClient client = new UdacisearchClient.Builder()
                .setName("CatFacts LLC")
                .setId(17)
                .setQuarterlyBudget(8000)
                .setNumEmployees(5)
                .setContractStart(Instant.now())
                .setContractLength(Duration.ofDays(180))
                .setTimeZone(ZoneId.of("America/Los_Angeles"))
                .setBillingAddress("555 Members Ln, Riverside, CA 92501")
                .build();

        System.out.println(client);
    }
}
