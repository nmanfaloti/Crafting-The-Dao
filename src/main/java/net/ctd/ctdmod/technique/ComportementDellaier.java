package net.ctd.ctdmod.technique;

public interface ComportementDellaier {
    void executer();

    int nombreExecutionsParCycle();

    Iterable<?> getElements();

    default int delaiEntreCycles() {
        return 0;
    }

    default boolean reprogrammerApresExecution() {
        return false;
    }
}