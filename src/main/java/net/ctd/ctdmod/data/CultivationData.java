package net.ctd.ctdmod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Données persistantes de cultivation d'un joueur (Qi actuel, Qi max et niveau).
 * <p>
 * Sérialisées via un {@link Codec} pour être stockées dans un {@code AttachmentType}.
 */
public record CultivationData(float qi, float maxQi, int level) {

    /**
     * Codec de sérialisation pour l'API d'attachements NeoForge.
     */
    public static final Codec<CultivationData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.fieldOf("qi").forGetter(CultivationData::qi),
            Codec.FLOAT.fieldOf("max_qi").forGetter(CultivationData::maxQi),
            Codec.INT.fieldOf("level").forGetter(CultivationData::level)
        ).apply(instance, CultivationData::new)
    );

    /**
     * Valeur par défaut utilisée lors de la création d'un joueur.
     */
    public static final CultivationData DEFAULT = new CultivationData(0.0F, 100.0F, 1);
}