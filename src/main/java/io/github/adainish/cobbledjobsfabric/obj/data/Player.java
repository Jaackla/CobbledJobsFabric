package io.github.adainish.cobbledjobsfabric.obj.data;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.google.gson.Gson;
import io.github.adainish.cobbledjobsfabric.CobbledJobsFabric;
import io.github.adainish.cobbledjobsfabric.enumerations.JobAction;
import io.github.adainish.cobbledjobsfabric.obj.configurabledata.ActionKey;
import io.github.adainish.cobbledjobsfabric.obj.configurabledata.ConfigurableJob;
import io.github.adainish.cobbledjobsfabric.obj.configurabledata.ConfigurableLevel;
import io.github.adainish.cobbledjobsfabric.obj.configurabledata.JobType;
import io.github.adainish.cobbledjobsfabric.storage.PlayerStorage;
import io.github.adainish.cobbledjobsfabric.util.Adapters;
import io.github.adainish.cobbledjobsfabric.util.EconomyUtil;
import io.github.adainish.cobbledjobsfabric.util.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {
    public UUID uuid;
    public String userName = "DEFAULT_DATA";
    public int maxJobs = 1;
    public HashMap<String, Job> jobs = new HashMap<>();

    public Player(UUID uuid) {
        this.uuid = uuid;
    }

    public Document toDocument() {
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        String json = gson.toJson(this);
        return Document.parse(json);
    }


    public void sendMessage(String message) {
        //check if online
        if (Util.isOnline(uuid)) {
            Util.send(Util.getPlayer(uuid), message);
        }
    }

    public void updateCache() {
        CobbledJobsFabric.dataWrapper.playerCache.put(uuid, this);
    }

    public void save() {
        //save to storage file
        CobbledJobsFabric.instance.playerStorage.savePlayer(this);
    }

    public void viewGUI(ServerPlayer serverPlayer)
    {
        UIManager.openUIForcefully(serverPlayer, mainJobsGUI());
    }

    public boolean jobsMaxedOut()
    {
        return getCurrentActiveJobCount() >= maxJobs;
    }

    public int getCurrentActiveJobCount() {
        AtomicInteger amount = new AtomicInteger();
        jobs.forEach((s, job) -> {
            ConfigurableJob configurableJob = job.getConfigurableJob();
            if (configurableJob != null) {
                if (job.hasJob && configurableJob.enabled)
                    amount.getAndIncrement();
            }
        });
        return amount.get();
    }

    public int getCurrentLevelForJob(Job job) {
        return job.level;
    }

    public ConfigurableLevel getConfigurableLevel(Job job, ConfigurableJob configurableJob) {
        if (configurableJob.levels.containsKey(getCurrentLevelForJob(job)))
            return configurableJob.levels.get(getCurrentLevelForJob(job));
        return null;
    }

    public boolean isJob(ConfigurableJob configurableJob, Job job)
    {
        return (job.jobName.equals(configurableJob.id));
    }

    public boolean inJob(ConfigurableJob configurableJob)
    {
        for (Job job : jobs.values()) {
            if (job.jobName.equals(configurableJob.id)) {
                return job.hasJob;
            }
        }
        return false;
    }

    public double retrieveExperience(Job job, ConfigurableJob configurableJob, String jobTypeID, String actionKeyID) {
        double amount = configurableJob.jobTypes.get(jobTypeID).actionKeys.get(actionKeyID).baseExperience;

        ConfigurableLevel configurableLevel = getConfigurableLevel(job, configurableJob);
        if (configurableLevel != null) {
            if (configurableLevel.experienceBooster > 0)
                amount = amount * configurableLevel.experienceBooster;
        }
        return amount;
    }


    public double retrieveMoney(Job job, ConfigurableJob configurableJob, String jobTypeID, String actionKeyID) {
        double amount = configurableJob.jobTypes.get(jobTypeID).actionKeys.get(actionKeyID).baseWorth;

        ConfigurableLevel configurableLevel = getConfigurableLevel(job, configurableJob);
        if (configurableLevel != null) {
            if (configurableLevel.moneyBooster > 0)
                amount = amount * configurableLevel.moneyBooster;
        }
        return amount;
    }

    public void updateJobData(JobAction jobAction, String actionKeyID) {
        jobs.forEach((s, job) -> {
            ConfigurableJob configurableJob = job.getConfigurableJob();
            if (configurableJob != null) {
                if (configurableJob.enabled) {
                    configurableJob.jobTypes.forEach((s1, jobType) -> {
                        if (s1.equalsIgnoreCase(jobAction.name())) {
                            if (jobType.actionKeys.containsKey(actionKeyID)) {
                                if (!job.hasJob)
                                    return;
                                double exp = retrieveExperience(job, configurableJob, s1, actionKeyID);
                                //increase exp
                                job.increaseExperience(exp);
                                //exp message
                                sendMessage("&7You gained %experience% exp.".replace("%experience%", String.valueOf(exp)));
                                double money = retrieveMoney(job, configurableJob, s1, actionKeyID);
                                //increase money
                                EconomyUtil.add(uuid, money);
                                //money message
                                sendMessage("&7Added %amount%$ to your account.".replace("%amount%", String.valueOf(money)));
                                //if can level up, level up
                                if (job.canLevelUp()) {
                                    job.increaseLevel();
                                    sendMessage("&7You leveled up your %job% to %level%.");
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void updateJobStatus(ConfigurableJob configurableJob)
    {
        AtomicBoolean statusBool = new AtomicBoolean(true);
        for (Job job : jobs.values()) {
            if (isJob(configurableJob, job)) {
                if (!job.hasJob) {
                    if (jobsMaxedOut()) {
                        sendMessage("&cYou've already maxed out your job count....");
                        return;
                    }
                    job.hasJob = true;
                    statusBool.set(true);
                } else {
                    statusBool.set(false);
                    job.hasJob = false;
                }
            }
        }
        String status = "&ajoined";
        if (!statusBool.get())
            status = "&cleft";
        sendMessage("&7You %status% %job%."
                .replace("%status%", status)
                .replace("%job%", configurableJob.prettyName)
        );
    }

    public void syncWithJobManager() {
        if (jobs.isEmpty()) {
            CobbledJobsFabric.jobsConfig.jobManager.configurableJobs.forEach((s, configurableJob) -> {
                Job job = new Job();
                job.jobName = s;
                jobs.put(s, job);
            });
        } else {
            //mark old jobs as disabled
            jobs.forEach((s, job) -> {
                if (CobbledJobsFabric.jobsConfig.jobManager.configurableJobs.containsKey(s))
                    return;
                job.hasJob = false;
            });
            CobbledJobsFabric.jobsConfig.jobManager.configurableJobs.forEach((s, configurableJob) -> {
                if (jobs.containsKey(s)) {
                    jobs.get(s).jobName = s;
                } else {
                    Job job = new Job();
                    job.jobName = s;
                    jobs.put(s, job);
                }
            });
        }
    }

    public GooeyButton filler() {
        return GooeyButton.builder()
                .display(new ItemStack(Items.GRAY_STAINED_GLASS_PANE))
                .build();
    }

    public List<Button> sortedJobsButtons() {
        List<Button> buttons = new ArrayList<>();
        List<ConfigurableJob> configurableJobs = new ArrayList<>(CobbledJobsFabric.jobsConfig.jobManager.configurableJobs.values());
        configurableJobs.sort(Comparator.comparing(configurableJob -> configurableJob.displayOrder));

        for (ConfigurableJob configurableJob : configurableJobs) {
            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString(configurableJob.prettyName))
                    .lore(Util.formattedArrayList(configurableJob.description))
                    .display(configurableJob.displayStack.copy())
                    .onClick(b -> {
                        UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(configurableJob));
                    })
                    .build();
            buttons.add(button);
        }
        return buttons;
    }

    public List<String> parsedActionKeyString(ActionKey actionKey)
    {
        List<String> strings = new ArrayList<>();

        for (String s:actionKey.description) {
            strings.add(s
                    .replace("%amount%", String.valueOf(actionKey.baseWorth))
                    .replace("%exp%", String.valueOf(actionKey.baseExperience))
            );
        }

        return strings;
    }

    public List<Button> sortedActionKeys(JobType jobType)
    {
        List<Button> buttons = new ArrayList<>();

        List<ActionKey> sortedActionKeys = new ArrayList<>(jobType.actionKeys.values());
        sortedActionKeys.sort(Comparator.comparing(actionkey -> actionkey.baseWorth));
        for (ActionKey actionKey:sortedActionKeys) {
            ResourceLocation location = ResourceLocation.tryParse(actionKey.actionKey);
            if (location == null)
                continue;
            new ItemStack(Items.DIRT);
            ItemStack stack;
            if (PokemonSpecies.INSTANCE.getByIdentifier(location) != null)
            {
                stack = Util.returnIcon(PokemonSpecies.INSTANCE.getByIdentifier(location).create(1));
            } else {
                stack = new ItemStack(BuiltInRegistries.ITEM.get(location));
            }
            if (stack.isEmpty())
                continue;

            GooeyButton button = GooeyButton.builder()
                    .display(stack)
                    .title(Util.formattedString(actionKey.prettyString))
                    .lore(Util.formattedArrayList(parsedActionKeyString(actionKey)))
                    .build();

            buttons.add(button);
        }
        return buttons;
    }

    public List<Button> sortedActionButtons(ConfigurableJob configurableJob) {
        List<Button> buttons = new ArrayList<>();
        List<JobType> sortedTypes = new ArrayList<>(configurableJob.jobTypes.values());
        sortedTypes.sort(Comparator.comparing(jobType -> jobType.displayOrder));
        sortedTypes.forEach((jobType) -> {
            GooeyButton button = GooeyButton.builder()
                    .title(Util.formattedString(jobType.prettyTitle))
                    .lore(Util.formattedArrayList(jobType.description))
                    .display(jobType.displayStack.copy())
                    .onClick(b -> {
                        UIManager.openUIForcefully(b.getPlayer(), jobActionKeyScrollableInfoMenu(configurableJob, jobType));
                    })
                    .build();

            buttons.add(button);
        });

        return buttons;
    }

    public GooeyPage jobActionMenu(ConfigurableJob configurableJob)
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());
        boolean inJob = inJob(configurableJob);
        ItemStack joinOrLeaveStack = new ItemStack(Items.ENDER_PEARL);
        List<String> joinOrLeaveLore = new ArrayList<>(Arrays.asList("&aClick to join this job", "&7----> &r&7Joining this job gives money when completing actions!"));
        String joinOrLeaveTitle = "&aJoin job";
        if (inJob) {
            joinOrLeaveTitle = "&eActive Job";
            joinOrLeaveLore = new ArrayList<>(Arrays.asList("&cClick to leave this job", "&7----> &r&7You will no longer receive money for doing the jobs tasks", "&7but you'll be able to join other jobs if you're at max capacity!"));
            joinOrLeaveStack = new ItemStack(Items.ALLIUM);
        } else if (jobsMaxedOut())
        {
            joinOrLeaveTitle = "&4Max Jobs reached!";
            joinOrLeaveLore = new ArrayList<>(Arrays.asList("&7-----> &r&bYou've already maxed out your jobs!", "&7&m| &r&eYou'll need to leave a job before joining this one!"));
            joinOrLeaveStack = new ItemStack(Items.TNT);
        }
        GooeyButton joinOrLeave = GooeyButton.builder()
                .title(Util.formattedString(joinOrLeaveTitle))
                .display(joinOrLeaveStack)
                .lore(Util.formattedArrayList(joinOrLeaveLore))
                .onClick(b -> {
                    updateJobStatus(configurableJob);
                    UIManager.openUIForcefully(b.getPlayer(), jobActionMenu(configurableJob));
                })
                .build();

        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString("&3Go Back"))
                .display(new ItemStack(Items.ARROW))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(configurableJob));
                })
                .build();

        builder.set(2, 3, back);
        builder.set(2, 5, joinOrLeave);
        return GooeyPage.builder().template(builder.build()).build();
    }

    public GooeyPage selectOptionMenu(ConfigurableJob configurableJob)
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString("&3Go Back"))
                .display(new ItemStack(Items.ARROW))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), mainJobsGUI());
                })
                .build();

        GooeyButton viewJobInfo = GooeyButton.builder()
                .title(Util.formattedString("&aJob Info"))
                .display(new ItemStack(Items.ENDER_CHEST))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), jobActionScrollableInfoMenu(configurableJob));
                })
                .build();

        GooeyButton joinOption = GooeyButton.builder()
                .title(Util.formattedString("&bJob Options"))
                .display(new ItemStack(Items.ENDER_PEARL))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), jobActionMenu(configurableJob));
                })
                .build();

        builder.set(2, 2, back);
        builder.set(2, 4, joinOption);
        builder.set(2, 6, viewJobInfo);
        return GooeyPage.builder().template(builder.build()).build();
    }

    public LinkedPage jobActionScrollableInfoMenu(ConfigurableJob configurableJob)
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Previous Page"))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Next Page"))
                .linkType(LinkType.Next)
                .build();

        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString("&3Go Back"))
                .display(new ItemStack(Items.ARROW))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), selectOptionMenu(configurableJob));
                })
                .build();

        builder.set(0, 3, previous)
                .set(0, 4, back)
                .set(0, 5, next)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), sortedActionButtons(configurableJob), LinkedPage.builder().template(builder.build()));
    }

    public LinkedPage jobActionKeyScrollableInfoMenu(ConfigurableJob configurableJob, JobType jobType)
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Previous Page"))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Next Page"))
                .linkType(LinkType.Next)
                .build();

        GooeyButton back = GooeyButton.builder()
                .title(Util.formattedString("&3Go Back"))
                .display(new ItemStack(Items.ARROW))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), jobActionScrollableInfoMenu(configurableJob));
                })
                .build();
        builder.set(0, 3, previous)
                .set(0, 4, back)
                .set(0, 5, next)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), sortedActionKeys(jobType), LinkedPage.builder().template(builder.build()));
    }


    public LinkedPage mainJobsGUI() {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Previous Page"))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .title(Util.formattedString("Next Page"))
                .linkType(LinkType.Next)
                .build();
        builder.set(0, 3, previous)
                .set(0, 5, next)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), sortedJobsButtons(), LinkedPage.builder().template(builder.build()));
    }
}
