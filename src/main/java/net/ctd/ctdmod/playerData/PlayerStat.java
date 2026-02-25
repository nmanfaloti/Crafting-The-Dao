package net.ctd.ctdmod.playerData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

public class PlayerStat {
	private static final String ROOT_KEY = "ctd_player_stats";
	private static final String INITIALIZED_KEY = "initialized";
	private static final String LOGIN_COUNT_KEY = "login_count";
	private static final String CULTIVATION_LEVEL_KEY = "nvCultivation";
    private static final String QI_LEVEL_KEY = "nvQi";
    private static final String POWER_METRIC = "power_metric";

	private PlayerStat() {
	}

    // Helper method to get or create the stats tag for a player
	private static CompoundTag getOrCreateStatsTag(Player player) {
		CompoundTag persistentData = player.getPersistentData();
		if (!persistentData.contains(ROOT_KEY, Tag.TAG_COMPOUND)) {
			persistentData.put(ROOT_KEY, new CompoundTag());
		}
		return persistentData.getCompound(ROOT_KEY);
	}

	public static boolean initializeDefaultsIfNeeded(Player player) {
		CompoundTag statsTag = getOrCreateStatsTag(player);
		if (statsTag.getBoolean(INITIALIZED_KEY)) {
			return false;
		}
		forceInitializeDefaults(player);
		return true;
	}

    public static void forceInitializeDefaults(Player player) {
        CompoundTag statsTag = getOrCreateStatsTag(player);
        statsTag.putBoolean(INITIALIZED_KEY, true);
        statsTag.putInt(LOGIN_COUNT_KEY, 0);
        statsTag.putInt(CULTIVATION_LEVEL_KEY, 11);
        statsTag.putInt(QI_LEVEL_KEY, 11);
        statsTag.putInt(POWER_METRIC, 10);
    }

    // Example stat: login count
	public static int incrementLoginCount(Player player) {
		CompoundTag statsTag = getOrCreateStatsTag(player);
		int nextValue = statsTag.getInt(LOGIN_COUNT_KEY) + 1;
		statsTag.putInt(LOGIN_COUNT_KEY, nextValue);
		return nextValue;
	}

	public static int getLoginCount(Player player) {
		return getOrCreateStatsTag(player).getInt(LOGIN_COUNT_KEY);
	}

	public static void setIntStat(Player player, String key, int value) {
		getOrCreateStatsTag(player).putInt(key, value);
	}

	public static int getIntStat(Player player, String key) {
		return getOrCreateStatsTag(player).getInt(key);
	}

	public static void copyAllStats(Player source, Player target) {
		CompoundTag sourceData = source.getPersistentData();
		if (sourceData.contains(ROOT_KEY, Tag.TAG_COMPOUND)) {
			target.getPersistentData().put(ROOT_KEY, sourceData.getCompound(ROOT_KEY).copy());
		}
	}


    public static String viewStats(Player player) {
        CompoundTag statsTag = getOrCreateStatsTag(player);
        StringBuilder sb = new StringBuilder("Player Stats:\n");
        sb.append("Login Count: ").append(statsTag.getInt(LOGIN_COUNT_KEY)).append("\n");
        sb.append("Cultivation Level: ").append(statsTag.getInt(CULTIVATION_LEVEL_KEY)).append("\n");
        sb.append("Qi Level: ").append(statsTag.getInt(QI_LEVEL_KEY)).append("\n");
        sb.append("Power Metric: ").append(statsTag.getInt(POWER_METRIC)).append("\n");
        return sb.toString();
    }



    public static String getCultivationLevelKey() {return CULTIVATION_LEVEL_KEY;}

    public static String getQiLevelKey() {return QI_LEVEL_KEY;}

    public static String getPowerMetricKey() {return POWER_METRIC;}

    public static int increaseQiLevel(Player player, int amount) {
        CompoundTag statsTag = getOrCreateStatsTag(player);
        int newValue = statsTag.getInt(QI_LEVEL_KEY) + amount;
        statsTag.putInt(QI_LEVEL_KEY, newValue);
        return newValue;
    }

    public static void decreaseQiLevel(Player player, int amount) {
        CompoundTag statsTag = getOrCreateStatsTag(player);
        int newValue = Math.max(0, statsTag.getInt(QI_LEVEL_KEY) - amount);
        statsTag.putInt(QI_LEVEL_KEY, newValue);
    }

    public static int getQiLevel(Player player) {
        return getOrCreateStatsTag(player).getInt(QI_LEVEL_KEY);
    }
}
