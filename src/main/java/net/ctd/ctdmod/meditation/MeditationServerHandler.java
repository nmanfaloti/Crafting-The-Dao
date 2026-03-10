package net.ctd.ctdmod.meditation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.ctd.ctdmod.Config;
import net.ctd.ctdmod.data.CultivationUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Gère l'état de méditation côté serveur : enregistrement des joueurs « en méditation » et
 * attribution du Qi à intervalle lorsqu'ils restent immobiles.
 */
public final class MeditationServerHandler {

  /** Seuil horizontal uniquement (ignore l'axe Y / gravité). */
  private static final double MOVEMENT_THRESHOLD_SQ = 1.0E-6;

  private static final Map<UUID, MutableMeditationState> STATE_BY_PLAYER =
      new ConcurrentHashMap<>();

  private MeditationServerHandler() {}

  /**
   * Indique si au moins un joueur est actuellement en méditation. Permet d'éviter tout traitement
   * du tick quand la map est vide.
   */
  public static boolean hasAnyMeditators() {
    return !STATE_BY_PLAYER.isEmpty();
  }

  /**
   * Enregistre ou désenregistre le joueur comme étant en méditation. Appelé par le handler du
   * paquet {@link net.ctd.ctdmod.network.MeditatePayload}.
   */
  public static void setMeditating(ServerPlayer player, boolean active) {
    UUID id = player.getUUID();
    if (active) {
      STATE_BY_PLAYER.put(id, new MutableMeditationState());
      player.setPose(Pose.CROUCHING);
    } else {
      STATE_BY_PLAYER.remove(id);
      player.setPose(Pose.STANDING);
    }
  }

  /**
   * Appelé chaque tick côté serveur pour chaque joueur. Si le joueur est en méditation et immobile,
   * incrémente le compteur et accorde du Qi à chaque intervalle.
   */
  public static void tick(ServerPlayer player) {
    MutableMeditationState state = STATE_BY_PLAYER.get(player.getUUID());
    if (state == null) return;

    // Seul le mouvement horizontal compte ; l'axe Y est ignoré car la gravité
    // applique en permanence un deltaMovement.y ≈ −0.0784, même au sol.
    Vec3 dm = player.getDeltaMovement();
    if (dm.x * dm.x + dm.z * dm.z >= MOVEMENT_THRESHOLD_SQ) {
      state.ticksAccumulated = 0;
      return;
    }

    player.setPose(Pose.CROUCHING);

    int next = state.ticksAccumulated + 1;
    int interval = Config.MEDITATION_INTERVAL_TICKS.get();
    if (next >= interval) {
      CultivationUtil.modifyQi(player, Config.MEDITATION_QI_GAIN.get().floatValue());
      next = 0;
    }
    state.ticksAccumulated = next;
  }

  /** Indique si le joueur est actuellement en méditation. */
  public static boolean isMeditating(Player player) {
    return STATE_BY_PLAYER.containsKey(player.getUUID());
  }

  /** Retire l'état de méditation du joueur (appelé à la déconnexion ou interruption). */
  public static void remove(Player player) {
    STATE_BY_PLAYER.remove(player.getUUID());
  }

  /** État mutable réutilisé pour éviter les allocations à chaque tick. */
  private static final class MutableMeditationState {
    int ticksAccumulated;

    MutableMeditationState() {
      this.ticksAccumulated = 0;
    }
  }
}
