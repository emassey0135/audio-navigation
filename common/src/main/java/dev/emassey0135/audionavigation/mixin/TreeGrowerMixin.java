package dev.emassey0135.audionavigation.mixin;

import java.util.Optional;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import dev.emassey0135.audionavigation.poi.Features;

@SuppressWarnings("UnstableApiUsage")
@Mixin(TreeGrower.class)
public class TreeGrowerMixin {
	@Shadow @Nullable
	private ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean flowersNearby) {
		throw new AbstractMethodError("Shadow");
	}

	@Shadow @Nullable
	private ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource random) {
		throw new AbstractMethodError("Shadow");
	}

	@Overwrite
	public boolean growTree(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random) {
		ResourceKey<ConfiguredFeature<?, ?>> resourceKey = this.getConfiguredMegaFeature(random);
		if (resourceKey != null) {
			Holder<ConfiguredFeature<?, ?>> holder = (Holder<ConfiguredFeature<?, ?>>)world.registryAccess()
				.lookupOrThrow(Registries.CONFIGURED_FEATURE)
				.get(resourceKey)
				.orElse(null);
			if (holder != null) {
				for (int i = 0; i >= -1; i--) {
					for (int j = 0; j >= -1; j--) {
						if (isTwoByTwoSapling(state, world, pos, i, j)) {
							ConfiguredFeature<?, ?> configuredFeature = holder.value();
							BlockState blockState = Blocks.AIR.defaultBlockState();
							world.setBlock(pos.offset(i, 0, j), blockState, 260);
							world.setBlock(pos.offset(i + 1, 0, j), blockState, 260);
							world.setBlock(pos.offset(i, 0, j + 1), blockState, 260);
							world.setBlock(pos.offset(i + 1, 0, j + 1), blockState, 260);
							if (configuredFeature.place(world, chunkGenerator, random, pos.offset(i, 0, j))) {
								Features.addFeatureToDatabase(resourceKey.location().getPath(), pos.offset(i, 0, j), world);
								return true;
							}

							world.setBlock(pos.offset(i, 0, j), state, 260);
							world.setBlock(pos.offset(i + 1, 0, j), state, 260);
							world.setBlock(pos.offset(i, 0, j + 1), state, 260);
							world.setBlock(pos.offset(i + 1, 0, j + 1), state, 260);
							return false;
						}
					}
				}
			}
		}

		ResourceKey<ConfiguredFeature<?, ?>> resourceKey2 = this.getConfiguredFeature(random, this.hasFlowers(world, pos));
		if (resourceKey2 == null) {
			return false;
		} else {
			Holder<ConfiguredFeature<?, ?>> holder2 = (Holder<ConfiguredFeature<?, ?>>)world.registryAccess()
				.lookupOrThrow(Registries.CONFIGURED_FEATURE)
				.get(resourceKey2)
				.orElse(null);
			if (holder2 == null) {
				return false;
			} else {
				ConfiguredFeature<?, ?> configuredFeature2 = holder2.value();
				BlockState blockState2 = world.getFluidState(pos).createLegacyBlock();
				world.setBlock(pos, blockState2, 260);
				if (configuredFeature2.place(world, chunkGenerator, random, pos)) {
					if (world.getBlockState(pos) == blockState2) {
						world.sendBlockUpdated(pos, state, blockState2, 2);
					}

					Features.addFeatureToDatabase(resourceKey2.location().getPath(), pos, world);
					return true;
				} else {
					world.setBlock(pos, state, 260);
					return false;
				}
			}
		}
	}

	@Shadow
	private static boolean isTwoByTwoSapling(BlockState state, BlockGetter world, BlockPos pos, int x, int z) {
		throw new AbstractMethodError("Shadow");
	}

	@Shadow
	private boolean hasFlowers(LevelAccessor world, BlockPos pos) {
		throw new AbstractMethodError("Shadow");
	}
}
