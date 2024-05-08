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

import java.util.List;

@CommandParameters(name = "pay",
        description = "Pay someone money from your own account.",
        usage = "/pay <player> <amount>",
        permission = "capitalism.command.pay",
        source = SourceType.ONLY_IN_GAME)
public class Command_pay extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 2) return false;

        getPlayer(args[0]).ifPresentOrElse(player ->
        {
            double amount;
            try
            {
                amount = Double.parseDouble(args[1]);
            }
            catch (NumberFormatException ex)
            {
                msg(sender, "revolution.command.error.invalid_number", Placeholder.unparsed("number", args[1]));
                return;
            }

            if (amount < 0)
            {
                msg(sender, "capitalism.command.pay.negative_not_allowed");
                return;
            }

            final CPlayerService service = (CPlayerService) Shortcuts.getService(Key.key("capitalism", "primary"));
            final CPlayer targetData = service.getPlayerData(player.getUniqueId());
            final CPlayer senderData = service.getPlayerData(playerSender.getUniqueId());

            if (senderData.getBalance() < amount)
            {
                msg(sender, "capitalism.command.pay.cannot_afford");
                return;
            }

            senderData.setBalance(senderData.getBalance() - amount);
            targetData.setBalance(targetData.getBalance() + amount);

            msg(player, "capitalism.command.pay.received",
                    Placeholder.parsed("name", sender.getName()),
                    Placeholder.component("display", playerSender.displayName()),
                    Placeholder.unparsed("amount", service.getFormat().format(amount)));
            msg(sender, "capitalism.command.pay.sent",
                    Placeholder.parsed("name", player.getName()),
                    Placeholder.component("display", player.displayName()),
                    Placeholder.unparsed("amount", service.getFormat().format(amount)));
        }, () -> msg(sender, "revolution.command.error.player_not_found"));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length == 1 ? match(getOnlinePlayers(), args[0]) : null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Capitalism.getInstance();
    }
}
