package io.github.adainish.cobbledjobsfabric.listener;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.adainish.cabled.events.EntityFishingRodCallback;
import io.github.adainish.cabled.events.PlayerCraftCallback;
import io.github.adainish.cobbledjobsfabric.enumerations.JobAction;
import io.github.adainish.cobbledjobsfabric.obj.data.Player;
import io.github.adainish.cobbledjobsfabric.storage.PlayerStorage;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FabricActionListener
{

    public FabricActionListener()
    {
        registerKilling();
        registerCrafting();
        registerMining();
        registerFishing();
    }

    public void registerFishing()
    {

        EntityFishingRodCallback.EVENT.register((fishingHook, entity, player) -> {
            if (fishingHook == null)
                return InteractionResult.PASS;
            if (fishingHook.isInWater())
            {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof net.minecraft.world.item.FishingRodItem)
                {
                    try {
                        Player p = PlayerStorage.getPlayer(player.getUUID());
                        if (p != null)
                        {
                            String update = "fishing_rod";
                            if (entity instanceof PokemonEntity)
                                update = ((PokemonEntity) entity).getPokemon().getSpecies().getResourceIdentifier().toString();
                            if (entity instanceof ItemEntity itemEntity)
                                update = BuiltInRegistries.ITEM.getKey(itemEntity.getItem().getItem()).toString();
                            p.updateJobData(JobAction.Fish, update);
                            p.updateCache();
                        }
                    } catch (Exception e)
                    {

                    }
                }
            }
            return InteractionResult.PASS;
        });
    }

    public void registerCrafting()
    {
        PlayerCraftCallback.EVENT.register((world, player, stack, amount) -> {
            try {
                Player p = PlayerStorage.getPlayer(player.getUUID());
                if (p != null)
                {
                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    p.updateJobData(JobAction.Craft, location.toString());
                    p.updateCache();
                }
            } catch (Exception e)
            {

            }
        });
    }

    public void registerMining() {
        PlayerBlockBreakEvents.AFTER.register((world, serverPlayer, pos, state, blockEntity) -> {
            if (world.isClientSide())
                return;
            try {
                Player player = PlayerStorage.getPlayer(serverPlayer.getUUID());
                if (player != null) {
                    //update job data for mining
                    Block block = state.getBlock();
                    ResourceLocation location = BuiltInRegistries.BLOCK.getKey(block);
                    player.updateJobData(JobAction.Mine, location.toString());
                    player.updateCache();
                }
            } catch (Exception e) {

            }
        });
    }

    public void registerKilling()
    {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (world.isClientSide())
                return;
            if (entity instanceof ServerPlayer)
            {
                try {
                    Player player = PlayerStorage.getPlayer(entity.getUUID());
                    if (player != null)
                    {
                        //update job data for killing
                        ResourceLocation location = null;
                        if (killedEntity instanceof PokemonEntity)
                        {
                            location = ((PokemonEntity) killedEntity).getPokemon().getSpecies().getResourceIdentifier();
                        } else {
                            location = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
                        }
                        player.updateJobData(JobAction.Kill, location.toString());
                        player.updateCache();
                    }
                } catch (Exception e)
                {

                }
            }
        });
    }
}
