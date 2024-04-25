package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@CommandParameters(name = "smite",
        description = "Someone being a little bitch? Smite them down!",
        usage = "/smite <player>",
        permission = "administration.command.smite",
        source = SourceType.BOTH)
public class Command_smite extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        final String reason = (args.length > 1) ? StringUtils.join(ArrayUtils.remove(args, 0), " ") : null;

        Optional.ofNullable(Bukkit.getPlayer(args[0])).ifPresentOrElse(player ->{
            Shortcuts.broadcast("administration.broadcast.smite", Placeholder.unparsed("player", player.getName()));
            if (reason != null) Shortcuts.broadcast("administration.broadcast.reason", Placeholder.unparsed("reason", reason));

            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.setHealth(0.0);

            // Slightly different approach than the original method of smiting. This one only uses 1 Location instance.
            final Location position = player.getLocation();
            for (int x = -1; x <= 1; x++)
            {
                for (int z = -1; z <= 1; z++)
                {
                    position.add(x, 0, z);
                    player.getWorld().strikeLightning(position);
                }
            }

        }, () -> msg(sender, "revolution.command.error.player_not_found"));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(getOnlinePlayers(), args[0]) : null;
    }

    @Override
    public Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
