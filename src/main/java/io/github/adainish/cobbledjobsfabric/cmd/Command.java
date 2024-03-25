package io.github.adainish.cobbledjobsfabric.cmd;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.cobbledjobsfabric.CobbledJobsFabric;
import io.github.adainish.cobbledjobsfabric.obj.data.Player;
import io.github.adainish.cobbledjobsfabric.storage.PlayerStorage;
import io.github.adainish.cobbledjobsfabric.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class Command
{
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("jobs")
                .executes(cc -> {
                    try {
                        Player player = CobbledJobsFabric.instance.playerStorage.getPlayer(cc.getSource().getPlayerOrException().getUUID());
                        if (player != null)
                        {
                            player.viewGUI(cc.getSource().getPlayer());
                        } else {
                            Util.send(cc.getSource(), "&cUnable to load your jobs data...");
                        }
                    } catch (Exception e)
                    {

                    }
                    return 1;
                })
                .then(Commands.literal("reload")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                        .executes(cc -> {
                            CobbledJobsFabric.instance.reload();
                            Util.send(cc.getSource(), "&eReloaded the jobs add-on, please check the console for any errors.");
                            return 1;
                        })
                )
                ;

    }
}
