package creativitium.revolution.basics.command;

import com.google.common.collect.Lists;
import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CustomCommand extends RCommand
{
    private final CommandMeta meta;

    public CustomCommand(CommandMeta meta)
    {
        super(meta.getName(), meta.getDescription(), meta.getUsage(), meta.getAliases().toArray(new String[0]),
                meta.getPermission(), meta.getSourceType());

        this.meta = meta;
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final List<List<String>> partitioned = Lists.partition(meta.getMessages(), 8);
        final int page = args.length == 0 ? 1 : Math.max(Math.min(Integer.parseInt(args[0]), partitioned.size()), 1);
        if (meta.isHeaderEnabled())
        {
            msg(sender, "basics.custom_command.header",
                    Placeholder.unparsed("name", StringUtils.capitalise(getName().toLowerCase())),
                    Placeholder.unparsed("page", String.valueOf(page)),
                    Placeholder.unparsed("pages", String.valueOf(partitioned.size())));
        }

        partitioned.get(page - 1).forEach(message -> msg(sender, message));

        if (meta.isFooterEnabled())
        {
            final Component previous = Component.text("<<").decorate(TextDecoration.BOLD);
            final Component next = Component.text(">>").decorate(TextDecoration.BOLD);

            msg(sender, "basics.custom_command.footer",
                    Placeholder.component("prev", page == 1 ? previous.color(NamedTextColor.DARK_GRAY) :
                            previous.color(NamedTextColor.WHITE).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/" + getName() + " " + (page - 1)))),
                    Placeholder.component("next", page >= partitioned.size() ? next.color(NamedTextColor.DARK_GRAY) :
                            next.color(NamedTextColor.WHITE).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/" + getName() + " " + (page + 1)))),
                    Placeholder.unparsed("page", String.valueOf(page)),
                    Placeholder.unparsed("pages", String.valueOf(partitioned.size())));
        }
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }

    @Builder
    @Getter
    public static class CommandMeta
    {
        private String name;
        private String description;
        private String usage;
        @Builder.Default
        private List<String> aliases = new ArrayList<>();
        private String permission;
        @Builder.Default
        private SourceType sourceType = SourceType.BOTH;
        private boolean headerEnabled;
        private boolean footerEnabled;
        private List<String> messages;
    }
}
