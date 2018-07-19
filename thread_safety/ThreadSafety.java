// Thread safety issue: a thread may read the static count variable before 
// another thread increments it, thus two threads will be incrementing the same count value.
class UnsafeCountThread extends Thread {
	public static int count = 0;

	@Override
	public void run() {
		count++;
		// System.out.println("Task number " + count);
	}
}

// Using one Counter object, we use the synchronized keyword
// to make sure only one thread is using the increment method (I think...), and
// make sure count is a volatile variable so that threads don't cache
// count in their own threads.
// (Synchronized does this.....)
// (Volatile forces all read and write operations to be on the main thread).
class Counter {
	private volatile int count;

	public Counter() {
		count = 0;
	}

	public synchronized void increment() {
		count++;
	}

	public int getCount() {
		return count;
	}
}

class SafeCountThread extends Thread {
	Counter counter;

	public SafeCountThread(Counter counter) {
		this.counter = counter;
	}

	@Override
	public void run() {
		counter.increment();
		// System.out.println("Count: " + counter.getCount());
	}
}

// Trying to count all threads using a static variable.
// ....this doesn't work, gotta see what synchronized is doing specifically.
class SafeCountThreadStatic extends Thread {
	public volatile static int count = 0;

	@Override
	public synchronized void run() {
		count++;
		// System.out.println("Task number " + count);
	}
}

public class ThreadSafety {

	public static void main(String[] args) {
		Thread[] threadArray;
		int generate = 100000;

		// Unsafely count threads
		threadArray = unsafeThreadCounter(generate);
		checkThreadsClosed(threadArray);
		System.out.println("Thread unsafe count: " + UnsafeCountThread.count);

		// Safely count threads using Count object
		Counter counter = new Counter(); // count object to help count all
											// threads
		threadArray = safeThreadCounter(counter, generate);
		checkThreadsClosed(threadArray);
		System.out.println("Thread safe count (using Count object): " + counter.getCount());

		// Safely count threads using static variable
		threadArray = safeThreadCounterStatic(generate);
		checkThreadsClosed(threadArray);
		System.out.println("Thread safe count (using static variable): " + SafeCountThreadStatic.count);

		System.out.println("All Done!");
	}

	public static Thread[] unsafeThreadCounter(int generate) {
		Thread[] threadArray = new Thread[generate];

		for (int i = 0; i < generate; i++) {
			Thread thread = new UnsafeCountThread();
			thread.start();
			threadArray[i] = thread;
		}

		return threadArray;
	}

	// Creates threadCount number of threads
	public static Thread[] safeThreadCounter(Counter counter, int generate) {
		Thread[] threadArray = new Thread[generate];

		for (int i = 0; i < generate; i++) {
			Thread thread = new SafeCountThread(counter);
			thread.start();
			threadArray[i] = thread;
		}

		return threadArray;
	}

	public static Thread[] safeThreadCounterStatic(int generate) {
		Thread[] threadArray = new Thread[generate];

		for (int i = 0; i < generate; i++) {
			Thread thread = new SafeCountThreadStatic();
			thread.start();
			threadArray[i] = thread;
		}

		return threadArray;
	}

	public static void checkThreadsClosed(Thread[] threadArray) {
		// Check when all threads are done
		try {
			for (int i = 0; i < threadArray.length; i++) {
				threadArray[i].join();
			}
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted");
		}
	}

}

// -------- Another way to implement Thread tasks ---------
//
// class IncrementingRunnable implements Runnable {
// private static int count = 0;
//
// @Override
// public void run() {
// count++;
// System.out.println("Task number " + count);
// }
// }