package net.ctd.ctdmod;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common mod configuration. Options are defined via {@link ModConfigSpec} and loaded from the
 * mod's config file (e.g. in the config folder). The spec is registered in {@link CTDMod}.
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    // -------------------------------------------------------------------------
    // Méditation (paramètres configurables)
    // -------------------------------------------------------------------------

    /** Qi accordé à chaque intervalle de méditation. */
    public static final ModConfigSpec.DoubleValue MEDITATION_QI_GAIN = BUILDER
            .comment("Qi granted per meditation interval (default: 5.0)")
            .defineInRange("meditationQiGain", 5.0, 0.0, Double.MAX_VALUE);

    /** Nombre de ticks entre deux gains de Qi en méditation (80 = 4 s à 20 TPS). */
    public static final ModConfigSpec.IntValue MEDITATION_INTERVAL_TICKS = BUILDER
            .comment("Ticks between each Qi gain while meditating (80 = 4 seconds at 20 TPS)")
            .defineInRange("meditationIntervalTicks", 80, 1, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
