package com.example.coalbomb;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CoalBombMod.MODID);

    public static final RegistryObject<BlockEntityType<CoalBombBlockEntity>> COAL_BOMB = BLOCK_ENTITIES.register("coal_bomb",
            () -> BlockEntityType.Builder.of(CoalBombBlockEntity::new, ModBlocks.COAL_BOMB_BLOCK.get()).build(null));

    private ModBlockEntities() {}
}
