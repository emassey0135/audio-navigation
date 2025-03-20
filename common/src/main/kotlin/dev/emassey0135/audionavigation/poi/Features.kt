package dev.emassey0135.audionavigation.poi

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import dev.emassey0135.audionavigation.AudioNavigation

object Features {
  val features = setOf(
    "acacia",
    "amethyst_geode",
    "azalea_tree",
    "bamboo_no_podzol",
    "bamboo_some_podzol",
    "basalt_blobs",
    "basalt_pillar",
    "birch",
    "blackstone_blobs",
    "blue_ice",
    "bonus_chest",
    "cave_vine",
    "cave_vine_in_moss",
    "cherry",
    "chorus_plant",
    "clay_pool_with_dripleaves",
    "clay_with_dripleaves",
    "crimson_forest_vegetation",
    "crimson_fungus",
    "dark_oak",
    "delta",
    "desert_well",
    "disk_clay",
    "disk_grass",
    "disk_gravel",
    "disk_sand",
    "dripleaf",
    "dripstone_cluster",
    "end_gateway_delayed",
    "end_gateway_return",
    "end_island",
    "end_platform",
    "end_spike",
    "fancy_oak",
    "flower_cherry",
    "flower_default",
    "flower_flower_forest",
    "flower_meadow",
    "flower_pale_garden",
    "flower_plain",
    "flower_swamp",
    "forest_flowers",
    "forest_rock",
    "fossil_coal",
    "fossil_diamonds",
    "glow_lichen",
    "glowstone_extra",
    "huge_brown_mushroom",
    "huge_red_mushroom",
    "ice_patch",
    "ice_spike",
    "iceberg_blue",
    "iceberg_packed",
    "jungle_bush",
    "jungle_tree",
    "jungle_tree_no_vine",
    "kelp",
    "lake_lava",
    "large_basalt_columns",
    "large_dripstone",
    "mangrove",
    "mega_jungle_tree",
    "mega_pine",
    "mega_spruce",
    "monster_room",
    "moss_patch",
    "moss_patch_ceiling",
    "moss_vegetation",
    "nether_sprouts",
    "oak",
    "ore_ancient_debris_large",
    "ore_ancient_debris_small",
    "ore_andesite",
    "ore_blackstone",
    "ore_clay",
    "ore_coal",
    "ore_coal_buried",
    "ore_copper_large",
    "ore_copper_small",
    "ore_diamond_buried",
    "ore_diamond_large",
    "ore_diamond_medium",
    "ore_diamond_small",
    "ore_diorite",
    "ore_dirt",
    "ore_emerald",
    "ore_gold",
    "ore_gold_buried",
    "ore_granite",
    "ore_gravel",
    "ore_gravel_nether",
    "ore_infested",
    "ore_iron",
    "ore_iron_small",
    "ore_lapis",
    "ore_lapis_buried",
    "ore_magma",
    "ore_nether_gold",
    "ore_quartz",
    "ore_redstone",
    "ore_soul_sand",
    "ore_tuff",
    "pale_forest_flowers",
    "pale_moss_patch",
    "pale_moss_vegetation",
    "pale_oak",
    "pale_oak_creaking",
    "patch_berry_bush",
    "patch_brown_mushroom",
    "patch_cactus",
    "patch_crimson_roots",
    "patch_dead_bush",
    "patch_fire",
    "patch_grass",
    "patch_grass_jungle",
    "patch_large_fern",
    "patch_melon",
    "patch_pumpkin",
    "patch_red_mushroom",
    "patch_soul_fire",
    "patch_sugar_cane",
    "patch_sunflower",
    "patch_taiga_grass",
    "patch_tall_grass",
    "patch_waterlily",
    "pile_hay",
    "pile_ice",
    "pile_melon",
    "pile_pumpkin",
    "pile_snow",
    "pine",
    "pointed_dripstone",
    "rooted_azalea_tree",
    "sculk_patch_ancient_city",
    "sculk_patch_deep_dark",
    "sculk_vein",
    "sea_pickle",
    "seagrass_mid",
    "seagrass_short",
    "seagrass_slightly_less_short",
    "seagrass_tall",
    "single_piece_of_grass",
    "small_basalt_columns",
    "spore_blossom",
    "spring_lava_frozen",
    "spring_lava_nether",
    "spring_lava_overworld",
    "spring_nether_closed",
    "spring_nether_open",
    "spring_water",
    "spruce",
    "super_birch",
    "swamp_oak",
    "tall_mangrove",
    "twisting_vines",
    "underwater_magma",
    "vines",
    "void_start_platform",
    "warm_ocean_vegetation",
    "warped_forest_vegetation",
    "warped_fungus",
    "weeping_vines"
  )
  private val redundantFeatures = setOf(
    "bamboo_vegetation",
    "birch_tall",
    "dark_forest_vegetation",
    "freeze_top_layer",
    "lush_caves_clay",
    "mangrove_vegetation",
    "meadow_trees",
    "mushroom_island_vegetation",
    "pale_garden_vegetation",
    "trees_birch_and_oak",
    "trees_flower_forest",
    "trees_grove",
    "trees_jungle",
    "trees_old_growth_pine_taiga",
    "trees_old_growth_spruce_taiga",
    "trees_plains",
    "trees_savanna",
    "trees_sparse_jungle",
    "trees_taiga",
    "trees_water",
    "trees_windswept_hills",
  )
  val duplicateFeatures = mapOf(
    "birch_bees_0002" to "birch",
    "birch_bees_002" to "birch",
    "birch_bees_005" to "birch",
    "cherry_bees_005" to "cherry",
    "crimson_forest_vegetation_bonemeal" to "crimson_forest_vegetation",
    "crimson_fungus_planted" to "crimson_fungus",
    "fancy_oak_bees" to "fancy_oak",
    "fancy_oak_bees_0002" to "fancy_oak",
    "fancy_oak_bees_002" to "fancy_oak",
    "fancy_oak_bees_005" to "fancy_oak",
    "moss_patch_bonemeal" to "moss_patch",
    "nether_sprouts_bonemeal" to "nether_sprouts",
    "oak_bees_0002" to "oak",
    "oak_bees_002" to "oak",
    "oak_bees_005" to "oak",
    "pale_moss_patch_bonemeal" to "pale_moss_patch",
    "pale_oak_bonemeal" to "pale_oak",
    "super_birch_bees" to "super_birch",
    "super_birch_bees_0002" to "super_birch",
    "twisting_vines_bonemeal" to "twisting_vines",
    "warped_forest_vegetation_bonemeal" to "warped_forest_vegetation",
    "warped_fungus_planted" to "warped_fungus",
  )
  val defaultIncludedFeatures = setOf(
    "acacia",
    "amethyst_geode",
    "azalea_tree",
    "bamboo_no_podzol",
    "bamboo_some_podzol",
    "birch",
    "bonus_chest",
    "cherry",
    "chorus_plant",
    "clay_pool_with_dripleaves",
    "clay_with_dripleaves",
    "crimson_forest_vegetation",
    "crimson_fungus",
    "dark_oak",
    "delta",
    "desert_well",
    "dripleaf",
    "dripstone_cluster",
    "end_gateway_delayed",
    "end_gateway_return",
    "end_island",
    "end_platform",
    "end_spike",
    "fancy_oak",
    "forest_rock",
    "fossil_coal",
    "fossil_diamonds",
    "glowstone_extra",
    "huge_brown_mushroom",
    "huge_red_mushroom",
    "ice_spike",
    "iceberg_blue",
    "iceberg_packed",
    "jungle_bush",
    "jungle_tree",
    "jungle_tree_no_vine",
    "lake_lava",
    "large_basalt_columns",
    "large_dripstone",
    "mangrove",
    "mega_jungle_tree",
    "mega_pine",
    "mega_spruce",
    "monster_room",
    "nether_sprouts",
    "oak",
    "ore_ancient_debris_large",
    "ore_ancient_debris_small",
    "ore_blackstone",
    "ore_coal",
    "ore_coal_buried",
    "ore_copper_large",
    "ore_copper_small",
    "ore_diamond_buried",
    "ore_diamond_large",
    "ore_diamond_medium",
    "ore_diamond_small",
    "ore_emerald",
    "ore_gold",
    "ore_gold_buried",
    "ore_infested",
    "ore_iron",
    "ore_iron_small",
    "ore_lapis",
    "ore_lapis_buried",
    "ore_magma",
    "ore_nether_gold",
    "ore_quartz",
    "ore_redstone",
    "ore_soul_sand",
    "pale_oak",
    "pale_oak_creaking",
    "patch_berry_bush",
    "patch_brown_mushroom",
    "patch_cactus",
    "patch_crimson_roots",
    "patch_fire",
    "patch_melon",
    "patch_pumpkin",
    "patch_red_mushroom",
    "patch_soul_fire",
    "patch_sugar_cane",
    "pile_hay",
    "pile_ice",
    "pile_melon",
    "pile_pumpkin",
    "pile_snow",
    "pine",
    "pointed_dripstone",
    "rooted_azalea_tree",
    "sculk_patch_ancient_city",
    "sculk_patch_deep_dark",
    "sculk_vein",
    "sea_pickle",
    "small_basalt_columns",
    "spore_blossom",
    "spring_lava_frozen",
    "spring_lava_nether",
    "spring_lava_overworld",
    "spring_nether_closed",
    "spring_nether_open",
    "spring_water",
    "spruce",
    "super_birch",
    "swamp_oak",
    "tall_mangrove",
    "underwater_magma",
    "void_start_platform",
    "warped_forest_vegetation",
    "warped_fungus",
  )
  @JvmStatic fun addFeatureToDatabase(identifier: String, pos: BlockPos, world: ServerLevel) {
    if (identifier !in redundantFeatures) {
      val identifier = if (duplicateFeatures.containsKey(identifier)) duplicateFeatures.get(identifier)!! else identifier
      Poi(PoiType.FEATURE, identifier, pos).addToDatabase(world)
    }
  }
}
