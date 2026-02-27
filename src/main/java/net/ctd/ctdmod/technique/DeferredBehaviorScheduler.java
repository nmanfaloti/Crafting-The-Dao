package net.ctd.ctdmod.technique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * DeferredBehaviorScheduler is a utility class that manages deferred execution
 * of behaviors defined by the DeferredBehavior interface.
 * It allows adding, removing, and executing behaviors in a controlled manner,
 * with support for execution cycles and delays.
 *
 * The tick method should be called regularly (e.g., every game tick)
 * to process the queued behaviors.
 */
public class DeferredBehaviorScheduler {
    private static final List<ScheduledBehaviorEntry> scheduledBehaviors = new ArrayList<>();

    private DeferredBehaviorScheduler() {}

    public static void add(DeferredBehavior behavior) {
        scheduledBehaviors.add(new ScheduledBehaviorEntry(behavior));
    }

    public static boolean remove(DeferredBehavior behavior) {
        return scheduledBehaviors.removeIf(entry -> entry.behavior == behavior);
    }

    public static List<DeferredBehavior> getScheduledBehaviors() {
        List<DeferredBehavior> behaviors = new ArrayList<>();
        for (ScheduledBehaviorEntry entry : scheduledBehaviors) {
            behaviors.add(entry.behavior);
        }
        return Collections.unmodifiableList(behaviors);
    }

    public static void tick() {
        Iterator<ScheduledBehaviorEntry> iterator = scheduledBehaviors.iterator();

        while (iterator.hasNext()) {
            ScheduledBehaviorEntry entry = iterator.next();
            if (entry.cyclesBeforeExecution > 0) {
                entry.cyclesBeforeExecution--;
                continue;
            }

            int executionsPerCycle = Math.max(1, entry.behavior.getExecutionsPerCycle());
            for (int i = 0; i < executionsPerCycle; i++) {
                entry.behavior.execute();
                if (entry.elements.hasNext()) {
                    entry.elements.next();
                }
    
            }

            if (!entry.elements.hasNext()) {
                if (entry.behavior.shouldRescheduleAfterExecution()) {
                    entry.reinitialize();
                } else {
                    iterator.remove();
                }
            } else {
                entry.cyclesBeforeExecution = Math.max(0, entry.behavior.getDelayBetweenCycles());
            }
        }
    }

    public static void clear() {
        scheduledBehaviors.clear();
    }

    // Internal class to represent a scheduled behavior entry with its execution state

    private static final class ScheduledBehaviorEntry {
        private final DeferredBehavior behavior;
        private Iterator<?> elements;
        private int cyclesBeforeExecution;

        private ScheduledBehaviorEntry(DeferredBehavior behavior) {
            this.behavior = behavior;
            this.elements = behavior.getElements().iterator();
            this.cyclesBeforeExecution = Math.max(0, behavior.getDelayBetweenCycles());
        }

        private void reinitialize() {
            this.elements = this.behavior.getElements().iterator();
            this.cyclesBeforeExecution = Math.max(0, this.behavior.getDelayBetweenCycles());
        }
    }
}