package dev.polaris_light.allvoid;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import dev.polaris_light.allvoid.helpers.PaleBlossomCreakingHeart;
import dev.polaris_light.allvoid.helpers.RenewableCoral;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(AllVoid.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RuleGameTests {
    @GameTest(template = "spawn_platform", timeoutTicks = 100)
    public static void renewableCoralGrowsAndExpandedFans(GameTestHelper helper) {
        AllVoidConfig.renewableCoral = AllVoidConfig.RenewableCoralMode.TRUE;
        AllVoidConfig.allowFeaturesInNonVoidWorlds = true;

        BlockPos origin = helper.absolutePos(new BlockPos(0, 4, 0));
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-6, -2, -6), origin.offset(6, 7, 6))) {
            helper.getLevel().setBlock(pos, Blocks.WATER.defaultBlockState(), 2);
        }
        helper.getLevel().setBlock(origin.below(), Blocks.STONE.defaultBlockState(), 3);

        BlockState coral = Blocks.TUBE_CORAL.defaultBlockState()
                .setValue(BaseCoralPlantTypeBlock.WATERLOGGED, true);
        helper.getLevel().setBlock(origin, coral, 3);
        if (!(coral.getBlock() instanceof BonemealableBlock bonemeal)) {
            helper.fail("Coral plant was not bonemealable");
            return;
        }
        if (!bonemeal.isValidBonemealTarget(helper.getLevel(), origin, coral)) {
            helper.fail("Coral plant should be a valid bonemeal target");
            return;
        }

        RenewableCoral.grow(helper.getLevel(), RandomSource.create(1L), origin, coral);
        boolean grew = false;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-8, -4, -8), origin.offset(8, 8, 8))) {
            if (helper.getLevel().getBlockState(pos).is(BlockTags.CORAL_BLOCKS)) {
                grew = true;
                break;
            }
        }
        if (!grew) {
            helper.fail("Renewable coral did not grow any coral blocks");
            return;
        }

        BlockPos fanPos = origin.offset(3, 0, 0);
        BlockState fan = Blocks.TUBE_CORAL_FAN.defaultBlockState()
                .setValue(BaseCoralPlantTypeBlock.WATERLOGGED, true);
        helper.getLevel().setBlock(fanPos, fan, 3);
        BonemealableBlock fanBonemeal = (BonemealableBlock) fan.getBlock();
        if (fanBonemeal.isValidBonemealTarget(helper.getLevel(), fanPos, fan)) {
            helper.fail("Coral fans should be disabled in TRUE mode");
            return;
        }
        AllVoidConfig.renewableCoral = AllVoidConfig.RenewableCoralMode.EXPANDED;
        if (!fanBonemeal.isValidBonemealTarget(helper.getLevel(), fanPos, fan)) {
            helper.fail("Coral fans should be enabled in EXPANDED mode");
            return;
        }

        helper.succeed();
    }

    @GameTest(template = "spawn_platform", timeoutTicks = 100)
    public static void renewableDeepslateHandlesBothFluidDirections(GameTestHelper helper) {
        AllVoidConfig.renewableDeepslate = true;
        AllVoidConfig.allowFeaturesInNonVoidWorlds = true;

        BlockPos testAnchor = helper.absolutePos(BlockPos.ZERO);
        BlockPos lateral = new BlockPos(testAnchor.getX() + 2, -20, testAnchor.getZ() + 2);
        Set<BlockPos> fluidPlaceEvents = ConcurrentHashMap.newKeySet();
        Consumer<BlockEvent.FluidPlaceBlockEvent> listener =
                event -> fluidPlaceEvents.add(event.getPos().immutable());
        NeoForge.EVENT_BUS.addListener(listener);

        helper.getLevel().setBlock(
                lateral,
                Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, 4),
                0
        );
        helper.getLevel().setBlock(lateral.east(), Blocks.WATER.defaultBlockState(), 0);
        if (!FluidInteractionRegistry.canInteract(helper.getLevel(), lateral)) {
            helper.fail("Flowing lava did not interact with adjacent water");
            return;
        }
        if (!helper.getLevel().getBlockState(lateral).is(Blocks.COBBLED_DEEPSLATE)) {
            helper.fail("Flowing lava did not become cobbled deepslate");
            return;
        }
        if (!fluidPlaceEvents.contains(lateral)) {
            helper.fail("Cobbled deepslate conversion did not fire FluidPlaceBlockEvent");
            return;
        }

        BlockPos downward = new BlockPos(lateral.getX() + 2, -23, lateral.getZ() + 2);
        helper.getLevel().setBlock(downward, Blocks.WATER.defaultBlockState(), 0);
        helper.getLevel().setBlock(downward.above(), Blocks.LAVA.defaultBlockState(), 0);
        helper.getLevel().scheduleTick(downward.above(), Fluids.LAVA, 1);
        helper.succeedWhen(() -> {
            if (!helper.getLevel().getBlockState(downward).is(Blocks.DEEPSLATE)) {
                helper.fail("Lava flowing into water did not become deepslate");
            }
            if (!fluidPlaceEvents.contains(downward)) {
                helper.fail("Deepslate conversion did not fire FluidPlaceBlockEvent");
            }
            NeoForge.EVENT_BUS.unregister(listener);
        });
    }

    @GameTest(template = "spawn_platform", timeoutTicks = 100)
    public static void paleBlossomCreatesCreakingHeart(GameTestHelper helper) {
        if (!ModList.get().isLoaded("vanillabackport")) {
            helper.succeed();
            return;
        }

        AllVoidConfig.paleBlossomCreakingHeart = true;
        AllVoidConfig.allowFeaturesInNonVoidWorlds = true;

        Registry<Block> blocks = helper.getLevel().registryAccess().registryOrThrow(Registries.BLOCK);
        Block sapling = blocks.getOptional(ResourceLocation.withDefaultNamespace("pale_oak_sapling"))
                .orElse(null);
        Block log = blocks.getOptional(ResourceLocation.withDefaultNamespace("pale_oak_log"))
                .orElse(null);
        Block eyeblossom = blocks.getOptional(ResourceLocation.withDefaultNamespace("open_eyeblossom"))
                .orElse(null);
        Block heart = blocks.getOptional(ResourceLocation.withDefaultNamespace("creaking_heart"))
                .orElse(null);
        if (sapling == null || log == null || eyeblossom == null || heart == null) {
            helper.fail("Vanilla Backport pale garden blocks were not registered");
            return;
        }

        BlockPos anchor = helper.absolutePos(BlockPos.ZERO);
        BlockPos saplingPos = new BlockPos(anchor.getX(), -10, anchor.getZ());
        setBiome(helper, saplingPos, ResourceLocation.withDefaultNamespace("pale_garden"));
        String actualBiome = helper.getLevel().getBiome(saplingPos).unwrapKey()
                .map(key -> key.location().toString())
                .orElse("<unregistered>");
        if (!actualBiome.equals("minecraft:pale_garden")) {
            helper.fail("Expected pale garden biome, got " + actualBiome);
            return;
        }

        BlockPos logPos = saplingPos.above();
        BlockPos secondLogPos = logPos.east();
        helper.getLevel().setBlock(logPos, log.defaultBlockState(), 3);
        helper.getLevel().setBlock(secondLogPos, log.defaultBlockState(), 3);
        helper.getLevel().setBlock(saplingPos.east(2), eyeblossom.defaultBlockState(), 3);

        for (int seed = 0; seed < 100; seed++) {
            helper.getLevel().setBlock(logPos, log.defaultBlockState(), 3);
            helper.getLevel().setBlock(secondLogPos, log.defaultBlockState(), 3);
            PaleBlossomCreakingHeart.afterTreeGrowth(
                    helper.getLevel(),
                    saplingPos,
                    sapling.defaultBlockState(),
                    RandomSource.create(seed)
            );
            if (helper.getLevel().getBlockState(logPos).is(heart)
                    || helper.getLevel().getBlockState(secondLogPos).is(heart)) {
                helper.succeed();
                return;
            }
        }

        helper.fail("Pale oak logs did not produce a creaking heart; biome=" + actualBiome
                + ", eyeblossom=" + helper.getLevel().getBlockState(saplingPos.east(2))
                + ", log=" + helper.getLevel().getBlockState(logPos));
    }

    @GameTest(template = "spawn_platform", timeoutTicks = 100)
    public static void paleBlossomIsInertWithoutVanillaBackport(GameTestHelper helper) {
        if (ModList.get().isLoaded("vanillabackport")) {
            helper.succeed();
            return;
        }

        AllVoidConfig.paleBlossomCreakingHeart = true;
        AllVoidConfig.allowFeaturesInNonVoidWorlds = true;

        BlockPos anchor = helper.absolutePos(BlockPos.ZERO);
        BlockPos saplingPos = new BlockPos(anchor.getX(), -10, anchor.getZ());
        BlockPos logPos = saplingPos.above();
        helper.getLevel().setBlock(logPos, Blocks.OAK_LOG.defaultBlockState(), 3);

        for (int seed = 0; seed < 10; seed++) {
            PaleBlossomCreakingHeart.afterTreeGrowth(
                    helper.getLevel(),
                    saplingPos,
                    Blocks.OAK_SAPLING.defaultBlockState(),
                    RandomSource.create(seed)
            );
        }

        if (!helper.getLevel().getBlockState(logPos).is(Blocks.OAK_LOG)) {
            helper.fail("Pale blossom rule modified blocks without Vanilla Backport");
            return;
        }
        helper.succeed();
    }

    @SuppressWarnings("unchecked")
    private static void setBiome(GameTestHelper helper, BlockPos pos, ResourceLocation biomeId) {
        Holder<Biome> biome = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.BIOME)
                .getHolder(biomeId)
                .orElseThrow();
        LevelChunk chunk = helper.getLevel().getChunkAt(pos);
        LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(pos.getY()));
        PalettedContainer<Holder<Biome>> biomes =
                (PalettedContainer<Holder<Biome>>) section.getBiomes();
        biomes.set(
                (pos.getX() & 15) >> 2,
                (pos.getY() & 15) >> 2,
                (pos.getZ() & 15) >> 2,
                biome
        );
        chunk.setUnsaved(true);
    }

    private RuleGameTests() {
    }
}
