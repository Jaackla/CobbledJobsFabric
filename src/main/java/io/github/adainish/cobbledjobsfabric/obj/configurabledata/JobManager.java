package io.github.adainish.cobbledjobsfabric.obj.configurabledata;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import io.github.adainish.cobbledjobsfabric.enumerations.DefaultJobs;
import io.github.adainish.cobbledjobsfabric.enumerations.JobAction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import java.util.HashMap;

public class JobManager
{
    public HashMap<String, ConfigurableJob> configurableJobs = new HashMap<>();

    public JobManager()
    {

    }

    public void initDefaults()
    {
        if (configurableJobs.isEmpty())
        {
            for (DefaultJobs defaultJob:DefaultJobs.values()) {
                ConfigurableJob configurableJob = new ConfigurableJob();
                configurableJob.id = defaultJob.name().toLowerCase();
                configurableJob.prettyName = "&b" + defaultJob.name();
                configurableJob.enabled = true;
                switch (defaultJob)
                {
                    case Miner -> {
                        JobType jobType = new JobType();
                        jobType.prettyTitle = "&b" + "Mine";
                        //load default action keys
                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.STONE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.DIAMOND_ORE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.ANDESITE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.COBBLESTONE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.GOLD_ORE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.IRON_ORE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            actionKey.prettyString = actionKey.actionKey
                                    .replace("cobblemon:", "")
                                            .replace("minecraft:", "")
                                                    .replace("_", " ");
                            jobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }
                        configurableJob.jobTypes.put(JobAction.Mine.name().toLowerCase(), jobType);
                        break;
                    }
                    case Hunter -> {
                        JobType jobType = new JobType();
                        //load default action keys
                        jobType.prettyTitle = "&bMob " + defaultJob.name();
                        //vanilla entities
                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {

                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.BLAZE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SKELETON);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.CREEPER);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ENDERMAN);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SPIDER);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            jobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }

                        // cobblemon
                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("charmander").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("charmeleon").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("charizard").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("vulpix").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("ninetales").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("pikachu").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            jobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }
                        configurableJob.jobTypes.put(JobAction.Kill.name().toLowerCase(), jobType);
                        break;
                    }
                    case Crafter -> {
                        JobType jobType = new JobType();
                        //load default action keys
                        jobType.prettyTitle = "&b" + defaultJob.name();
                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.BIRCH_BOAT);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.IRON_AXE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.IRON_SHOVEL);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.IRON_SWORD);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.IRON_PICKAXE);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.IRON_BLOCK);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            jobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }
                        configurableJob.jobTypes.put(JobAction.Craft.name().toLowerCase(), jobType);
                        break;
                    }
                    case Collector -> {
                        JobType jobType = new JobType();
                        jobType.prettyTitle = "&b" + "Capture Pokemon";
                        JobType secondJobType = new JobType();
                        secondJobType.prettyTitle = "&b" + "Evolve Pokemon";
                        //load default action keys
                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("charmander").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("charmeleon").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("charizard").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("vulpix").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("ninetales").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("pikachu").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            jobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }

                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("wartortle").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("charmeleon").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("ivysaur").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("wartortle").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("ninetales").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("graveler").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            jobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }

                        configurableJob.jobTypes.put(JobAction.Capture.name().toLowerCase(), jobType);
                        configurableJob.jobTypes.put(JobAction.Evolve.name().toLowerCase(), secondJobType);
                        break;
                    }
                    case Fisherman -> {
                        JobType jobType = new JobType();
                        jobType.prettyTitle = "&b" + "Fish Items";
                        JobType secondaryJobType = new JobType();
                        secondaryJobType.prettyTitle = "&b" + "Fish Pokemon";
                        //load default action keys

                        //vanilla entities
                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {

                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.COD);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SALMON);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.TROPICAL_FISH);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.FROG);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.DOLPHIN);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PUFFERFISH);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            secondaryJobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }

                        //load default action keys
                        //load vanilla fishing action
                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.ACACIA_LOG);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.SALMON);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.NAUTILUS_SHELL);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.TROPICAL_FISH);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.PUFFERFISH);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.COD);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            jobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }
                        //load cobblemon kill water type pokemon as action
                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("magikarp").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("gyarados").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("psyduck").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("golduck").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("squirtle").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = PokemonSpecies.INSTANCE.getByName("wartortle").getResourceIdentifier();
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            secondaryJobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }
                        configurableJob.jobTypes.put(JobAction.Fish.name().toLowerCase(), jobType);
                        configurableJob.jobTypes.put(JobAction.Kill.name().toLowerCase(), secondaryJobType);
                        break;
                    }
                    case Woodcutter -> {
                        JobType jobType = new JobType();
                        jobType.prettyTitle = "&b" + "Cut Wood";
                        //load default action keys
                        for (int i = 0; i < 6; i++) {
                            ActionKey actionKey = new ActionKey();
                            actionKey.baseExperience = 1;
                            actionKey.baseWorth = 1;
                            switch (i)
                            {
                                case 0 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.ACACIA_LOG);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 1 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.BIRCH_LOG);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 2 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.OAK_LOG);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                    break;
                                }
                                case 3 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.DARK_OAK_LOG);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 4 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.MANGROVE_LOG);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }
                                case 5 -> {
                                    ResourceLocation location = BuiltInRegistries.ITEM.getKey(Items.SPRUCE_LOG);
                                    actionKey.actionKey = location.toString().toLowerCase();
                                }

                            }
                            jobType.actionKeys.put(actionKey.actionKey, actionKey);
                        }
                        configurableJob.jobTypes.put(JobAction.Mine.name().toLowerCase(), jobType);
                        break;
                    }
                }
                for (int i = 0; i < 15; i++) {
                    ConfigurableLevel configurableLevel = new ConfigurableLevel();
                    configurableLevel.requiredExperience = (i + 1) * 100;
                    configurableLevel.experienceBooster = (i + 1) * 1.15;
                    configurableLevel.moneyBooster = (i + 1) * 1.15;
                    configurableJob.levels.put(i, configurableLevel);
                }

                configurableJobs.put(configurableJob.id, configurableJob);
            }
        }
    }
}
