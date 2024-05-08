package creativitium.revolution.foundation.commands;

import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.MM;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandParameters(name = "messages",
        description = "Manage the Message Service.",
        usage = "/messages <reload | stats | test <message>>",
        aliases = {"msgsystem"},
        permission = "revolution.command.messages",
        source = SourceType.BOTH)
public class Command_messages extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            return false;
        }

        switch (args[0].toLowerCase())
        {
            case "reload" ->
            {
                foundation.getMessageService().reload();
                msg(sender, "revolution.command.messages.reload");
            }
            case "stats", "statistics" -> msg(sender, "revolution.command.messages.stats",
                    TagResolver.resolver("prefixcount", Tag.inserting(Component.text(foundation.getMessageService().getPrefixes().size()))),
                    TagResolver.resolver("messagescount", Tag.inserting(Component.text(foundation.getMessageService().getMessages().size()))));
            case "test" -> msg(sender, "revolution.command.messages.test", Placeholder.component("output",
                    MM.getMiniMessageAll().deserialize(StringUtils.join(ArrayUtils.subarray(args, 1, args.length), " "),
                            foundation.getMessageService().getAllPrefixes().toArray(new TagResolver.Single[0]))));
            default ->
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return foundation;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return args.length <= 1 ? List.of("reload", "stats", "test") : null;
    }
}