package io.github.adainish.cobbledjobsfabric.subscriptions;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import io.github.adainish.cobbledjobsfabric.CobbledJobsFabric;
import io.github.adainish.cobbledjobsfabric.enumerations.JobAction;
import io.github.adainish.cobbledjobsfabric.obj.data.Player;
import io.github.adainish.cobbledjobsfabric.storage.PlayerStorage;
import kotlin.Unit;

public class EventSubscriptions
{

    public EventSubscriptions()
    {

        subscribeToPlayerLogin();
        subscribeToPlayerLogout();
        subscribeToCapture();
        subscribeToFainting();
        subscribeToEvolving();
    }

    public void subscribeToEvolving()
    {
        CobblemonEvents.EVOLUTION_COMPLETE.subscribe(Priority.NORMAL, event -> {
            try {
                Player player = CobbledJobsFabric.instance.playerStorage.getPlayer(event.component1().getOwnerPlayer().getUUID());
                if (player != null)
                {
                    //update job data for evolving
                    player.updateJobData(JobAction.Evolve, event.component1().getSpecies().resourceIdentifier.toString());
                    player.updateCache();
                }
            } catch (Exception e)
            {
                return Unit.INSTANCE;
            }

            return Unit.INSTANCE;
        });

    }

    public void subscribeToFainting()
    {
        CobblemonEvents.POKEMON_FAINTED.subscribe(Priority.NORMAL, event -> {
            try {
                Player player = CobbledJobsFabric.instance.playerStorage.getPlayer(event.component1().getOwnerPlayer().getUUID());
                if (player != null)
                {
                    //update job data for killing
                    player.updateJobData(JobAction.Kill, event.component1().getSpecies().resourceIdentifier.toString());
                    player.updateCache();
                }
            } catch (Exception e)
            {
                return Unit.INSTANCE;
            }

            return Unit.INSTANCE;
        });

    }

    public void subscribeToCapture()
    {
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {
            Player player = CobbledJobsFabric.instance.playerStorage.getPlayer(event.getPlayer().getUUID());
            if (player != null)
            {
                //update job data for capturing
                player.updateJobData(JobAction.Capture, event.component1().getSpecies().resourceIdentifier.toString());
                player.updateCache();
            }
            return Unit.INSTANCE;
        });

    }

    public void subscribeToPlayerLogin()
    {
        PlatformEvents.SERVER_PLAYER_LOGIN.subscribe(Priority.NORMAL, event -> {


            Player player = CobbledJobsFabric.instance.playerStorage.getPlayer(event.getPlayer().getUUID());
            if (player == null) {
                CobbledJobsFabric.instance.playerStorage.makePlayer(event.getPlayer());
                player = CobbledJobsFabric.instance.playerStorage.getPlayer(event.getPlayer().getUUID());
            }

            if (player != null) {
                player.userName = event.getPlayer().getName().plainCopy().toString();
                player.syncWithJobManager();
                player.updateCache();
            }

            return Unit.INSTANCE;
        });
    }

    public void subscribeToPlayerLogout()
    {
        PlatformEvents.SERVER_PLAYER_LOGOUT.subscribe(Priority.NORMAL, event -> {
            Player player = CobbledJobsFabric.instance.playerStorage.getPlayer(event.getPlayer().getUUID());
            if (player != null) {
                player.save();
            }
            return Unit.INSTANCE;
        });

    }
}
