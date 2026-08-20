package com.prc.projectcell.ae2;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance monitoring and debugging utilities for caching system.
 * <p>
 * Tracks cache hit rates, performance metrics, and provides debug commands.
 */
public class PerformanceMonitor {
    private static final AtomicLong KEY_CACHE_HITS = new AtomicLong(0);
    private static final AtomicLong KEY_CACHE_MISSES = new AtomicLong(0);
    private static final AtomicLong EMC_CACHE_HITS = new AtomicLong(0);
    private static final AtomicLong EMC_CACHE_MISSES = new AtomicLong(0);
    private static final AtomicLong NBT_CACHE_HITS = new AtomicLong(0);
    private static final AtomicLong NBT_CACHE_MISSES = new AtomicLong(0);

    /**
     * Record a KEY_CACHE hit
     */
    public static void recordKeyCacheHit() {
        KEY_CACHE_HITS.incrementAndGet();
    }

    /**
     * Record a KEY_CACHE miss
     */
    public static void recordKeyCacheMiss() {
        KEY_CACHE_MISSES.incrementAndGet();
    }

    /**
     * Record an EMC_CACHE hit
     */
    public static void recordEMCCacheHit() {
        EMC_CACHE_HITS.incrementAndGet();
    }

    /**
     * Record an EMC_CACHE miss
     */
    public static void recordEMCCacheMiss() {
        EMC_CACHE_MISSES.incrementAndGet();
    }

    /**
     * Record an NBT_CACHE hit
     */
    public static void recordNBTCacheHit() {
        NBT_CACHE_HITS.incrementAndGet();
    }

    /**
     * Record an NBT_CACHE miss
     */
    public static void recordNBTCacheMiss() {
        NBT_CACHE_MISSES.incrementAndGet();
    }

    /**
     * Calculate KEY_CACHE hit rate
     */
    public static double getKeyCacheHitRate() {
        long total = KEY_CACHE_HITS.get() + KEY_CACHE_MISSES.get();
        return total == 0 ? 0 : (double) KEY_CACHE_HITS.get() / total;
    }

    /**
     * Calculate EMC_CACHE hit rate
     */
    public static double getEMCCacheHitRate() {
        long total = EMC_CACHE_HITS.get() + EMC_CACHE_MISSES.get();
        return total == 0 ? 0 : (double) EMC_CACHE_HITS.get() / total;
    }

    /**
     * Calculate NBT_CACHE hit rate
     */
    public static double getNBTCacheHitRate() {
        long total = NBT_CACHE_HITS.get() + NBT_CACHE_MISSES.get();
        return total == 0 ? 0 : (double) NBT_CACHE_HITS.get() / total;
    }

    /**
     * Get comprehensive performance report
     */
    public static String getReport() {

        return "═══════════════════════════════════════\n" +
                "ProjectCell Performance Report\n" +
                "═══════════════════════════════════════\n" +
                "KEY_CACHE:\n" +
                String.format("  Hit Rate: %.1f%% (%d hits, %d misses)\n",
                        getKeyCacheHitRate() * 100,
                        KEY_CACHE_HITS.get(),
                        KEY_CACHE_MISSES.get()) +
                "EMC_VALUE_CACHE:\n" +
                String.format("  Hit Rate: %.1f%% (%d hits, %d misses)\n",
                        getEMCCacheHitRate() * 100,
                        EMC_CACHE_HITS.get(),
                        EMC_CACHE_MISSES.get()) +
                "NBT_DECISION_CACHE:\n" +
                String.format("  Hit Rate: %.1f%% (%d hits, %d misses)\n",
                        getNBTCacheHitRate() * 100,
                        NBT_CACHE_HITS.get(),
                        NBT_CACHE_MISSES.get()) +
                "STORAGE_QUERY_BATCHER:\n" +
                String.format("  %s\n", StorageQueryBatcher.getStats()) +
                "═══════════════════════════════════════\n";
    }

    /**
     * Reset all counters (useful for benchmarking)
     */
    public static void reset() {
        KEY_CACHE_HITS.set(0);
        KEY_CACHE_MISSES.set(0);
        EMC_CACHE_HITS.set(0);
        EMC_CACHE_MISSES.set(0);
        NBT_CACHE_HITS.set(0);
        NBT_CACHE_MISSES.set(0);
    }

    /**
     * Log performance report to console
     */
    public static void logReport() {
        String report = getReport();
    }
}

