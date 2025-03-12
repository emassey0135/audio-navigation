package dev.emassey0135.audionavigation.mixin;

import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import dev.emassey0135.audionavigation.Poi;
import dev.emassey0135.audionavigation.PoiType;

@SuppressWarnings("UnstableApiUsage")
@Mixin(SaplingGenerator.class)
public class SaplingGeneratorMixin {
	@Shadow @Nullable
	private RegistryKey<ConfiguredFeature<?, ?>> getSmallTreeFeature(Random random, boolean flowersNearby) {
		throw new AbstractMethodError("Shadow");
	}

	@Shadow @Nullable
	private RegistryKey<ConfiguredFeature<?, ?>> getMegaTreeFeature(Random random) {
		throw new AbstractMethodError("Shadow");
	}

	@Overwrite
	public boolean generate(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
		RegistryKey<ConfiguredFeature<?, ?>> registryKey = this.getMegaTreeFeature(random);
		if (registryKey != null) {
			RegistryEntry<ConfiguredFeature<?, ?>> registryEntry = (RegistryEntry<ConfiguredFeature<?, ?>>)world.getRegistryManager()
				.getOrThrow(RegistryKeys.CONFIGURED_FEATURE)
				.getOptional(registryKey)
				.orElse(null);
			if (registryEntry != null) {
				for (int i = 0; i >= -1; i--) {
					for (int j = 0; j >= -1; j--) {
						if (canGenerateLargeTree(state, world, pos, i, j)) {
							ConfiguredFeature<?, ?> configuredFeature = registryEntry.value();
							BlockState blockState = Blocks.AIR.getDefaultState();
							world.setBlockState(pos.add(i, 0, j), blockState, 4);
							world.setBlockState(pos.add(i + 1, 0, j), blockState, 4);
							world.setBlockState(pos.add(i, 0, j + 1), blockState, 4);
							world.setBlockState(pos.add(i + 1, 0, j + 1), blockState, 4);
							if (configuredFeature.generate(world, chunkGenerator, random, pos.add(i, 0, j))) {
								new Poi(PoiType.FEATURE, registryKey.getValue().getPath(), pos.add(i, 0, j)).addToDatabase(world);
								return true;
							}

							world.setBlockState(pos.add(i, 0, j), state, 4);
							world.setBlockState(pos.add(i + 1, 0, j), state, 4);
							world.setBlockState(pos.add(i, 0, j + 1), state, 4);
							world.setBlockState(pos.add(i + 1, 0, j + 1), state, 4);
							return false;
						}
					}
				}
			}
		}

		RegistryKey<ConfiguredFeature<?, ?>> registryKey2 = this.getSmallTreeFeature(random, this.areFlowersNearby(world, pos));
		if (registryKey2 == null) {
			return false;
		} else {
			RegistryEntry<ConfiguredFeature<?, ?>> registryEntry2 = (RegistryEntry<ConfiguredFeature<?, ?>>)world.getRegistryManager()
				.getOrThrow(RegistryKeys.CONFIGURED_FEATURE)
				.getOptional(registryKey2)
				.orElse(null);
			if (registryEntry2 == null) {
				return false;
			} else {
				ConfiguredFeature<?, ?> configuredFeature2 = registryEntry2.value();
				BlockState blockState2 = world.getFluidState(pos).getBlockState();
				world.setBlockState(pos, blockState2, 4);
				if (configuredFeature2.generate(world, chunkGenerator, random, pos)) {
					if (world.getBlockState(pos) == blockState2) {
						world.updateListeners(pos, state, blockState2, 2);
					}

					new Poi(PoiType.FEATURE, registryKey2.getValue().getPath(), pos).addToDatabase(world);
					return true;
				} else {
					world.setBlockState(pos, state, 4);
					return false;
				}
			}
		}
	}

	@Shadow
	private static boolean canGenerateLargeTree(BlockState state, BlockView world, BlockPos pos, int x, int z) {
		throw new AbstractMethodError("Shadow");
	}

	@Shadow
	private boolean areFlowersNearby(WorldAccess world, BlockPos pos) {
		throw new AbstractMethodError("Shadow");
	}
}
