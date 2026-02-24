package bz.benchmark;

import bz.benchmark.model.CommonsPerson;
import bz.benchmark.model.LombokPerson;
import bz.benchmark.model.PlainPerson;
import bz.benchmark.model.RecordPerson;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
@State(Scope.Thread)
public class HashCodeEqualsBenchmark {

    private PlainPerson plainPerson1;
    private PlainPerson plainPerson2;
    private PlainPerson plainPersonDifferent;

    private LombokPerson lombokPerson1;
    private LombokPerson lombokPerson2;
    private LombokPerson lombokPersonDifferent;

    private RecordPerson recordPerson1;
    private RecordPerson recordPerson2;
    private RecordPerson recordPersonDifferent;

    private CommonsPerson commonsPerson1;
    private CommonsPerson commonsPerson2;
    private CommonsPerson commonsPersonDifferent;

    @Setup
    public void setup() {
        plainPerson1 = new PlainPerson("John", "Doe", 30, "john@example.com", "Paris");
        plainPerson2 = new PlainPerson("John", "Doe", 30, "john@example.com", "Paris");
        plainPersonDifferent = new PlainPerson("Jane", "Smith", 25, "jane@example.com", "Lyon");

        lombokPerson1 = new LombokPerson("John", "Doe", 30, "john@example.com", "Paris");
        lombokPerson2 = new LombokPerson("John", "Doe", 30, "john@example.com", "Paris");
        lombokPersonDifferent = new LombokPerson("Jane", "Smith", 25, "jane@example.com", "Lyon");

        recordPerson1 = new RecordPerson("John", "Doe", 30, "john@example.com", "Paris");
        recordPerson2 = new RecordPerson("John", "Doe", 30, "john@example.com", "Paris");
        recordPersonDifferent = new RecordPerson("Jane", "Smith", 25, "jane@example.com", "Lyon");

        commonsPerson1 = new CommonsPerson("John", "Doe", 30, "john@example.com", "Paris");
        commonsPerson2 = new CommonsPerson("John", "Doe", 30, "john@example.com", "Paris");
        commonsPersonDifferent = new CommonsPerson("Jane", "Smith", 25, "jane@example.com", "Lyon");
    }

    // --- hashCode ---

    @Benchmark
    public int plainHashCode() {
        return plainPerson1.hashCode();
    }

    @Benchmark
    public int lombokHashCode() {
        return lombokPerson1.hashCode();
    }

    @Benchmark
    public int recordHashCode() {
        return recordPerson1.hashCode();
    }

    @Benchmark
    public int commonsHashCode() {
        return commonsPerson1.hashCode();
    }

    // --- equals (same values) ---

    @Benchmark
    public boolean plainEqualsTrue() {
        return plainPerson1.equals(plainPerson2);
    }

    @Benchmark
    public boolean lombokEqualsTrue() {
        return lombokPerson1.equals(lombokPerson2);
    }

    @Benchmark
    public boolean recordEqualsTrue() {
        return recordPerson1.equals(recordPerson2);
    }

    @Benchmark
    public boolean commonsEqualsTrue() {
        return commonsPerson1.equals(commonsPerson2);
    }

    // --- equals (different values) ---

    @Benchmark
    public boolean plainEqualsFalse() {
        return plainPerson1.equals(plainPersonDifferent);
    }

    @Benchmark
    public boolean lombokEqualsFalse() {
        return lombokPerson1.equals(lombokPersonDifferent);
    }

    @Benchmark
    public boolean recordEqualsFalse() {
        return recordPerson1.equals(recordPersonDifferent);
    }

    @Benchmark
    public boolean commonsEqualsFalse() {
        return commonsPerson1.equals(commonsPersonDifferent);
    }

    public static void main(String[] args) throws Exception {
        new Runner(new OptionsBuilder()
                .include(HashCodeEqualsBenchmark.class.getSimpleName())
                .build()
        ).run();
    }
}
