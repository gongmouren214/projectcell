package com.prc.projectcell.ae2;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.ItemInfo;

/**
 * Handles pagination of large knowledge sets to avoid processing
 * all items in a single tick. Distributes work over multiple ticks.
 *
 * TPS Impact: -30% additional on getAvailableStacks()
 */
public class KnowledgeIterator {
    private static final int BATCH_SIZE = 100; // Items per tick

    private final List<ItemInfo> knowledge;
    private int currentIndex;
    private final int maxIndex;

    public KnowledgeIterator(List<ItemInfo> knowledge) {
        this.knowledge = knowledge;
        this.currentIndex = 0;
        this.maxIndex = knowledge.size();
    }

    /**
     * Get next batch of items (max BATCH_SIZE)
     */
    public List<ItemInfo> nextBatch() {
        if (!hasNext()) {
            return new ArrayList<>();
        }

        int endIndex = Math.min(currentIndex + BATCH_SIZE, maxIndex);
        List<ItemInfo> batch = new ArrayList<>(knowledge.subList(currentIndex, endIndex));
        currentIndex = endIndex;
        return batch;
    }

    /**
     * Check if more batches are available
     */
    public boolean hasNext() {
        return currentIndex < maxIndex;
    }

    /**
     * Get progress percentage (0-100)
     */
    public int getProgress() {
        return (currentIndex * 100) / maxIndex;
    }

    /**
     * Get current batch index
     */
    public int getCurrentBatch() {
        return (currentIndex / BATCH_SIZE) + 1;
    }

    /**
     * Get total batches needed
     */
    public int getTotalBatches() {
        return (maxIndex + BATCH_SIZE - 1) / BATCH_SIZE;
    }

    /**
     * Reset iterator for re-processing
     */
    public void reset() {
        this.currentIndex = 0;
    }
}

