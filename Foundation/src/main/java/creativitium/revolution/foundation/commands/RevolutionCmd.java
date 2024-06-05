/*
 * Copyright (C) 2024 videogamesm12
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

        msg(sender, "foundation.command.revolution.info.build_info", Placeholder.component("plugins",
                Component.join(JoinConfiguration.commas(true),
                        foundation.getMessageService().getExternalPlugins().stream().map(pl ->
                        {
                            final Optional<BuildVersion> buildVersion = BuildVersion.getVersion(pl);

                            return getMessage("foundation.command.revolution.info.build_info.plugin",
                                    TagResolver.resolver("status_color", Tag.styling(text -> text.color(pl.isEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED))),
                                    Placeholder.parsed("name", pl.getName()),
                                    Placeholder.parsed("stringified_build_info", buildVersion.isPresent() ? buildVersion.get().toString() : ""),
                                    Placeholder.component("formatted_build_info", buildVersion.isPresent() ? getMessage("foundation.command.revolution.info.build_info.format", buildVersion.get().toPlaceholders()) : getMessage("foundation.command.revolution.info.build_info.unable_to_find_build_metadata")));
                        }).toList())));

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return foundation;
    }
}
