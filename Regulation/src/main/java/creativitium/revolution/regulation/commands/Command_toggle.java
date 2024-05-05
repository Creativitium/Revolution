package creativitium.revolution.regulation.commands;

import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.regulation.Regulation;
import creativitium.revolution.regulation.Setting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            listToggles(sender, world);
            return true;
        }

        Setting.findSetting(args[0]).filter(setting -> setting.getType().equals(boolean.class)).ifPresentOrElse(setting ->
        {
            setting.setBoolean(world, !setting.getBoolean(world));
            msg(sender, "regulation.command.toggle." + (setting.getBoolean(world) ? "enabled" : "disabled") + "." + (setting.isPlural() ? "plural" : "singular"),
                    Placeholder.component("toggle", Foundation.getInstance().getMessageService().getMessage(setting.getName())));
        }, () -> listToggles(sender, world));

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 1) return null;
        return match(Arrays.stream(Setting.values()).filter(setting -> setting.getType().equals(boolean.class)).map(setting -> setting.name().toLowerCase()).toList(), args[0]);
    }

    private void listToggles(CommandSender sender, World world)
    {
        msg(sender, "regulation.command.toggle.list");
        Arrays.stream(Setting.values()).filter(setting -> setting.getType().equals(boolean.class)).toList().forEach(setting ->
        {
            final Component name = Foundation.getInstance().getMessageService().getMessage(setting.getName())
                    .color(setting.getBoolean(world) ? NamedTextColor.DARK_GREEN : NamedTextColor.DARK_RED);
            final Component description = Foundation.getInstance().getMessageService().getMessage(setting.getDescription())
                    .color(setting.getBoolean(world) ? NamedTextColor.GREEN : NamedTextColor.RED);

            msg(sender, "regulation.command.toggle.list.format", Placeholder.component("name", name),
                    Placeholder.component("description", description));
        });
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Regulation.getInstance();
    }
}
