package net.ctd.ctdmod.data;

import net.ctd.ctdmod.CTDMod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Déclarations des {@link AttachmentType} utilisés par le mod.
 * <p>
 * Contient notamment les données de cultivation du joueur.
 */
public final class CTDAttachments {

    /**
     * Registre différé pour les attachements NeoForge.
     */
    public static final DeferredRegister<AttachmentType<?>> DR =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CTDMod.MODID);

    /**
     * Données de cultivation d'un joueur (Qi, Qi max, niveau).
     * <p>
     * - Sérialisées via {@link CultivationData#CODEC}.<br>
     * - Copiées à la mort du joueur via {@link AttachmentType.Builder#copyOnDeath()}.
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CultivationData>> CULTIVATION =
        DR.register("cultivation", () ->
            AttachmentType.builder(() -> CultivationData.DEFAULT)
                .serialize(CultivationData.CODEC)
                .copyOnDeath()
                .build()
        );

    private CTDAttachments() {
        // Classe utilitaire : pas d'instanciation.
    }
}

