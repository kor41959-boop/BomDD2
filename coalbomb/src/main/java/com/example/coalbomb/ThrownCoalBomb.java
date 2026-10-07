package com.example.coalbomb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class ThrownCoalBomb extends ThrowableItemProjectile {

    public ThrownCoalBomb(EntityType<? extends ThrownCoalBomb> type, Level level) {
        super(type, level);
    }

    public ThrownCoalBomb(Level level, LivingEntity owner) {
        super(ModEntities.COAL_BOMB.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.COAL_BOMB.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) {
            return;
        }
        BlockPos origin;
        if (result instanceof BlockHitResult blockHit) {
            origin = blockHit.getBlockPos().relative(blockHit.getDirection());
        } else {
            origin = this.blockPosition();
        }
        if (!plant(origin)) {
            spawnAtLocation(ModItems.COAL_BOMB.get());
        }
        discard();
    }

    private boolean plant(BlockPos origin) {
        BlockState bomb = ModBlocks.COAL_BOMB_BLOCK.get().defaultBlockState();
        BlockPos.MutableBlockPos p = origin.mutable();
        for (int i = 0; i < 4; i++) {
            if (level().isOutsideBuildHeight(p)) {
                return false;
            }
            if (level().getBlockState(p).canBeReplaced()) {
                level().setBlock(p, bomb, 3);
                level().playSound(null, p, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 0.5F);
                return true;
            }
            p.move(Direction.UP);
        }
        return false;
    }
}
