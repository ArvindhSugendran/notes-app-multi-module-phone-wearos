package com.app.notesappandroidproject.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * AppExecutors
 * <p>
 * This class manages thread execution for background and main thread tasks using different Executor instances.
 * It provides thread pools for disk operations, network operations, and UI thread execution to ensure efficient
 * asynchronous processing in the application. This avoids blocking the main thread and ensures better performance.
 * <p>
 * Features:
 * 1. Handles disk I/O tasks with a single thread executor.
 * 2. Handles network operations with a fixed thread pool that supports concurrent requests.
 * 3. Handles UI thread execution using a custom main thread handler.
 * 4. Singleton design pattern ensures a single instance of `AppExecutors` throughout the app lifecycle.
 * <p>
 * Dependencies:
 * - Uses standard Java Executors for thread management.
 * - Leverages Android's main thread handler for executing tasks on the UI thread safely.
 */
public class AppExecutors {

    private static final int THREAD_COUNT = 3;

    private final Executor diskIO; // Executor for disk I/O operations.
    private final Executor networkIO; // Executor for network operations.
    private final Executor mainThread; // Executor for main thread/UI operations.

    private static final AppExecutors INSTANCE = new AppExecutors(); // Singleton instance of AppExecutors.


    /**
     * Private constructor to initialize thread pools for disk I/O, network I/O, and the main thread.
     * Ensures that the instance follows a singleton pattern.
     */
    private AppExecutors() {
        this.diskIO = Executors.newSingleThreadExecutor();
        this.networkIO = Executors.newFixedThreadPool(THREAD_COUNT);
        this.mainThread = new MainThreadExecutor();
    }

    /**
     * Provides access to the singleton instance of AppExecutors.
     *
     * @return Singleton instance of AppExecutors.
     */
    public static AppExecutors getInstance() {
        return INSTANCE;
    }

    /**
     * Provides access to the disk I/O executor.
     *
     * @return Executor for disk I/O tasks.
     */
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * Provides access to the network I/O executor.
     *
     * @return Executor for network operations.
     */
    public Executor networkIO() {
        return networkIO;
    }

    /**
     * Provides access to the main thread executor for UI operations.
     *
     * @return Executor for main thread tasks.
     */
    public Executor mainThread() {
        return mainThread;
    }

    /**
     * MainThreadExecutor
     * <p>
     * A custom implementation of Executor to execute tasks on the Android main/UI thread using a Handler.
     * This is necessary for safely interacting with UI components from background threads.
     */
    private static class MainThreadExecutor implements Executor {
        private final android.os.Handler mainThreadHandler = new android.os.Handler(android.os.Looper.getMainLooper());

        /**
         * Executes a given Runnable on the main thread.
         *
         * @param command The task to execute.
         */
        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
