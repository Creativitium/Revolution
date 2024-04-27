package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.basics.data.BPlayerService;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

@CommandParameters(name = "realname",
        description = "Unmask the user behind a nickname.",
        usage = "/realname <nickname>",
        permission = "basics.command.realname",
        source = SourceType.BOTH)
public class Command_realname extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;
        final String nickname = args[0];
        final PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        final BPlayerService service = (BPlayerService) Shortcuts .getExternalPlayerService(Basics.getInstance());

        Bukkit.getOnlinePlayers().stream().filter(p -> service.getPlayerData(p.getUniqueId()).getNickname() != null
                && serializer.serialize(service.getPlayerData(p.getUniqueId()).getNickname()).equalsIgnoreCase(nickname))
                .findAny().ifPresentOrElse(player -> msg(sender, "basics.command.realname.player_is",
                        Placeholder.component("nickname", service.getPlayerData(player.getUniqueId()).getNickname()),
                        Placeholder.unparsed("username", player.getName())),
                () -> msg(sender, "basics.command.realname.nobody"));
        return true;
    }

    @Override
    public Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
