package net.ctd.ctdmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.item.context.BlockPlaceContext;

/**
 * Bloc contrôleur d’un multiblock.
 * <p>
 * Responsabilités principales :
 * <ul>
 *   <li>Fournir une propriété d’état {@code ACTIVE} reflétant l’état de formation du multiblock.</li>
 *   <li>Servir de point d’ancrage à la BlockEntity de contrôle (validation de structure, logique machine).</li>
 *   <li>Assurer une valeur d’état cohérente à la pose et lors des mises à jour de structure.</li>
 * </ul>
 *
 * À ce stade, cette classe gère uniquement l’état binaire de formation.
 * La logique de validation et la BlockEntity associée seront introduites ultérieurement.
 */
public class CTDMultiBlockControllerBlock extends CTDBaseBlock {

    /**
     * Propriété d’état exposant l’activité du multiblock.
     * <p>
     * {@code true} : la structure associée au contrôleur est considérée comme formée/valide.<br>
     * {@code false} : la structure est incomplète ou invalide.
     *
     * <p>On réutilise la propriété standard {@link BlockStateProperties#LIT}
     * afin de bénéficier d’un support naturel côté modèles/variants.
     */
    public static final BooleanProperty ACTIVE = BlockStateProperties.LIT;

    /**
     * Construit un nouveau bloc contrôleur de multiblock.
     *
     * @param properties configuration bas niveau du bloc (dureté, son, outil requis, etc.)
     */
    public CTDMultiBlockControllerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        // Initialisation de l’état par défaut : multiblock non formé (ACTIVE = false).
        // Cet état sera ultérieurement synchronisé avec la BlockEntity de contrôle.
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, Boolean.FALSE));
    }

    /**
     * Enregistre les propriétés d’état supportées par ce bloc.
     * <p>
     * Cette méthode doit référencer toutes les propriétés manipulées sur le {@link BlockState}
     * (ici uniquement {@link #ACTIVE}).
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    /**
     * Définit l’état initial du bloc au moment de sa pose dans le monde.
     * <p>
     * Par défaut, un contrôleur nouvellement placé est considéré comme inactif
     * ({@code ACTIVE = false}) tant qu’aucune validation de structure n’a été effectuée.
     *
     * @param context contexte de pose (joueur, face, position, etc.)
     * @return l’état initial appliqué au bloc posé
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(ACTIVE, Boolean.FALSE);
    }
}
