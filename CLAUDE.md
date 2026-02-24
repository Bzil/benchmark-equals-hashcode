# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

JMH benchmark suite comparing `hashCode()` and `equals()` performance across four Java implementations: hand-written POJO (`Objects.hash`), Lombok (`@EqualsAndHashCode`), Java Record (`invokedynamic`), and Apache Commons (`EqualsBuilder`/`HashCodeBuilder`).

## Build & Run

```bash
mvn clean package
java -jar target/benchmarks.jar
```

Run a subset of benchmarks:
```bash
java -jar target/benchmarks.jar ".*hashCode.*"
java -jar target/benchmarks.jar -f 1 -wi 3    # fast dev run: 1 fork, 3 warmup iterations
```

There are no unit tests — this project only contains JMH benchmarks.

## Architecture

- `bz.benchmark.model/` — Four Person implementations (PlainPerson, LombokPerson, RecordPerson, CommonsPerson), each with the same 5 fields (firstName, lastName, age, email, city) but different equals/hashCode strategies
- `HashCodeEqualsBenchmark` — Micro-benchmarks for raw `hashCode()` and `equals()` calls (12 benchmarks)
- `CollectionBenchmark` — `HashSet.contains()` and `HashMap.get()` benchmarks parameterized by collection size (16 benchmarks x 3 sizes)

Both benchmark classes have `main()` methods and are packaged into a single uber-jar via maven-shade-plugin with `org.openjdk.jmh.Main` as the entry point.

## Tech Stack

- Java 25 (see `.java-version`)
- Maven with maven-shade-plugin (produces `target/benchmarks.jar`)
- JMH 1.37, Lombok 1.18.42, Commons Lang 3.17.0
