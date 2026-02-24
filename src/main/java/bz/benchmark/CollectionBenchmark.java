package bz.benchmark;

import bz.benchmark.model.LombokPerson;
import bz.benchmark.model.PlainPerson;
import bz.benchmark.model.RecordPerson;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class CollectionBenchmark {

    @Param({"100", "1000", "10000"})
    private int size;

    private Set<PlainPerson> plainSet;
    private Set<LombokPerson> lombokSet;
    private Set<RecordPerson> recordSet;

    private Map<PlainPerson, Integer> plainMap;
    private Map<LombokPerson, Integer> lombokMap;
    private Map<RecordPerson, Integer> recordMap;

    private PlainPerson plainHit;
    private PlainPerson plainMiss;
    private LombokPerson lombokHit;
    private LombokPerson lombokMiss;
    private RecordPerson recordHit;
    private RecordPerson recordMiss;

    @Setup
    public void setup() {
        plainSet = new HashSet<>();
        lombokSet = new HashSet<>();
        recordSet = new HashSet<>();
        plainMap = new HashMap<>();
        lombokMap = new HashMap<>();
        recordMap = new HashMap<>();

        for (int i = 0; i < size; i++) {
            String first = "First" + i;
            String last = "Last" + i;
            String email = "email%d@test.com".formatted(i);
            String city = "City" + (i % 50);

            plainSet.add(new PlainPerson(first, last, i, email, city));
            lombokSet.add(new LombokPerson(first, last, i, email, city));
            recordSet.add(new RecordPerson(first, last, i, email, city));

            plainMap.put(new PlainPerson(first, last, i, email, city), i);
            lombokMap.put(new LombokPerson(first, last, i, email, city), i);
            recordMap.put(new RecordPerson(first, last, i, email, city), i);
        }

        int mid = size / 2;
        plainHit = new PlainPerson("First" + mid, "Last" + mid, mid, "email%d@test.com".formatted(mid), "City" + (mid % 50));
        lombokHit = new LombokPerson("First" + mid, "Last" + mid, mid, "email%d@test.com".formatted(mid), "City" + (mid % 50));
        recordHit = new RecordPerson("First" + mid, "Last" + mid, mid, "email%d@test.com".formatted(mid), "City" + (mid % 50));

        plainMiss = new PlainPerson("Missing", "Person", -1, "nope@test.com", "Nowhere");
        lombokMiss = new LombokPerson("Missing", "Person", -1, "nope@test.com", "Nowhere");
        recordMiss = new RecordPerson("Missing", "Person", -1, "nope@test.com", "Nowhere");
    }

    // --- Set.contains (hit) ---

    @Benchmark
    public boolean plainSetContainsHit() {
        return plainSet.contains(plainHit);
    }

    @Benchmark
    public boolean lombokSetContainsHit() {
        return lombokSet.contains(lombokHit);
    }

    @Benchmark
    public boolean recordSetContainsHit() {
        return recordSet.contains(recordHit);
    }

    // --- Set.contains (miss) ---

    @Benchmark
    public boolean plainSetContainsMiss() {
        return plainSet.contains(plainMiss);
    }

    @Benchmark
    public boolean lombokSetContainsMiss() {
        return lombokSet.contains(lombokMiss);
    }

    @Benchmark
    public boolean recordSetContainsMiss() {
        return recordSet.contains(recordMiss);
    }

    // --- Map.get (hit) ---

    @Benchmark
    public Integer plainMapGetHit() {
        return plainMap.get(plainHit);
    }

    @Benchmark
    public Integer lombokMapGetHit() {
        return lombokMap.get(lombokHit);
    }

    @Benchmark
    public Integer recordMapGetHit() {
        return recordMap.get(recordHit);
    }

    // --- Map.get (miss) ---

    @Benchmark
    public Integer plainMapGetMiss() {
        return plainMap.get(plainMiss);
    }

    @Benchmark
    public Integer lombokMapGetMiss() {
        return lombokMap.get(lombokMiss);
    }

    @Benchmark
    public Integer recordMapGetMiss() {
        return recordMap.get(recordMiss);
    }

    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder()
                .include(CollectionBenchmark.class.getSimpleName())
                .build()
        ).run();
    }
}
