package dev.polaris_light.allvoid;

import dev.polaris_light.allvoid.config.AllVoidConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.NaturalSpawner;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

/** Server-side event hooks for the Carpet-style renewable resources. */
public final class AllVoidEvents {
    public AllVoidEvents() {
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                EntityType.PIGLIN_BRUTE,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AllVoidEvents::canSpawnPiglinBrute,
                RegisterSpawnPlacementsEvent.Operation.OR
        );
    }

    @SubscribeEvent
    public void addWanderingTraderTrades(WandererTradesEvent event) {
        if (!AllVoidConfig.tallFlowersFromWanderingTrader) {
            return;
        }
        for (var flower : new ItemStack[]{
                new ItemStack(Items.LILAC),
                new ItemStack(Items.ROSE_BUSH),
                new ItemStack(Items.PEONY),
                new ItemStack(Items.SUNFLOWER)
        }) {
            event.getGenericTrades().add(new BasicItemListing(1, flower, 12, 1, 0.05F) {
                @Override
                public MerchantOffer getOffer(Entity trader, RandomSource random) {
                    if (!(trader.level() instanceof ServerLevel level)
                            || !AllVoidConfig.featuresEnabled(level)) {
                        return null;
                    }
                    return super.getOffer(trader, random);
                }
            });
        }
    }

    @SubscribeEvent
    public void convertGuardian(EntityStruckByLightningEvent event) {
        if (!AllVoidConfig.renewableSponges
                || !(event.getEntity() instanceof Guardian guardian)
                || guardian instanceof ElderGuardian
                || !(guardian.level() instanceof ServerLevel level)
                || !AllVoidConfig.featuresEnabled(level)) {
            return;
        }

        ElderGuardian elder = EntityType.ELDER_GUARDIAN.create(level);
        if (elder == null) {
            return;
        }
        elder.moveTo(guardian.getX(), guardian.getY(), guardian.getZ(), guardian.getYRot(), guardian.getXRot());
        elder.setYHeadRot(guardian.getYHeadRot());
        elder.setPersistenceRequired();
        if (guardian.hasCustomName()) {
            elder.setCustomName(guardian.getCustomName());
            elder.setCustomNameVisible(guardian.isCustomNameVisible());
        }
        level.addFreshEntity(elder);
        guardian.discard();
        event.setCanceled(true);
    }

    private static boolean canSpawnPiglinBrute(
            EntityType<PiglinBrute> type,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        if (!AllVoidConfig.piglinsSpawningInBastions
                || !AllVoidConfig.featuresEnabled(level.getLevel().getChunkSource().getGenerator())
                || level.getLevel().dimension() != Level.NETHER) {
            return false;
        }

        Structure bastion = level.getLevel().registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getOrThrow(BuiltinStructures.BASTION_REMNANT);
        if (!level.getLevel().structureManager().getStructureAt(pos, bastion).isValid()) {
            return false;
        }

        BlockPos below = pos.below();
        return level.getBlockState(below).isValidSpawn(level, below, type)
                && NaturalSpawner.isValidEmptySpawnBlock(
                        level, pos, level.getBlockState(pos), level.getFluidState(pos), type)
                && NaturalSpawner.isValidEmptySpawnBlock(
                        level, pos.above(), level.getBlockState(pos.above()), level.getFluidState(pos.above()), type)
                && Piglin.checkPiglinSpawnRules(EntityType.PIGLIN, level, spawnType, pos, random);
    }
}
