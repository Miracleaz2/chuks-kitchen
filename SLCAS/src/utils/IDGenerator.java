package utils;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private AtomicInteger counter = new AtomicInteger(1000);

    public String generateId(String prefix) {
        return prefix + "-" + counter.incrementAndGet();
    }
}
