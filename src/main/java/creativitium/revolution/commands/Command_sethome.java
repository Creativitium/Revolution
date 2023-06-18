package creativitium.revolution.commands;

import creativitium.revolution.templates.RCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

@CommandParameters(name = "sethome",
        description = "Sets a location for you to teleport to later on.",
        usage = "/sethome [name]",
        permission = "revolution.command.sethome",
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
            plugin.pls.getPlayerData(playerSender.getUniqueId()).getHomes().put(homeName, playerSender.getLocation());
            msg(sender, "revolution.command.sethome.set", Placeholder.unparsed("name", homeName));
        }
        else
        {
            msg(sender, "revolution.command.sethome.invalid_home_name", Placeholder.unparsed("name", args[0]));
        }

        return true;
    }
}
