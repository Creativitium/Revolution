package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandParameters(name = "multirun",
        description = "Execute a command multiple times in a row.",
        usage = "/multirun <amount> <command>",
        permission = "administration.command.multirun",
        source = SourceType.BOTH)
public class Command_multirun extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length < 2) return false;
        try
        {
            int amount = Integer.parseInt(args[0]);

            if (getPlugin().getConfig().getStringList("multirun.blacklist").contains(args[1].toLowerCase()))
            {
                msg(sender, "administration.command.multirun.blacklisted");
                return true;
            }

            String command = String.join(" ", ArrayUtils.remove(args, 0));

            // Mmm, let's not be too excessive
            if (amount > getPlugin().getConfig().getInt("multirun.limits.cutoff"))
            {
                msg(sender, "administration.command.multirun.too_excessive", Placeholder.unparsed("amount", String.valueOf(amount)));
                return true;
            }

            // General warning that the amount given is rather excessive otherwise.
            if (amount > getPlugin().getConfig().getInt("multirun.limits.warning"))
            {
                msg(sender, "administration.command.multirun.excessive", Placeholder.unparsed("amount", String.valueOf(amount)));
            }

            msg(sender, "administration.command.multirun.executing", Placeholder.unparsed("command", "/" + command),
                    Placeholder.unparsed("amount", String.valueOf(amount)));

            final String placeholder = getPlugin().getConfig().getString("multirun.placeholder", "@n");

            for (int i = 1; i < amount + 1; i++)
            {
                if (SourceType.ONLY_IN_GAME.matchesSourceType(sender))
                    playerSender.performCommand(command.replaceAll(placeholder, String.valueOf(i)));
                else
                    Bukkit.dispatchCommand(sender, command.replaceAll(placeholder, String.valueOf(i)));
            }
        }
        catch (NumberFormatException ex)
        {
            msg(sender, "revolution.command.error.invalid_number", Placeholder.unparsed("number", args[0]));
        }

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
