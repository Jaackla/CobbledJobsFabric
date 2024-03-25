package io.github.adainish.cobbledjobsfabric;

import ca.landonjw.gooeylibs2.api.tasks.Task;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import io.github.adainish.cobbledjobsfabric.cmd.Command;
import io.github.adainish.cobbledjobsfabric.config.DBConfig;
import io.github.adainish.cobbledjobsfabric.config.JobsConfig;
import io.github.adainish.cobbledjobsfabric.config.LanguageConfig;
import io.github.adainish.cobbledjobsfabric.listener.FabricActionListener;
import io.github.adainish.cobbledjobsfabric.storage.Database;
import io.github.adainish.cobbledjobsfabric.storage.PlayerStorage;
import io.github.adainish.cobbledjobsfabric.subscriptions.EventSubscriptions;
import io.github.adainish.cobbledjobsfabric.tasks.PlayerDataTask;
import io.github.adainish.cobbledjobsfabric.wrapper.DataWrapper;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class CobbledJobsFabric implements ModInitializer {

    public static CobbledJobsFabric instance;
    public static final String MOD_NAME = "CobbledJobs";
    public static final String VERSION = "1.0.0-Beta";
    public static final String AUTHORS = "Winglet";
    public static final String YEAR = "2023";
    private static final Logger log = LogManager.getLogger(MOD_NAME);
    private static File configDir;
    private static File storage;
    private static File playerStorageDir;
    private static MinecraftServer server;
    public static JobsConfig jobsConfig;
    public static LanguageConfig languageConfig;
    public static DBConfig dbConfig;
    public static DataWrapper dataWrapper;

    public PlayerStorage playerStorage;

    public static EventSubscriptions eventSubscriptions;

    public static FabricActionListener fabricActionListener;

    public static Logger getLog() {
        return log;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        CobbledJobsFabric.server = server;
    }

    public static File getConfigDir() {
        return configDir;
    }

    public static void setConfigDir(File configDir) {
        CobbledJobsFabric.configDir = configDir;
    }

    public static File getStorage() {
        return storage;
    }

    public static void setStorage(File storage) {
        CobbledJobsFabric.storage = storage;
    }

    public static File getPlayerStorageDir() {
        return playerStorageDir;
    }

    public static void setPlayerStorageDir(File playerStorageDir) {
        CobbledJobsFabric.playerStorageDir = playerStorageDir;
    }

    @Override
    public void onInitialize()
    {
        instance = this;
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        //do data set up
        PlatformEvents.SERVER_STARTED.subscribe(Priority.NORMAL, event -> {
            setServer(event.getServer());
            playerStorage = new PlayerStorage();
            //init subscriptions
            eventSubscriptions = new EventSubscriptions();
            //register fabric events
            fabricActionListener = new FabricActionListener();
            dataWrapper = new DataWrapper();
            reload();
            Task.builder().execute(new PlayerDataTask()).infinite().interval((20 * 60) * 30).build();
            return Unit.INSTANCE;
        });

        PlatformEvents.SERVER_STOPPING.subscribe(Priority.NORMAL, t -> {
            this.handleShutDown();
            return Unit.INSTANCE;
        });



        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Command.getCommand());
        });
    }

    public void initDirs() {
        setConfigDir(new File(FabricLoader.getInstance().getConfigDir() + "/CobbledJobs/"));
        getConfigDir().mkdir();
        setStorage(new File(getConfigDir(), "/storage/"));
        getStorage().mkdirs();
        setPlayerStorageDir(new File(storage, "/playerdata/"));
        getPlayerStorageDir().mkdirs();
    }



    public void initConfigs() {
        log.warn("Loading Config Files");
        DBConfig.writeConfig();
        dbConfig = DBConfig.getConfig();

        if (dbConfig != null)
        {
            if (dbConfig.enabled)
            {
                playerStorage.database = new Database();
            }
        }
        JobsConfig.writeConfig();
        jobsConfig = JobsConfig.getConfig();
        LanguageConfig.writeConfig();
        languageConfig = LanguageConfig.getConfig();
    }

    public void reload() {
        initDirs();
        initConfigs();

    }

    public void handleShutDown()
    {
        playerStorage.saveAll();
        if (playerStorage.database != null)
            playerStorage.database.shutdown();
    }
}
