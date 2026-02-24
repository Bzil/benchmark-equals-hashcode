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
└── HashCodeEqualsBenchmark.java # 9 JMH benchmarks
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

### Key Takeaways

- **`Objects.hash()` is a performance trap** — the varargs array allocation makes it ~9x slower than alternatives.
- **Lombok and Record are nearly identical for `hashCode()`**, both generating efficient inline computations.
- **Records got a significant `equals()` optimization in JDK 25** — `equalsTrue` dropped from 1.74 ns (JDK 21) to 1.43 ns.
- **Records are the best overall choice in modern Java** — excellent performance on both `hashCode()` and `equals()`, with zero boilerplate.
- **Plain POJO `equals()` with manual short-circuit wins on different objects**, but the `hashCode()` penalty from `Objects.hash()` makes it the worst overall performer for hash-based collections.

## Tech Stack

| Dependency | Version |
|---|---|
| Java | 25 |
| JMH | 1.37 |
| Lombok | 1.18.42 |
