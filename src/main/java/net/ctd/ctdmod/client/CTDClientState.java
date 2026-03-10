package net.ctd.ctdmod.client;

/**
 * État client lisible par le HUD (ex. barre de Qi).
 * Mis à jour par le code qui gère la touche de méditation ; ne contient pas de référence
 * à des classes client-only pour rester chargeable côté serveur.
 */
public final class CTDClientState {

    private static volatile boolean meditating;

    /**
     * Dernier état de la touche de méditation connu (pour détecter les changements).
     * Synchronisé avec le serveur via SyncMeditationStatePayload pour éviter que,
     * après une interruption forcée (ex. dégâts), une nouvelle pression ne soit ignorée.
     */
    private static volatile boolean lastMeditateKeyState;

    private CTDClientState() {
    }

    public static void setMeditating(boolean value) {
        meditating = value;
    }

    public static boolean isMeditating() {
        return meditating;
    }

    public static void setLastMeditateKeyState(boolean value) {
        lastMeditateKeyState = value;
    }

    public static boolean getLastMeditateKeyState() {
        return lastMeditateKeyState;
    }

    /**
     * Synchronise meditating et lastMeditateKeyState en une seule opération (ex. SyncMeditationStatePayload).
     */
    public static void syncFromServer(boolean active) {
        meditating = active;
        lastMeditateKeyState = active;
    }
}
