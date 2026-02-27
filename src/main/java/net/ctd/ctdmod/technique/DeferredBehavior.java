package net.ctd.ctdmod.technique;

/*
 * Interface representing a behavior to be executed in a deferred manner.
 * It defines an action to execute, the number of executions per cycle,
 * the concerned elements, as well as delay and rescheduling options.
 *
 * PS: the implementing class must manage the storage of the element to process itself.
 */
public interface DeferredBehavior {
    void execute();

    int getExecutionsPerCycle();

    Iterable<?> getElements();

    default int getDelayBetweenCycles() {
        return 0;
    }

    default boolean shouldRescheduleAfterExecution() {
        return false;
    }
}