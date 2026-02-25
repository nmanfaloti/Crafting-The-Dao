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
    private static final List<ScheduledBehaviorEntry> jestionDellayer = new ArrayList<>();

    private DeferredBehaviorScheduler() {}

    public static void ajouter(DeferredBehavior comportement) {
        jestionDellayer.add(new ScheduledBehaviorEntry(comportement));
    }

    public static boolean retirer(DeferredBehavior comportement) {
        return jestionDellayer.removeIf(entree -> entree.comportement == comportement);
    }

    public static List<DeferredBehavior> getJestionDellayer() {
        List<DeferredBehavior> comportements = new ArrayList<>();
        for (ScheduledBehaviorEntry entree : jestionDellayer) {
            comportements.add(entree.comportement);
        }
        return Collections.unmodifiableList(comportements);
    }

    public static void tick() {
        Iterator<ScheduledBehaviorEntry> iterator = jestionDellayer.iterator();

        while (iterator.hasNext()) {
            ScheduledBehaviorEntry entree = iterator.next();
            if (entree.cyclesAvantExecution > 0) {
                entree.cyclesAvantExecution--;
                continue;
            }

            int executionsCycle = Math.max(1, entree.comportement.nombreExecutionsParCycle());
            for (int i = 0; i < executionsCycle; i++) {
                entree.comportement.executer();
                if (entree.elements.hasNext()) entree.elements.next();  // Minimal one time execution 
    
            }

            if (!entree.elements.hasNext()) {
                if (entree.comportement.reprogrammerApresExecution()) {
                    entree.reinitialiser();
                } else {
                    iterator.remove();
                }
            } else {
                entree.cyclesAvantExecution = Math.max(0, entree.comportement.delaiEntreCycles());
            }
        }
    }

    public static void vider() {
        jestionDellayer.clear();
    }

    // Internal class to represent a scheduled behavior entry with its execution state

    private static final class ScheduledBehaviorEntry {
        private final DeferredBehavior comportement;
        private Iterator<?> elements;
        private int cyclesAvantExecution;

        private ScheduledBehaviorEntry(DeferredBehavior comportement) {
            this.comportement = comportement;
            this.elements = comportement.getElements().iterator();
            this.cyclesAvantExecution = Math.max(0, comportement.delaiEntreCycles());
        }

        private void reinitialiser() {
            this.elements = this.comportement.getElements().iterator();
            this.cyclesAvantExecution = Math.max(0, this.comportement.delaiEntreCycles());
        }
    }
}