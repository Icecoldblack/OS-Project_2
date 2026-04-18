# OS Project 2 — Process Simulation & Synchronization

**Course:** Operating Systems — Spring 2025  
**Language:** Java 21  
**Entry Point:** `src/main.java`

---

## What This Project Does

This project simulates real-time process execution and thread synchronization — two of the most fundamental concepts in operating systems. It runs three demonstrations back-to-back from a single file:

| Part | Topic | Concept |
|---|---|---|
| 1 | Process Thread Simulation | Concurrency, CPU burst |
| 2 | Producer-Consumer | Mutex, Semaphores, Bounded Buffer |
| 3 | Dining Philosophers | Deadlock Avoidance, ReentrantLock |

---

## Project Structure

```
OS_Project_2/
├── src/
│   └── main.java          # All source code (Parts 1, 2, 3)
├── processes.txt          # Input file with process IDs and burst times
└── README.md              # This file
```

---

## How to Run

### From Eclipse
1. Open `src/main.java`
2. Click the **Run** button 
3. View output in the Console panel

### From the Terminal
```powershell
# Compile
javac -d bin (Get-ChildItem src/*.java | ForEach-Object { $_.FullName })

# Run
java -cp bin main
```

> **Note:** Run from the `OS_Project_2/` directory so `processes.txt` is found correctly.

---

## Input File — processes.txt

Each line defines one process: `pid  burstTime`

```
1 3
2 1
3 5
4 2
5 4
```

If `processes.txt` is not found, the program falls back to 4 built-in demo processes.

---

## Part 1 — Process Thread Simulation

Each line in `processes.txt` becomes a Java thread. The thread logs when it starts, sleeps for `burstTime` seconds (simulating CPU usage), then logs when it finishes. All threads run concurrently.

**Sample output:**
```
Process 2 started.  (burst = 1s)
Process 1 started.  (burst = 3s)
Process 2 finished.
Process 1 finished.
```

---

## Part 2 — Producer-Consumer

A `Producer` thread generates 10 items into a shared `BoundedBuffer` (max 5 slots). Two `Consumer` threads pull items out. Three synchronization primitives prevent any bugs:

- **`ReentrantLock` (mutex)** — only one thread accesses the buffer at a time
- **`Semaphore empty`** — producer blocks when buffer is full
- **`Semaphore full`** — consumers block when buffer is empty

**Sample output:**
```
[Producer]    Added item 3   | Buffer size: 2/5
[Consumer 1]  Removed item 3 | Buffer size: 1/5
[Consumer 1]  Processing item 3...
```

---

## Part 3 — Dining Philosophers

Five philosopher threads sit at a round table with five forks (locks) between them. Each philosopher must hold two forks to eat. Without coordination, they deadlock.

**Deadlock solution:** Always pick up the **lower-numbered fork first**. This breaks the circular-wait condition, guaranteeing every philosopher eventually eats.

**Sample output:**
```
[Philosopher 2] Waiting for forks 2 and 3...
[Philosopher 2] Picked up fork 2 and 3
[Philosopher 2] Eating...
[Philosopher 2] Released forks 2 and 3
```

---

## Grading Checklist

| Requirement | Status |
|---|---|
| Thread creation from `processes.txt` | Done - Part 1 |
| Correct synchronization implementation | Done - Part 2 (mutex + semaphores) |
| Output clarity — logs show execution order | Done - All 3 parts |
| Report with explanation | Done - `REPORT.md` |
| Source code with comments | Done - `main.java` |
| Input file (`processes.txt`) | Done - Included |
