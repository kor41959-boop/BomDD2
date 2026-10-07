package com.example.coalbomb;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, CoalBombMod.MODID);

    public static final RegistryObject<EntityType<ThrownCoalBomb>> COAL_BOMB = ENTITY_TYPES.register("coal_bomb",
            () -> EntityType.Builder.<ThrownCoalBomb>of(ThrownCoalBomb::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("coal_bomb"));

    private ModEntities() {}
}
