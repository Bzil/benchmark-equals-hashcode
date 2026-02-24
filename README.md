# hashCode & equals Benchmark: Lombok vs Plain POJO vs Record

JMH benchmark comparing `hashCode()` and `equals()` performance across three Java implementations of the same data class:

- **Plain POJO** — hand-written `equals()`/`hashCode()` using `Objects.equals` and `Objects.hash`
- **Lombok POJO** — `@EqualsAndHashCode` generated code
- **Java Record** — compiler-generated `equals()`/`hashCode()` via `invokedynamic`

## Prerequisites

- Java 25+
- Maven 3.8+

## Build & Run

```bash
mvn clean package
java -jar target/benchmarks.jar
```

### Useful JMH options

```bash
# Single fork, 3 warmup iterations (faster dev runs)
java -jar target/benchmarks.jar -f 1 -wi 3

# Run only hashCode benchmarks
java -jar target/benchmarks.jar ".*hashCode.*"

# Output results as JSON
java -jar target/benchmarks.jar -rf json -rff results.json
```

## Project Structure

```
src/main/java/com/benchmark/
├── model/
│   ├── PlainPerson.java        # Manual equals/hashCode with Objects.hash
│   ├── LombokPerson.java       # @EqualsAndHashCode @Getter @AllArgsConstructor
│   └── RecordPerson.java       # Java record
├── HashCodeEqualsBenchmark.java # 9 raw hashCode/equals benchmarks
└── CollectionBenchmark.java    # 12 Set/Map lookup benchmarks (x3 sizes)
```

## Results

Measured on JDK 25 (OpenJDK Zulu 25+36-LTS), average time in nanoseconds per operation.

### hashCode()

| Implementation | ns/op | Notes |
|---|---|---|
| **Lombok** | **1.43** | Inline field-by-field computation |
| **Record** | **1.43** | `invokedynamic` via `ObjectMethods.bootstrap` |
| Plain | 12.86 | `Objects.hash()` allocates a varargs `Object[]` |

### equals() — equal objects

| Implementation | ns/op |
|---|---|
| **Record** | **1.43** |
| **Plain** | **1.44** |
| Lombok | 1.90 |

### equals() — different objects (short-circuit on first field)

| Implementation | ns/op |
|---|---|
| **Lombok** | **0.73** |
| **Plain** | **0.74** |
| Record | 1.33 |

### Collection Lookups

Real-world performance measured with `HashSet.contains()` and `HashMap.get()`, parameterized across 100, 1000 and 10,000 entries.

#### Set.contains() — hit

| Implementation | 100 | 1,000 | 10,000 |
|---|---|---|---|
| **Lombok** | **9.94** | **7.77** | **7.44** |
| **Record** | **8.59** | **8.66** | **8.43** |
| Plain | 20.05 | 20.80 | 20.67 |

#### Set.contains() — miss

| Implementation | 100 | 1,000 | 10,000 |
|---|---|---|---|
| **Record** | **2.13** | **2.22** | **2.05** |
| **Lombok** | **2.12** | **2.92** | **2.12** |
| Plain | 14.48 | 14.25 | 14.28 |

#### Map.get() — hit

| Implementation | 100 | 1,000 | 10,000 |
|---|---|---|---|
| **Lombok** | **7.81** | **8.15** | **8.04** |
| **Record** | **8.53** | **8.67** | **8.52** |
| Plain | 18.83 | 20.54 | 20.91 |

#### Map.get() — miss

| Implementation | 100 | 1,000 | 10,000 |
|---|---|---|---|
| **Record** | **1.98** | **2.02** | **1.99** |
| **Lombok** | **2.01** | **2.95** | **2.05** |
| Plain | 13.59 | 13.88 | 14.25 |

### Key Takeaways

- **`Objects.hash()` is a performance trap** — the varargs array allocation makes it ~9x slower than alternatives, and this directly impacts every Set/Map operation.
- **Plain POJO is 2-7x slower in collection lookups** — the `hashCode()` penalty from `Objects.hash()` dominates real-world performance in hash-based collections.
- **Lombok and Record are nearly identical for `hashCode()`**, both generating efficient inline computations.
- **Records got a significant `equals()` optimization in JDK 25** — `equalsTrue` dropped from 1.74 ns (JDK 21) to 1.43 ns.
- **Records are the best overall choice in modern Java** — excellent performance on both `hashCode()` and `equals()`, with zero boilerplate.
- **Collection size has no impact on lookup time** — performance is stable across 100, 1,000 and 10,000 entries, confirming O(1) HashMap behavior. The difference comes purely from hashCode/equals cost.
- **Plain POJO `equals()` with manual short-circuit wins on isolated different-object comparisons**, but this advantage is irrelevant in practice since the `hashCode()` cost dominates collection lookups.

## Tech Stack

| Dependency | Version |
|---|---|
| Java | 25 |
| JMH | 1.37 |
| Lombok | 1.18.42 |
