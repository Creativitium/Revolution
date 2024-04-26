package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Pattern;

@CommandParameters(name = "sethome",
        description = "Sets a location for you to teleport to later on.",
        usage = "/sethome [name]",
        permission = "basics.command.sethome",
        source = SourceType.ONLY_IN_GAME)
public class Command_sethome extends RCommand
{
    private static final Pattern homeRegex = Pattern.compile("^[A-Za-z\\d_]+$");

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        String homeName = args.length == 0 ? "home" : args[0];

        if (homeRegex.matcher(homeName).find())
        {
            ((BPlayer) (Shortcuts.getExternalPlayerService(getPlugin())).getPlayerData(playerSender.getUniqueId()))
                    .getHomes().put(homeName, playerSender.getLocation());
            msg(sender, "basics.command.sethome.set", Placeholder.unparsed("name", homeName));
        }
        else
        {
            msg(sender, "basics.command.sethome.invalid_home_name", Placeholder.unparsed("name", args[0]));
        }

        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
