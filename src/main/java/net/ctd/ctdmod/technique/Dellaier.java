package net.ctd.ctdmod.technique;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Dellaier {
    private static final List<EntreeDellaier> jestionDellayer = new ArrayList<>();

    private Dellaier() {}

    public static void ajouter(ComportementDellaier comportement) {
        jestionDellayer.add(new EntreeDellaier(comportement));
    }

    public static boolean retirer(ComportementDellaier comportement) {
        return jestionDellayer.removeIf(entree -> entree.comportement == comportement);
    }

    public static List<ComportementDellaier> getJestionDellayer() {
        List<ComportementDellaier> comportements = new ArrayList<>();
        for (EntreeDellaier entree : jestionDellayer) {
            comportements.add(entree.comportement);
        }
        return Collections.unmodifiableList(comportements);
    }

    public static void tick() {
        Iterator<EntreeDellaier> iterator = jestionDellayer.iterator();

        while (iterator.hasNext()) {
            EntreeDellaier entree = iterator.next();
            if (entree.cyclesAvantExecution > 0) {
                entree.cyclesAvantExecution--;
                continue;
            }

            int executionsCycle = Math.max(1, entree.comportement.nombreExecutionsParCycle());
            for (int i = 0; i < executionsCycle && entree.elements.hasNext(); i++) {
                entree.comportement.executer();
                entree.elements.next();
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

    private static final class EntreeDellaier {
        private final ComportementDellaier comportement;
        private Iterator<?> elements;
        private int cyclesAvantExecution;

        private EntreeDellaier(ComportementDellaier comportement) {
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
