package dev.polaris_light.allvoid.helpers;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

/**
 * Impl Carpet Sky Additions' {@code paleBlossomCreakingHeart} rule when Vanilla Backport presents.
 */
public final class PaleBlossomCreakingHeart {
    private static final ResourceLocation PALE_OAK_SAPLING =
            ResourceLocation.withDefaultNamespace("pale_oak_sapling");
    private static final ResourceLocation PALE_OAK_LOG =
            ResourceLocation.withDefaultNamespace("pale_oak_log");
    private static final ResourceLocation OPEN_EYEBLOSSOM =
            ResourceLocation.withDefaultNamespace("open_eyeblossom");
    private static final ResourceLocation CREAKING_HEART =
            ResourceLocation.withDefaultNamespace("creaking_heart");
    private static final ResourceLocation PALE_GARDEN =
            ResourceLocation.withDefaultNamespace("pale_garden");

    public static void afterTreeGrowth(
            ServerLevel level,
            BlockPos saplingPos,
            BlockState saplingState,
            RandomSource random
    ) {
        if (!AllVoidConfig.paleBlossomCreakingHeart
                || !ModList.get().isLoaded("vanillabackport")
                || !AllVoidConfig.featuresEnabled(level)
                || block(PALE_OAK_SAPLING).isEmpty()
                || !saplingState.is(block(PALE_OAK_SAPLING).orElseThrow())
                || !isPaleGarden(level, saplingPos)
                || !hasOpenEyeblossomNearby(level, saplingPos)
                || random.nextInt(10) != 0) {
            return;
        }

        Optional<Block> paleOakLog = block(PALE_OAK_LOG);
        Optional<Block> creakingHeart = block(CREAKING_HEART);
        if (paleOakLog.isEmpty() || creakingHeart.isEmpty()) {
            return;
        }
        List<BlockPos> eligibleLogs = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos logPos = saplingPos.offset(x, y, z);
                    if (level.getBlockState(logPos).is(paleOakLog.get())) {
                        eligibleLogs.add(logPos.immutable());
                    }
                }
            }
        }
        if (eligibleLogs.isEmpty()) {
            return;
        }

        BlockPos heartPos = eligibleLogs.get(random.nextInt(eligibleLogs.size()));
        Block heartBlock = creakingHeart.get();
        BlockState heartState = heartBlock.defaultBlockState();
        level.setBlock(heartPos, heartState, Block.UPDATE_ALL);

        // Updates its awake/dormant state from this tick.
        level.scheduleTick(heartPos, heartBlock, 1);
    }

    private static boolean isPaleGarden(ServerLevel level, BlockPos pos) {
        return level.getBiome(pos).unwrapKey()
                .map(key -> key.location().equals(PALE_GARDEN))
                .orElse(false);
    }

    private static boolean hasOpenEyeblossomNearby(ServerLevel level, BlockPos pos) {
        Optional<Block> openEyeblossom = block(OPEN_EYEBLOSSOM);
        if (openEyeblossom.isEmpty()) {
            return false;
        }
        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            if (level.getBlockState(nearby).is(openEyeblossom.get())) {
                return true;
            }
        }
        return false;
    }

    private static Optional<Block> block(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.getOptional(id);
    }

    private PaleBlossomCreakingHeart() {
    }
}
