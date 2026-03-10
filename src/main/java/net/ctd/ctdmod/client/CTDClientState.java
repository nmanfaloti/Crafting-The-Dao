package net.ctd.ctdmod.client;

/**
 * État client lisible par le HUD (ex. barre de Qi).
 * Mis à jour par le code qui gère la touche de méditation ; ne contient pas de référence
 * à des classes client-only pour rester chargeable côté serveur.
 */
public final class CTDClientState {

    private static volatile boolean meditating;

    private CTDClientState() {
    }

    public static void setMeditating(boolean value) {
        meditating = value;
    }

    public static boolean isMeditating() {
        return meditating;
    }
}
