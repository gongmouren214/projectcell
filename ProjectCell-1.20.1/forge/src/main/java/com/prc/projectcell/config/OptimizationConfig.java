package com.prc.projectcell.config;

/**
 * Performance optimization configuration.
 * <p>
 * Controls caching behavior, async processing, and batch sizes.
 */
public class OptimizationConfig {

    // ===== PHASE 2: Caching =====
    public static final int KEY_CACHE_MAX_SIZE = 1024;
    public static final int KNOWLEDGE_CACHE_MAX_SIZE = 100;

    // ===== PHASE 3: Advanced Caching =====
    public static final int EMC_VALUE_CACHE_MAX_SIZE = 2048;
    public static final int NBT_DECISION_CACHE_MAX_SIZE = 2048;
    public static final long CACHE_TTL_MS = 1000; // 1 second

    // ===== PHASE 3: Batching =====
    public static final int KNOWLEDGE_BATCH_SIZE = 100;
    public static final int STORAGE_QUERY_BATCH_SIZE = 5;
    public static final int STORAGE_QUERY_QUEUE_SIZE = 512;

    // ===== TIER 4: Async Processing =====
    public static final int ASYNC_THREAD_POOL_SIZE = 2; // Conservative
    public static final boolean ASYNC_PROCESSING_ENABLED = true; // Can disable if issues
    public static final int ASYNC_BATCH_SIZE = 200; // Items to process per async task

    // ===== TIER 4: Monitoring =====
    public static final boolean PERFORMANCE_MONITORING_ENABLED = true;
    public static final int MONITORING_LOG_INTERVAL_TICKS = 1200; // 60 seconds

    // ===== Debug =====
    public static final boolean DEBUG_LOGGING_ENABLED = false;

    /**
     * Print all optimization settings to console
     */
    public static void printConfig() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("ProjectCell Optimization Configuration\n");
        sb.append("═══════════════════════════════════════════════\n");

        sb.append("PHASE 2 - Caching:\n");
        sb.append("  KEY_CACHE_MAX_SIZE: ").append(KEY_CACHE_MAX_SIZE).append("\n");
        sb.append("  KNOWLEDGE_CACHE_MAX_SIZE: ").append(KNOWLEDGE_CACHE_MAX_SIZE).append("\n");

        sb.append("PHASE 3 - Advanced Caching:\n");
        sb.append("  EMC_VALUE_CACHE_MAX_SIZE: ").append(EMC_VALUE_CACHE_MAX_SIZE).append("\n");
        sb.append("  NBT_DECISION_CACHE_MAX_SIZE: ").append(NBT_DECISION_CACHE_MAX_SIZE).append("\n");
        sb.append("  CACHE_TTL_MS: ").append(CACHE_TTL_MS).append("\n");

        sb.append("PHASE 3 - Batching:\n");
        sb.append("  KNOWLEDGE_BATCH_SIZE: ").append(KNOWLEDGE_BATCH_SIZE).append("\n");
        sb.append("  STORAGE_QUERY_BATCH_SIZE: ").append(STORAGE_QUERY_BATCH_SIZE).append("\n");
        sb.append("  STORAGE_QUERY_QUEUE_SIZE: ").append(STORAGE_QUERY_QUEUE_SIZE).append("\n");

        sb.append("TIER 4 - Async Processing:\n");
        sb.append("  ASYNC_THREAD_POOL_SIZE: ").append(ASYNC_THREAD_POOL_SIZE).append("\n");
        sb.append("  ASYNC_PROCESSING_ENABLED: ").append(ASYNC_PROCESSING_ENABLED).append("\n");
        sb.append("  ASYNC_BATCH_SIZE: ").append(ASYNC_BATCH_SIZE).append("\n");

        sb.append("═══════════════════════════════════════════════\n");

        System.out.println(sb);
    }
}

