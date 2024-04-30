package creativitium.revolution.dimension.data;

import creativitium.revolution.dimension.Dimension;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class WorldCommand extends RCommand
{
    private CustomWorld world;

    public WorldCommand(CustomWorld world)
    {
        super(world.getName().replace(" ", "_"), world.getCommand().getDescription(),
                world.getCommand().getUsage(), world.getCommand().getAliases(), world.getCommand().getPermission(),
                SourceType.ONLY_IN_GAME);

        this.world = world;
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        playerSender.teleportAsync(world.getWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Dimension.getInstance();
    }
}
