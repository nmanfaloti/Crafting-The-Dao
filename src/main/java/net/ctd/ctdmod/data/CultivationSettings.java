package net.ctd.ctdmod.data;

/**
 * Constantes de réglage pour le système de cultivation (Qi, niveaux et HUD).
 * <p>
 * Centralise les valeurs afin de faciliter les futurs ajustements et la
 * potentielles intégration avec la configuration du mod.
 */
public final class CultivationSettings {

    /** Nombre de paliers (I à V) par tier. */
    public static final int PALIERS_PER_TIER = 5;

    /**
     * Sous-niveau maximum : 20 tiers × 5 paliers = 100 (Mortal Body I → Heavenly Dao V).
     */
    public static final int MAX_SUBLEVEL = 100;

    /**
     * Facteur multiplicatif appliqué à la capacité max à chaque montée de sous-niveau (palier).
     * Ex. 1.75 → nouveau max = ancien max × 1.75 ; le Qi actuel est conservé (pas de reset).
     */
    public static final float QI_MAX_GROWTH_FACTOR = 1.75F;

    /**
     * Durée pendant laquelle la barre de Qi reste visible après un changement (en millisecondes).
     */
    public static final long HUD_DISPLAY_DURATION_MS = 5000L;

    /**
     * Durée du fondu d'apparition de la barre de Qi (en millisecondes).
     */
    public static final long HUD_FADE_IN_DURATION_MS = 500L;

    /**
     * Durée du fondu de disparition de la barre de Qi (en millisecondes).
     */
    public static final long HUD_FADE_DURATION_MS = 1000L;

    /**
     * Scale minimum de la barre pendant le fade-in/fade-out (effet plus visible).
     */
    public static final float HUD_FADE_SCALE_MIN = 0.92F;

    /**
     * Vitesse d'animation du remplissage de la barre (0–1) : plus élevé = la barre suit le Qi plus vite.
     */
    public static final float HUD_QI_LERP_SPEED = 0.2F;

    /**
     * Durée d'affichage du texte de montée de niveau au centre de l'écran (en millisecondes).
     */
    public static final long LEVEL_UP_DISPLAY_DURATION_MS = 3000L;

    // -------------------------------------------------------------------------
    // Méditation (touche + immobilité → gain de Qi)
    // -------------------------------------------------------------------------

    /** Qi accordé à chaque intervalle de méditation. */
    public static final float MEDITATION_QI_GAIN = 5.0F;

    /** Nombre de ticks entre deux gains de Qi en méditation (80 = 4 s à 20 TPS). */
    public static final long MEDITATION_INTERVAL_TICKS = 80L;

    /** Vitesse d'interpolation de la barre de Qi pendant la méditation (remplissage plus fluide). */
    public static final float MEDITATION_QI_LERP_SPEED = 0.1F;

    // -------------------------------------------------------------------------
    // Animation palier (I → II → III…)
    // -------------------------------------------------------------------------

    /** Durée totale du pulse de la barre au passage de palier (ms). */
    public static final long PALIER_PULSE_DURATION_MS = 350L;

    /** Scale max de la barre pendant le pulse (ex. 1.12 = +12 %). */
    public static final float PALIER_PULSE_SCALE_MAX = 1.12F;

    /** Alpha max du micro-flash sur la zone de la barre au palier. */
    public static final float PALIER_FLASH_ALPHA_MAX = 0.25F;

    /** Durée du micro-flash palier (ms). */
    public static final long PALIER_FLASH_DURATION_MS = 150L;

    // -------------------------------------------------------------------------
    // Animation tier (Mortal Body V → Body Refining I)
    // -------------------------------------------------------------------------

    /** Durée de l'animation de scale du titre au centre (ms). */
    public static final long TIER_TITLE_SCALE_DURATION_MS = 500L;

    /** Scale initial du titre (apparition). */
    public static final float TIER_TITLE_SCALE_START = 0.4F;

    /** Scale max du titre avant de rester à 1.0. */
    public static final float TIER_TITLE_SCALE_END = 1.2F;

    /** Durée du flash plein écran à la montée de tier (ms). */
    public static final long TIER_SCREEN_FLASH_DURATION_MS = 600L;

    /** Alpha max du flash plein écran (couleur du tier). */
    public static final float TIER_SCREEN_FLASH_ALPHA_MAX = 0.12F;

    private CultivationSettings() {
        // Classe utilitaire : pas d'instanciation.
    }
}

