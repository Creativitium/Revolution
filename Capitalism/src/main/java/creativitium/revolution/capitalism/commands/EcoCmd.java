package creativitium.revolution.capitalism.commands;

import creativitium.revolution.capitalism.Capitalism;
import creativitium.revolution.capitalism.data.CPlayer;
import creativitium.revolution.capitalism.data.CPlayerService;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CommandParameters(name = "eco",
        description = "Manage the economy.",
        usage = "/eco <give | take | set> <player> <amount>",
        permission = "capitalism.command.eco", source = SourceType.BOTH)
public class EcoCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 3) return false;

        final Optional<Player> target = getPlayer(args[1]);
        if (target.isPresent())
        {
            final Player player = target.get();
            double amount;
            try
            {
                amount = Double.parseDouble(args[2]);
            }
            catch (NumberFormatException ex)
            {
                msg(sender, "revolution.command.error.invalid_number", Placeholder.unparsed("number", args[2]));
                return true;
            }

            if (Double.isInfinite(amount) || Double.isNaN(amount))
            {
                msg(sender, "capitalism.command.eco.infinity_not_allowed");
                return true;
            }

            if (Math.abs(amount) > 1000000000D)
            {
                msg(sender, "capitalism.command.eco.too_much");
                return true;
            }

            final CPlayerService service = (CPlayerService) Shortcuts.getService(Key.key("capitalism", "primary"));
            final CPlayer data = service.getPlayerData(player.getUniqueId());
            switch (args[0].toLowerCase())
            {
                case "give" ->
                {
                    data.setBalance(data.getBalance() + Math.abs(amount));
                    msg(sender, "capitalism.command.eco.given",
                            Placeholder.parsed("name", player.getName()),
                            Placeholder.unparsed("amount", service.getFormat().format(amount)));
                }
                case "set" ->
                {
                    data.setBalance(amount);
                    msg(sender, "capitalism.command.eco.set",
                            Placeholder.parsed("name", player.getName()),
                            Placeholder.unparsed("amount", service.getFormat().format(amount)));
                }
                case "take" ->
                {
                    data.setBalance(data.getBalance() - Math.abs(amount));
                    msg(sender, "capitalism.command.eco.taken",
                            Placeholder.parsed("name", player.getName()),
                            Placeholder.unparsed("amount", service.getFormat().format(amount)));
                }
                default ->
                {
                    return false;
                }
            }
        }
        else
        {
            msg(sender, "revolution.command.error.player_not_found");
        }

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length > 3 || args.length == 0) return null;

        final List<String> options;
        if (args.length == 1)
        {
            options = List.of("give", "set", "take");
        }
        else if (args.length == 2)
        {
            options = getOnlinePlayers();
        }
        else
        {
            options = new ArrayList<>();
        }

        return match(options, args[args.length - 1]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Capitalism.getInstance();
    }
}
