package creativitium.revolution.foundation.commands;

import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.BuildVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CommandParameters(name = "revolution",
        description = "Display information about Revolution.",
        usage = "/revolution",
        aliases = {"rvl"},
        permission = "foundation.command.revolution",
        source = SourceType.BOTH)
public class RevolutionCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 0) return false;

        msg(sender, "foundation.command.revolution.info.introduction",
                Placeholder.parsed("version", foundation.getDescription().getVersion()));

        final List<Component> plugins = foundation.getMessageService().getExternalPlugins().stream().map(plugin ->
        {
            final Optional<BuildVersion> buildMetadata = BuildVersion.getVersion(plugin);
            final Component formattedBuildMetadata = buildMetadata.isPresent() ? getMessage("foundation.command.revolution.info.build_info.format", buildMetadata.get().getAsPlaceholders().toArray(new TagResolver.Single[]{})) : getMessage("foundation.command.revolution.info.build_info.unable_to_find_build_metadata");
            final String stringBuildInfo = buildMetadata.isPresent() ? buildMetadata.get().toString() : "";

            return getMessage("foundation.command.revolution.info.build_info.plugin",
                    TagResolver.resolver("status_color",
                            Tag.styling(text -> text.color(plugin.isEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED))),
                    Placeholder.parsed("name", plugin.getName()),
                    Placeholder.parsed("build_info", stringBuildInfo),
                    Placeholder.component("formatted_build_info", formattedBuildMetadata));
        }).toList();

        msg(sender, "foundation.command.revolution.info.build_info", Placeholder.component("plugins",
                Component.join(JoinConfiguration.commas(true), plugins)));

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return foundation;
    }
}
