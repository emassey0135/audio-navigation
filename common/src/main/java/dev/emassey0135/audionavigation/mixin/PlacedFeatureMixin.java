package dev.emassey0135.audionavigation.mixin;

import dev.emassey0135.audionavigation.Poi;
import dev.emassey0135.audionavigation.PoiType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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

    @Shadow @Final private RegistryEntry<ConfiguredFeature<?, ?>> feature;

    @Shadow @Final private List<PlacementModifier> placementModifiers;

    /**
     * @author jacobsjo
     * @reason couldn't find a better way, the lambda is static
     */
    @Overwrite
    private boolean generate(FeaturePlacementContext context, Random random, BlockPos pos){
        Stream<BlockPos> stream = Stream.of(pos);

        for(PlacementModifier placementModifier : this.placementModifiers) {
            stream = stream.flatMap(blockPos -> placementModifier.getPositions(context, random, blockPos));
        }

        Optional<RegistryKey<ConfiguredFeature<?, ?>>> key = this.feature.getKey();
        ConfiguredFeature<?, ?> configuredFeature = this.feature.value();

        MutableBoolean mutableBoolean = new MutableBoolean();
        stream.forEach(blockPos -> {
                if (configuredFeature.generate(context.getWorld(), context.getChunkGenerator(), random, blockPos)) {
                    mutableBoolean.setTrue();

                    if (key.isPresent()) {
                        new Poi(PoiType.FEATURE, key.get().getValue().getPath(), blockPos).addToDatabase(context.getWorld().toServerWorld());
                    }
            }
        });
        return mutableBoolean.isTrue();
    }
}
