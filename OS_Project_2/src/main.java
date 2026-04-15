import java.io.*;
import java.util.*;

// ─────────────────────────────────────────────
// ProcessThread: represents one OS process
// Each process runs as its own thread and
// simulates CPU burst time using Thread.sleep()
// ─────────────────────────────────────────────
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
            // Simulate the CPU burst — sleep for burstTime seconds
            Thread.sleep(burstTime * 1000L);
        } catch (InterruptedException e) {
            System.out.println("Process " + pid + " was interrupted.");
        }
        System.out.println("Process " + pid + " finished.");
    }
}

// ─────────────────────────────────────────────
// Main: reads processes.txt, spawns a thread
//       for every process, then waits for all
//       of them to finish
// ─────────────────────────────────────────────
public class main {

    public static void main(String[] args) throws Exception {

        List<ProcessThread> threads = new ArrayList<>();

        // ── Read processes from file ──────────────────
        // Expected format per line:  pid burstTime
        // Example:  1 5
        //           2 3
        //           3 8
        File file = new File("processes.txt");

        if (!file.exists()) {
            System.out.println("processes.txt not found — using default demo processes.");
            // Demo data so you can run without the file
            threads.add(new ProcessThread(1, 3));
            threads.add(new ProcessThread(2, 1));
            threads.add(new ProcessThread(3, 5));
            threads.add(new ProcessThread(4, 2));
        } else {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;          // skip blank lines
                String[] parts = line.split("\\s+");  // split on whitespace
                int pid       = Integer.parseInt(parts[0]);
                int burstTime = Integer.parseInt(parts[1]);
                threads.add(new ProcessThread(pid, burstTime));
            }
            scanner.close();
        }

        System.out.println("=== Starting " + threads.size() + " process thread(s) ===\n");

        // ── Start every thread (all run concurrently) ─
        for (ProcessThread t : threads) {
            t.start();
        }

        // ── Wait for every thread to finish ──────────
        // join() blocks until that thread completes
        for (ProcessThread t : threads) {
            t.join();
        }

        System.out.println("\n=== All processes have finished. ===");
    }
}
