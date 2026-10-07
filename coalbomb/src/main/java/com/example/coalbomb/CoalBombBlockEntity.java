package com.example.coalbomb;

import java.util.Arrays;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class CoalBombBlockEntity extends BlockEntity {

    // columns of the circle, sorted from the center outwards (shared cache)
    private static int cachedRadius = -1;
    private static int[] cachedOrder = new int[0];

    private int age = 0;
    private int progress = 0;

    public CoalBombBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COAL_BOMB.get(), pos, state);
    }

    private static synchronized int[] getOrder(int radius) {
        if (cachedRadius != radius) {
            int r2 = radius * radius;
            long[] keys = new long[(2 * radius + 1) * (2 * radius + 1)];
            int n = 0;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int d2 = dx * dx + dz * dz;
                    if (d2 <= r2) {
                        int packed = ((dx + radius) << 16) | (dz + radius);
                        keys[n++] = ((long) d2 << 32) | (long) packed;
                    }
                }
            }
            keys = Arrays.copyOf(keys, n);
            Arrays.sort(keys);
            int[] order = new int[n];
            for (int i = 0; i < n; i++) {
                order[i] = (int) (keys[i] & 0xFFFFFFFFL);
            }
            cachedOrder = order;
            cachedRadius = radius;
        }
        return cachedOrder;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CoalBombBlockEntity be) {
        if (level instanceof ServerLevel server) {
            be.serverTick(server);
        }
    }

    private void serverTick(ServerLevel level) {
        int fuse = CoalBombConfig.FUSE_SECONDS.get() * 20;
        int warn = CoalBombConfig.WARN_SECONDS.get() * 20;
        int radius = CoalBombConfig.RADIUS.get();
        double cx = worldPosition.getX() + 0.5D;
        double cy = worldPosition.getY() + 0.5D;
        double cz = worldPosition.getZ() + 0.5D;

        age++;
        if (age % 100 == 0) {
            setChanged();
        }

        // Phase 1: glowing and ticking
        if (age <= fuse) {
            if (age % 10 == 0) {
                level.sendParticles(ParticleTypes.FLAME, cx, cy + 0.7D, cz, 2, 0.2D, 0.1D, 0.2D, 0.01D);
            }
            if (age % 20 == 0) {
                float pitch = 0.5F + 1.5F * ((float) age / (float) Math.max(1, fuse));
                level.playSound(null, worldPosition, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 1.0F, pitch);
            }
            return;
        }

        // Phase 2: the whole area is marked red
        if (age <= fuse + warn) {
            if (age == fuse + 1) {
                level.playSound(null, worldPosition, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 3.0F, 0.7F);
            }
            for (int i = 0; i < 150; i++) {
                double angle = level.random.nextDouble() * Math.PI * 2.0D;
                double dist = Math.sqrt(level.random.nextDouble()) * radius;
                spawnRed(level, (int) Math.floor(cx + Math.cos(angle) * dist), (int) Math.floor(cz + Math.sin(angle) * dist));
            }
            return;
        }

        // Phase 3: the wave eats the world from the center, slowly at first, then faster
        int waveAge = age - fuse - warn;
        if (waveAge == 1) {
            level.playSound(null, worldPosition, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 8.0F, 0.5F);
        }
        int[] order = getOrder(radius);
        int perTick = (int) Math.min(CoalBombConfig.MAX_COLUMNS_PER_TICK.get(),
                CoalBombConfig.START_COLUMNS.get() + waveAge * CoalBombConfig.ACCELERATION.get());
        int end = Math.min(order.length, progress + perTick);

        int vr = CoalBombConfig.VERTICAL_RANGE.get();
        int minY = Math.max(level.getMinBuildHeight(), worldPosition.getY() - vr);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, worldPosition.getY() + vr);
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        int flags = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS;

        for (int i = progress; i < end; i++) {
            int packed = order[i];
            int x = worldPosition.getX() + (packed >> 16) - radius;
            int z = worldPosition.getZ() + (packed & 0xFFFF) - radius;
            for (int y = minY; y <= maxY; y++) {
                m.set(x, y, z);
                if (m.equals(worldPosition)) {
                    continue;
                }
                BlockState s = level.getBlockState(m);
                if (!s.isAir() && !s.is(Blocks.BEDROCK)) {
                    level.setBlock(m, Blocks.AIR.defaultBlockState(), flags);
                }
            }
        }
        progress = end;
        setChanged();

        // red marks on the ring just ahead of the wave
        int lookEnd = Math.min(order.length, progress + 300);
        for (int i = progress; i < lookEnd; i += 6) {
            int packed = order[i];
            spawnRed(level, worldPosition.getX() + (packed >> 16) - radius, worldPosition.getZ() + (packed & 0xFFFF) - radius);
        }

        if (progress >= order.length) {
            level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);
            level.playSound(null, BlockPos.containing(cx, cy, cz), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 8.0F, 0.4F);
        }
    }

    private static void spawnRed(ServerLevel level, int x, int z) {
        BlockPos probe = new BlockPos(x, level.getMinBuildHeight(), z);
        if (!level.hasChunkAt(probe)) {
            return;
        }
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        level.sendParticles(DustParticleOptions.REDSTONE, x + 0.5D, y + 0.1D, z + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Age", age);
        tag.putInt("Progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        age = tag.getInt("Age");
        progress = tag.getInt("Progress");
    }
}
