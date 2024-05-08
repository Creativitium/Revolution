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
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@CommandParameters(name = "balance",
        description = "Check your balance.",
        usage = "/balance [player]",
        aliases = {"bal"},
        permission = "capitalism.command.balance",
        source = SourceType.BOTH)
public class Command_balance extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)) return false;

        final Optional<Player> player = args.length == 0 ? Optional.of(playerSender) : getPlayer(args[0]);
        player.ifPresentOrElse(target ->
        {
            final CPlayerService service = (CPlayerService) Shortcuts.getService(Key.key("capitalism", "primary"));
            final CPlayer data = service.getPlayerData(target.getUniqueId());

            msg(sender, "capitalism.command.balance." + (target != playerSender ? "other" : "you"),
                    Placeholder.unparsed("name", target.getName()),
                    Placeholder.parsed("amount", service.getFormat().format(service.getPlayerData(target.getUniqueId()).getBalance())));
        }, () -> msg(sender, "revolution.command.error.player_not_found"));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 1) return null;

        return match(getOnlinePlayers(), args[0]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Capitalism.getInstance();
    }
}
