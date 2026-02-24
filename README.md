# hashCode & equals Benchmark: Lombok vs Plain POJO vs Record vs Apache Commons

JMH benchmark comparing `hashCode()` and `equals()` performance across four Java implementations of the same data class:

- **Plain POJO** — hand-written `equals()`/`hashCode()` using `Objects.equals` and `Objects.hash`
- **Lombok POJO** — `@EqualsAndHashCode` generated code
- **Java Record** — compiler-generated `equals()`/`hashCode()` via `invokedynamic`
- **Apache Commons** — `EqualsBuilder`/`HashCodeBuilder` from commons-lang3

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
src/main/java/bz/benchmark/
├── model/
│   ├── PlainPerson.java        # Manual equals/hashCode with Objects.hash
│   ├── LombokPerson.java       # @EqualsAndHashCode @Getter @AllArgsConstructor
│   ├── RecordPerson.java       # Java record
│   └── CommonsPerson.java      # EqualsBuilder / HashCodeBuilder
├── HashCodeEqualsBenchmark.java # 12 raw hashCode/equals benchmarks
└── CollectionBenchmark.java    # 16 Set/Map lookup benchmarks (x3 sizes)
```

## Results

Measured on JDK 25 (OpenJDK Zulu 25+36-LTS), average time in nanoseconds per operation.

### hashCode()

| Implementation | ns/op | Notes |
|---|---|---|
| **Lombok** | **1.34** | Inline field-by-field computation |
| **Record** | **1.34** | `invokedynamic` via `ObjectMethods.bootstrap` |
| **Commons** | **1.34** | `HashCodeBuilder` append chain |
| Plain | 11.69 | `Objects.hash()` allocates a varargs `Object[]` |

### equals() — equal objects

| Implementation | ns/op |
|---|---|
| **Commons** | **1.07** |
| **Record** | **1.33** |
| **Plain** | **1.35** |
| Lombok | 1.76 |

### equals() — different objects (short-circuit on first field)

| Implementation | ns/op |
|---|---|
| **Lombok** | **0.65** |
| **Commons** | **0.65** |
| **Plain** | **0.67** |
| Record | 1.21 |

### Collection Lookups

Real-world performance measured with `HashSet.contains()` and `HashMap.get()`, parameterized across 100, 1000 and 10,000 entries.

#### Set.contains() — hit

| Implementation | 100 | 1,000 | 10,000 |
|---|---|---|---|
| **Lombok** | **7.30** | **7.48** | **7.43** |
| **Record** | **7.89** | **7.98** | **7.84** |
| **Commons** | **7.90** | **8.19** | **8.42** |
| Plain | 19.61 | 21.45 | 20.24 |

#### Set.contains() — miss

| Implementation | 100 | 1,000 | 10,000 |
|---|---|---|---|
| **Record** | **1.99** | **1.99** | **2.00** |
| **Commons** | **2.23** | **1.93** | **1.96** |
| **Lombok** | **1.99** | **2.67** | **2.00** |
| Plain | 13.71 | 13.30 | 14.00 |

#### Map.get() — hit

| Implementation | 100 | 1,000 | 10,000 |
|---|---|---|---|
| **Lombok** | **7.32** | **7.45** | **7.33** |
| **Record** | **7.95** | **8.03** | **8.07** |
| **Commons** | **8.16** | **8.18** | **8.61** |
| Plain | 17.63 | 19.84 | 19.76 |

#### Map.get() — miss

| Implementation | 100 | 1,000 | 10,000 |
|---|---|---|---|
| **Record** | **1.95** | **1.88** | **1.87** |
| **Commons** | **2.08** | **1.89** | **1.82** |
| **Lombok** | **1.91** | **2.63** | **1.87** |
| Plain | 17.26 | 15.70 | 15.79 |

### Key Takeaways

- **`Objects.hash()` is a performance trap** — the varargs array allocation makes it ~9x slower than alternatives, and this directly impacts every Set/Map operation.
- **Plain POJO is 2-7x slower in collection lookups** — the `hashCode()` penalty from `Objects.hash()` dominates real-world performance in hash-based collections.
- **Lombok, Record and Commons are nearly identical for `hashCode()`**, all generating efficient inline computations (~1.34 ns).
- **Commons `EqualsBuilder` is the fastest for equals on equal objects** (1.07 ns) — the JIT fully inlines the builder chain.
- **Commons performs on par with Lombok and Record in collections** — no measurable overhead from the builder pattern after JIT optimization.
- **Records are the best overall choice in modern Java** — excellent performance on both `hashCode()` and `equals()`, with zero boilerplate and no external dependency.
- **Collection size has no impact on lookup time** — performance is stable across 100, 1,000 and 10,000 entries, confirming O(1) HashMap behavior. The difference comes purely from hashCode/equals cost.

## Tech Stack

| Dependency | Version |
|---|---|
| Java | 25 |
| JMH | 1.37 |
| Lombok | 1.18.42 |
| Commons Lang | 3.17.0 |
