# Sovereign Collections: ImmutableList

A high-performance, persistent, and truly immutable singly-linked list engineered for the modern Java ecosystem (**Java 21 to Java 25+**).

`ImmutableList` is built on the principles of **Data Sovereignty**. It ensures that once data is recorded, it can never be altered by side-effects, race conditions, or external processes. This makes it the ideal foundation for local-first applications, SRE tooling, and privacy-centric software.


## 🛠 Why ImmutableList?

Traditional Java collections like `ArrayList` are mutable, leading to defensive copying and complex synchronization in
multi-threaded environments. `ImmutableList` solves this through **Persistence** and **Structural Sharing**.

### 1. Structural Sharing

Instead of deep-copying data, `ImmutableList` shares existing nodes between different versions of a list. When you
`prepend` an item, a new head is created that points to the original list.

* **Memory Efficiency:** Additions are $O(1)$.
* **Zero Data Duplication:** Your original data remains untouched and shared in memory.

### 2. Thread Safety by Design

Since the structure is physically incapable of changing, it is inherently thread-safe. You can pass the list across
threads (e.g., from a background SRE monitoring task to a UI thread) without ever worrying about
`ConcurrentModificationException`.

---

## 📊 Performance & Complexity Analysis

| Operation                  | `ImmutableList` (Ours) | `ArrayList` (JDK)   | `LinkedList` (JDK) |
|:---------------------------|:-----------------------|:--------------------|:-------------------|
| **Prepend (Add to Front)** | **O(1)**               | O(n)                | O(1)               |
| **Append (Add to End)**    | O(n)                   | O(1)*               | O(1)               |
| **Head / Get First**       | **O(1)**               | O(1)                | O(1)               |
| **Tail / Get Rest**        | **O(1)**               | O(n)                | O(1)               |
| **Random Access (Get i)**  | O(n)                   | **O(1)**            | O(n)               |
| **Immutability**           | **Physical (Records)** | None (Wrapper only) | None               |

*\* ArrayList append is O(1) amortized, but O(n) during array resize.*

---

## 🧠 Memory Management & GC Impact

As an SRE-grade library, `ImmutableList` is optimized for the **Java Memory Model (JMM)** and modern Garbage
Collectors (G1, ZGC).

* **Young Generation Friendly:** Nodes are implemented as Java **Records**. They are lightweight and usually reclaimed
  in the Young Generation (Eden space) with near-zero latency.
* **Fragmentation Resistance:** Unlike `ArrayList`, which requires large contiguous blocks of memory, `ImmutableList`
  stores data in discrete nodes. This prevents "Stop-the-World" pauses caused by heap fragmentation.
* **JIT Optimizations:** The immutability of the nodes allows the Graal or HotSpot JIT compilers to perform aggressive
  optimizations like **Scalar Replacement**, potentially eliminating heap allocations entirely for local scopes.

---

## 🔮 Future-Proofing: Project Valhalla Ready

This implementation is architected to benefit from **Project Valhalla** without breaking changes.

1. **Value Objects:** By using `Records` and `Sealed Interfaces`, this library is ready to transition to **Value Types
   **. This will allow the JVM to flatten nodes in memory, removing object header overhead.
2. **Generic Specialization:** When Valhalla introduces primitive generics, `ImmutableList<int>` will be possible,
   offering performance parity with C++ `std::vector` while maintaining functional safety.
3. **Cache Locality:** Future JVMs will be able to "inline" these persistent nodes, significantly reducing the cache
   misses typically associated with linked structures.

---

## 🚀 Key Features

* **Sealed Interface + Records:** Ensures exhaustive pattern matching and prevents unauthorized implementations.
* **Stack-Safe Iteration:** Functional operations like `map` and `filter` are implemented iteratively to handle massive
  datasets without `StackOverflowError`.
* **Zero Dependencies:** A pure, single-file core library.

---

## 💻 Quick Start

### Installation

Move the `ImmutableList.java` file into your project under the package `io.sovereign.collections`.

### Usage

```java
import io.sovereign.collections.ImmutableList;

public class Main {
    public static void main(String[] args) {
        // Create a list
        var tasks = ImmutableList.of("Fix Bug", "Analyze Memory");

        // Structural sharing: original 'tasks' remains unchanged
        var updated = tasks.prepend("SRE Audit");

        // Functional transformations
        var lengths = updated.map(String::length);
        var filtered = updated.filter(s -> s.contains("Bug"));

        System.out.println(updated); // Output: [SRE Audit, Fix Bug, Analyze Memory]
    }
}
```

---

## ⚖️ License

This project is licensed under the MIT License.

---

### Engineering Notes

*This library is architected for systems where correctness and memory predictability are non-negotiable. It favors the safety of the Java Memory Model over the convenience of mutation.*