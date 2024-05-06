package creativitium.revolution.regulation.commands;

import com.google.common.collect.Lists;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.regulation.Regulation;
import creativitium.revolution.regulation.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@CommandParameters(name = "toggle",
        description = "Toggle certain gameplay mechanics.",
        usage = "/toggle <option>",
        permission = "regulation.command.toggle",
        source = SourceType.BOTH)
public class Command_toggle extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        final World world = SourceType.ONLY_IN_GAME.matchesSourceType(sender) ? ((Player) sender).getWorld() : Bukkit.getWorlds().get(0);

        if (args.length == 0)
        {
            listToggles(sender, world, 1);
            return true;
        }

        Setting.findSetting(args[0]).filter(setting -> setting.getType().equals(boolean.class)).ifPresentOrElse(setting ->
        {
            setting.setBoolean(world, !setting.getBoolean(world));
            msg(sender, "regulation.command.toggle." + (setting.getBoolean(world) ? "enabled" : "disabled") + "." + (setting.isPlural() ? "plural" : "singular"),
                    Placeholder.component("toggle", Foundation.getInstance().getMessageService().getMessage(setting.getName())));
        }, () ->
        {
            int page = 1;

            try
            {
                page = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException ignored)
            {
            }

            listToggles(sender, world, page);
        });

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 1) return null;
        return match(Arrays.stream(Setting.values()).filter(setting -> setting.getType().equals(boolean.class)).map(setting -> setting.name().toLowerCase()).toList(), args[0]);
    }

    private void listToggles(CommandSender sender, World world, int page)
    {
        msg(sender, "regulation.command.toggle.list");
        final List<List<Setting>> paginated =  Lists.partition(Arrays.stream(Setting.values()).filter(setting ->
                setting.getType().equals(boolean.class)).toList(), 8);
        page = Math.max(Math.min(page, paginated.size()), 1);

        paginated.get(page - 1).forEach(setting ->
        {
            final Component name = getMessage(setting.getName())
                    .color(setting.getBoolean(world) ? NamedTextColor.DARK_GREEN : NamedTextColor.DARK_RED);
            final Component description = getMessage(setting.getDescription())
                    .color(setting.getBoolean(world) ? NamedTextColor.GREEN : NamedTextColor.RED);

            msg(sender, "regulation.command.toggle.list.format", Placeholder.component("name", name),
                    Placeholder.component("description", description));
        });

        final Component previous = Component.text("<<").decorate(TextDecoration.BOLD);
        final Component next = Component.text(">>").decorate(TextDecoration.BOLD);

        msg(sender, "regulation.command.toggle.list.footer",
                Placeholder.unparsed("page", String.valueOf(page)),
                Placeholder.unparsed("pages", String.valueOf(paginated.size())),
                Placeholder.component("prev", page == 1 ? previous.color(NamedTextColor.DARK_GRAY) :
                        previous.color(NamedTextColor.WHITE).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/toggle " + (page - 1)))),
                Placeholder.component("next", page >= paginated.size() ? next.color(NamedTextColor.DARK_GRAY) :
                        next.color(NamedTextColor.WHITE).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/toggle " + (page + 1)))));
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Regulation.getInstance();
    }
}
