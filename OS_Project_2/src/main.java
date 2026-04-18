import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// ─────────────────────────────────────────────────────────────────
// PART 1 — ProcessThread
// Represents an OS process. Each thread simulates a CPU burst
// by sleeping for burstTime seconds.
// ─────────────────────────────────────────────────────────────────
class ProcessThread extends Thread {
    int pid;
    int burstTime;

    public ProcessThread(int pid, int burstTime) {
        this.pid = pid;
        this.burstTime = burstTime;
    }

    @Override
    public void run() {
        System.out.println("Process " + pid + " started.  (burst = " + burstTime + "s)");
        try {
            Thread.sleep(burstTime * 1000L);
        } catch (InterruptedException e) {
            System.out.println("Process " + pid + " was interrupted.");
        }
        System.out.println("Process " + pid + " finished.");
    }
}

// ─────────────────────────────────────────────────────────────────
// PART 2 — BoundedBuffer
// Shared buffer between Producer and Consumer.
// mutex  → only one thread touches the buffer at a time
// empty  → semaphore counting free slots  (producer waits when 0)
// full   → semaphore counting filled slots (consumer waits when 0)
// ─────────────────────────────────────────────────────────────────
class BoundedBuffer {
    private static final int CAPACITY = 5;
    private final Queue<Integer> buffer = new LinkedList<>();
    private final Lock mutex            = new ReentrantLock();
    private final Semaphore empty       = new Semaphore(CAPACITY);
    private final Semaphore full        = new Semaphore(0);

    public void produce(int item) throws InterruptedException {
        empty.acquire();
        mutex.lock();
        try {
            buffer.add(item);
            System.out.printf("[Producer] Added item %-3d | Buffer size: %d/%d%n",
                              item, buffer.size(), CAPACITY);
        } finally {
            mutex.unlock();
        }
        full.release();
    }

    public int consume(int consumerId) throws InterruptedException {
        full.acquire();
        mutex.lock();
        int item;
        try {
            item = buffer.poll();
            System.out.printf("[Consumer %d] Removed item %-3d | Buffer size: %d/%d%n",
                              consumerId, item, buffer.size(), CAPACITY);
        } finally {
            mutex.unlock();
        }
        empty.release();
        return item;
    }
}

// ─────────────────────────────────────────────────────────────────
// PART 2 — Producer
// Generates NUM_ITEMS and places them into the BoundedBuffer.
// ─────────────────────────────────────────────────────────────────
class Producer extends Thread {
    private static final int NUM_ITEMS = 10;
    private final BoundedBuffer buffer;

    public Producer(BoundedBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        System.out.println("[Producer] Starting — will produce " + NUM_ITEMS + " items.");
        try {
            for (int i = 1; i <= NUM_ITEMS; i++) {
                buffer.produce(i);
                Thread.sleep((long) (Math.random() * 500));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[Producer] Done producing.");
    }
}

// ─────────────────────────────────────────────────────────────────
// PART 2 — Consumer
// Removes items from the BoundedBuffer and "processes" them.
// ─────────────────────────────────────────────────────────────────
class Consumer extends Thread {
    private final int id;
    private final BoundedBuffer buffer;
    private final int itemsToConsume;

    public Consumer(int id, BoundedBuffer buffer, int itemsToConsume) {
        this.id             = id;
        this.buffer         = buffer;
        this.itemsToConsume = itemsToConsume;
    }

    @Override
    public void run() {
        System.out.println("[Consumer " + id + "] Starting — will consume " + itemsToConsume + " items.");
        try {
            for (int i = 0; i < itemsToConsume; i++) {
                int item = buffer.consume(id);
                System.out.println("[Consumer " + id + "] Processing item " + item + "...");
                Thread.sleep((long) (Math.random() * 800));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[Consumer " + id + "] Done consuming.");
    }
}

// ─────────────────────────────────────────────────────────────────
// main — entry point for BOTH parts
// ─────────────────────────────────────────────────────────────────
public class main {

    public static void main(String[] args) throws Exception {

        // ══════════════════════════════════════════
        //  PART 1 — Process Simulation with Threads
        // ══════════════════════════════════════════
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  PART 1  Process Thread Simulation   ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        List<ProcessThread> threads = new ArrayList<>();

        File file = new File("processes.txt");
        if (!file.exists()) {
            System.out.println("processes.txt not found — using default demo processes.");
            threads.add(new ProcessThread(1, 3));
            threads.add(new ProcessThread(2, 1));
            threads.add(new ProcessThread(3, 5));
            threads.add(new ProcessThread(4, 2));
        } else {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                int pid       = Integer.parseInt(parts[0]);
                int burstTime = Integer.parseInt(parts[1]);
                threads.add(new ProcessThread(pid, burstTime));
            }
            scanner.close();
        }

        System.out.println("=== Starting " + threads.size() + " process thread(s) ===\n");
        for (ProcessThread t : threads) t.start();
        for (ProcessThread t : threads) t.join();
        System.out.println("\n=== All processes have finished. ===");

        // ══════════════════════════════════════════
        //  PART 2 — Producer-Consumer with Semaphores
        // ══════════════════════════════════════════
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║  PART 2  Producer-Consumer Synchronization     ║");
        System.out.println("╚══════════════════════════════════════════════==╝\n");

        final int TOTAL_ITEMS   = 10;
        final int NUM_CONSUMERS = 2;

        BoundedBuffer buffer     = new BoundedBuffer();
        Producer      producer   = new Producer(buffer);
        Consumer[]    consumers  = new Consumer[NUM_CONSUMERS];
        int           itemsEach  = TOTAL_ITEMS / NUM_CONSUMERS;

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumers[i] = new Consumer(i + 1, buffer, itemsEach);
        }

        producer.start();
        for (Consumer c : consumers) c.start();

        producer.join();
        for (Consumer c : consumers) c.join();

        System.out.println("\n=== All threads finished. Producer-Consumer complete. ===");
    }
}
