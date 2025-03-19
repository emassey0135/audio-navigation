package dev.emassey0135.audionavigation.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import dev.emassey0135.audionavigation.poi.Features;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/* This code has been derived from Worldgen Devtools (https://github.com/jacobsjo/world.gen-devtools).
   Copyright (c) 2023
   See LICENSE.worldgen-devtools for more information.
*/
@SuppressWarnings("UnstableApiUsage")
@Mixin(PlacedFeature.class)
public class PlacedFeatureMixin {

    @Shadow @Final private Holder<ConfiguredFeature<?, ?>> feature;

    @Shadow @Final private List<PlacementModifier> placement;

    /**
     * @author jacobsjo
     * @reason couldn't find a better way, the lambda is static
     */
    @Overwrite
    private boolean placeWithContext(PlacementContext context, RandomSource random, BlockPos pos){
        Stream<BlockPos> stream = Stream.of(pos);

        for(PlacementModifier placementModifier : this.placement) {
            stream = stream.flatMap(blockPos -> placementModifier.getPositions(context, random, blockPos));
        }

        Optional<ResourceKey<ConfiguredFeature<?, ?>>> key = this.feature.unwrapKey();
        ConfiguredFeature<?, ?> configuredFeature = this.feature.value();

        MutableBoolean mutableBoolean = new MutableBoolean();
        stream.forEach(blockPos -> {
                if (configuredFeature.place(context.getLevel(), context.generator(), random, blockPos)) {
                    mutableBoolean.setTrue();

                    if (key.isPresent()) {
                        Features.addFeatureToDatabase(key.get().location().getPath(), blockPos, context.getLevel().getLevel());
                    }
            }
        });
        return mutableBoolean.isTrue();
    }
}
