package com.example.coalbomb;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CoalBombMod.MODID);

    public static final RegistryObject<Item> COAL_BOMB = ITEMS.register("coal_bomb",
            () -> new CoalBombItem(new Item.Properties().stacksTo(16)));

    private ModItems() {}
}
