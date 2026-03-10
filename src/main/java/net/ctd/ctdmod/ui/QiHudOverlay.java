package net.ctd.ctdmod.ui;

import net.ctd.ctdmod.client.CTDClientState;
import net.ctd.ctdmod.data.CTDAttachments;
import net.ctd.ctdmod.data.CultivationData;
import net.ctd.ctdmod.data.CultivationSettings;
import net.ctd.ctdmod.data.CultivationTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class QiHudOverlay
{
  // Tes deux textures : fond et remplissage (à placer dans assets/ctdmod/textures/gui/).
  private static final ResourceLocation QI_BAR_BACKGROUND = ResourceLocation.fromNamespaceAndPath("ctdmod", "textures/gui/qi_bar_background.png");
  private static final ResourceLocation QI_BAR_FILL = ResourceLocation.fromNamespaceAndPath("ctdmod", "textures/gui/qi_bar_fill.png");

  // Dimensions d’affichage de la barre (ajuste si tes PNG ont d’autres dimensions).
  private static final int BAR_WIDTH = 182;
  private static final int BAR_HEIGHT = 5;

  // Derniers états connus pour détecter un changement de Qi côté client.
  private float lastQi = -1.0F;
  private float lastMaxQi = -1.0F;
  private int lastLevel = -1;
  private long lastChangeTimeMs = 0L;

  // Indique si le premier frame a déjà été traité (pour ne pas afficher la barre au chargement).
  private boolean hasSeenFirstFrame = false;

  // Informations d'affichage temporaire pour la notification de montée de niveau.
  private String levelUpTierName = null;
  private long levelUpDisplayUntilMs = 0L;

  // Valeur de Qi affichée (interpolée) pour animer le remplissage de la barre.
  private float displayedQi = -1.0F;

  // État des animations palier / tier.
  private long palierAnimStartMs = 0L;
  private long tierAnimStartMs = 0L;
  private int tierAnimTierColor = 0xFFFFFFFF;

  /**
   * Rendu de la barre de Qi en se basant sur les données de cultivation du joueur local.
   *
   * @param guiGraphics contexte de rendu GUI
   * @param screenWidth largeur de l'écran
   * @param screenHeight hauteur de l'écran
   * @param player joueur client dont on affiche le Qi
   */
  public void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight, LocalPlayer player)
  {
    CultivationData data = player.getData(CTDAttachments.CULTIVATION);

    float currentQi = data.qi();
    float maxQi = data.maxQi();
    int level = data.level();

    if (maxQi <= 0.0F) {
      return;
    }

    long now = System.currentTimeMillis();

    if (!hasSeenFirstFrame) {
      lastQi = currentQi;
      lastMaxQi = maxQi;
      lastLevel = level;
      hasSeenFirstFrame = true;
    } else {
      boolean changed = currentQi != lastQi || maxQi != lastMaxQi || level != lastLevel;
      if (changed) {
        boolean leveledUp = level > lastLevel;

        if (leveledUp) {
          levelUpTierName = CultivationTier.getDisplayNameForSublevel(level);
          levelUpDisplayUntilMs = now + CultivationSettings.LEVEL_UP_DISPLAY_DURATION_MS;

          // Nouveau tier (ex. level 5→6) : animation tier uniquement.
          boolean isTierChange = (level - 1) % CultivationSettings.PALIERS_PER_TIER == 0;
          if (isTierChange) {
            tierAnimStartMs = now;
            int tierIndex = CultivationTier.getTierIndexFromSublevel(level);
            tierAnimTierColor = CultivationTier.getFromLevel(tierIndex).getColor();
            palierAnimStartMs = 0L;
          } else {
            palierAnimStartMs = now;
          }
        }

        // Démarre l'animation du remplissage depuis l'ancienne valeur de Qi.
        displayedQi = lastQi;
        lastQi = currentQi;
        lastMaxQi = maxQi;
        lastLevel = level;
        lastChangeTimeMs = now;
      }
    }

    long elapsedSinceChange = lastChangeTimeMs == 0L ? Long.MAX_VALUE : now - lastChangeTimeMs;
    int x = (screenWidth / 2) - (BAR_WIDTH / 2);
    int y = screenHeight - 50;
    float barCenterX = screenWidth / 2.0F;
    float barCenterY = y + BAR_HEIGHT / 2.0F;

    // (1) Flash plein écran au passage de tier
    if (tierAnimStartMs > 0L) {
      long tierElapsed = now - tierAnimStartMs;
      if (tierElapsed < CultivationSettings.TIER_SCREEN_FLASH_DURATION_MS) {
        float t = (float) tierElapsed / (float) CultivationSettings.TIER_SCREEN_FLASH_DURATION_MS;
        float flashAlpha = CultivationSettings.TIER_SCREEN_FLASH_ALPHA_MAX * Mth.sin((float) Math.PI * t);
        int flashColor = (tierAnimTierColor & 0x00_FFFFFF) | ((int) (Mth.clamp(flashAlpha, 0.0F, 1.0F) * 255.0F) << 24);
        guiGraphics.fill(0, 0, screenWidth, screenHeight, flashColor);
      }
    }

    boolean showBar = (lastChangeTimeMs != 0L && elapsedSinceChange <= CultivationSettings.HUD_DISPLAY_DURATION_MS)
        || CTDClientState.isMeditating();
    if (showBar) {
      // Fade-in / fade-out alpha (pleine opacité pendant la méditation)
      float alpha = 1.0F;
      if (!CTDClientState.isMeditating()) {
        if (elapsedSinceChange < CultivationSettings.HUD_FADE_IN_DURATION_MS) {
          alpha = (float) elapsedSinceChange / (float) CultivationSettings.HUD_FADE_IN_DURATION_MS;
        } else {
          long fadeOutStart = CultivationSettings.HUD_DISPLAY_DURATION_MS - CultivationSettings.HUD_FADE_DURATION_MS;
          if (elapsedSinceChange > fadeOutStart) {
            long fadeElapsed = elapsedSinceChange - fadeOutStart;
            alpha = 1.0F - (float) fadeElapsed / (float) CultivationSettings.HUD_FADE_DURATION_MS;
          }
        }
        alpha = Mth.clamp(alpha, 0.0F, 1.0F);
      }

      // Scale de la barre pendant le fade (0.92 → 1.0)
      float barScale = 1.0F;
      if (elapsedSinceChange < CultivationSettings.HUD_FADE_IN_DURATION_MS) {
        barScale = Mth.lerp(alpha, CultivationSettings.HUD_FADE_SCALE_MIN, 1.0F);
      } else {
        long fadeOutStart = CultivationSettings.HUD_DISPLAY_DURATION_MS - CultivationSettings.HUD_FADE_DURATION_MS;
        if (elapsedSinceChange > fadeOutStart) {
          long fadeElapsed = elapsedSinceChange - fadeOutStart;
          float fadeOutAlpha = (float) fadeElapsed / (float) CultivationSettings.HUD_FADE_DURATION_MS;
          barScale = Mth.lerp(1.0F - Mth.clamp(fadeOutAlpha, 0.0F, 1.0F), 1.0F, CultivationSettings.HUD_FADE_SCALE_MIN);
        }
      }

      // Pulse palier (courbe 0 → 1 → 0 sur PALIER_PULSE_DURATION_MS)
      float pulseScale = 1.0F;
      if (palierAnimStartMs > 0L) {
        long palierElapsed = now - palierAnimStartMs;
        if (palierElapsed < CultivationSettings.PALIER_PULSE_DURATION_MS) {
          float t = (float) palierElapsed / (float) CultivationSettings.PALIER_PULSE_DURATION_MS;
          float curve = Mth.sin((float) Math.PI * t);
          pulseScale = 1.0F + (CultivationSettings.PALIER_PULSE_SCALE_MAX - 1.0F) * curve;
        }
      }

      float totalBarScale = barScale * pulseScale;

      // Interpolation du Qi affiché (lerp plus doux pendant la méditation)
      float lerpSpeed = CTDClientState.isMeditating()
          ? CultivationSettings.MEDITATION_QI_LERP_SPEED
          : CultivationSettings.HUD_QI_LERP_SPEED;
      if (displayedQi < 0.0F) {
        displayedQi = currentQi;
      } else {
        displayedQi = Mth.lerp(lerpSpeed, displayedQi, currentQi);
      }
      float progress = Mth.clamp(displayedQi / maxQi, 0.0f, 1.0f);
      int filledWidth = (int) (progress * BAR_WIDTH);

      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(barCenterX, barCenterY, 0.0);
      guiGraphics.pose().scale(totalBarScale, totalBarScale, 1.0F);
      guiGraphics.pose().translate(-barCenterX, -barCenterY, 0.0);

      guiGraphics.setColor(1.0F, 1.0F, 1.0F, alpha);
      guiGraphics.blit(QI_BAR_BACKGROUND, x, y, 0, 0, BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
      if (filledWidth > 0) {
        guiGraphics.blit(QI_BAR_FILL, x, y, 0, 0, filledWidth, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
      }
      guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

      guiGraphics.pose().popPose();

      // Micro-flash palier (rectangle couleur tier sur la barre)
      if (palierAnimStartMs > 0L) {
        long palierElapsed = now - palierAnimStartMs;
        if (palierElapsed < CultivationSettings.PALIER_FLASH_DURATION_MS) {
          float t = (float) palierElapsed / (float) CultivationSettings.PALIER_FLASH_DURATION_MS;
          float flashAlpha = CultivationSettings.PALIER_FLASH_ALPHA_MAX * Mth.sin((float) Math.PI * t);
          int tierColor = CultivationTier.getFromLevel(CultivationTier.getTierIndexFromSublevel(level)).getColor();
          int flashColor = (tierColor & 0x00_FFFFFF) | ((int) (Mth.clamp(flashAlpha, 0.0F, 1.0F) * 255.0F) << 24);
          guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, flashColor);
        }
      }
    }

    // (3) Titre montée de niveau (avec scale animé au passage de tier)
    if (levelUpTierName != null && now < levelUpDisplayUntilMs) {
      Minecraft minecraft = Minecraft.getInstance();
      Font font = minecraft.font;
      int centerX = screenWidth / 2;
      int centerY = screenHeight / 2;

      float titleScale = 1.0F;
      if (tierAnimStartMs > 0L) {
        long tierElapsed = now - tierAnimStartMs;
        if (tierElapsed < CultivationSettings.TIER_TITLE_SCALE_DURATION_MS) {
          float t = (float) tierElapsed / (float) CultivationSettings.TIER_TITLE_SCALE_DURATION_MS;
          if (t < 0.5F) {
            titleScale = Mth.lerp(t * 2.0F, CultivationSettings.TIER_TITLE_SCALE_START, CultivationSettings.TIER_TITLE_SCALE_END);
          } else {
            titleScale = Mth.lerp((t - 0.5F) * 2.0F, CultivationSettings.TIER_TITLE_SCALE_END, 1.0F);
          }
        }
      }

      guiGraphics.pose().pushPose();
      guiGraphics.pose().translate(centerX, centerY, 0.0);
      guiGraphics.pose().scale(titleScale, titleScale, 1.0F);
      guiGraphics.pose().translate(-centerX, -centerY, 0.0);
      guiGraphics.drawCenteredString(font, levelUpTierName, centerX, centerY, 0xFFFFFFFF);
      guiGraphics.pose().popPose();
    }
  }
}
