package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@CommandParameters(name = "gamemode",
        description = "Change your gamemode.",
        usage = "/gamemode <mode> [player]",
        aliases = {"gm"},
        permission = "basics.command.gamemode",
        source = SourceType.BOTH)
public class Command_gamemode extends RCommand
{
    final GameMode mode;

    public Command_gamemode()
    {
        super();
        this.mode = null;
    }

    public Command_gamemode(GameMode gameMode, short abbreviationLength)
    {
        super(gameMode.name().toLowerCase(),
                "Sets your gamemode to " + gameMode.name().toLowerCase() + ".",
                "/" + gameMode.name().toLowerCase() + " [player]",
                new String[]{"gm" + gameMode.name().substring(0, Math.min(gameMode.name().length(), abbreviationLength)).toLowerCase()},
                "basics.command.gamemode." + gameMode.name().toLowerCase(),
                SourceType.BOTH);
        this.mode = gameMode;
    }

    public Command_gamemode(GameMode mode)
    {
        this(mode, (short) 1);
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        GameMode mode;

        // General /gamemode command, we'll have to get it ourselves
        if (this.mode == null)
        {
            if (args.length == 0) return false;
            final String[] argz = args;

            final Optional<GameMode> possibility = Arrays.stream(GameMode.values()).filter(m ->
                    m.name().toLowerCase().startsWith(argz[0].toLowerCase())
                    || m.name().equalsIgnoreCase(argz[0])).findAny();

            // Let's see if a gamemode starts with the argument given
            if (possibility.isPresent())
            {
                mode = possibility.get();
            }
            // Apparently not, how about we try parsing it as an integer?
            else
            {
                try
                {
                    mode = GameMode.getByValue(Integer.parseInt(argz[0].trim()));
                }
                catch (NumberFormatException ignored)
                {
                    mode = null;
                }
            }

            // Well, we tried
            if (mode == null)
            {
                msg(sender, "basics.command.gamemode.invalid_mode", Placeholder.unparsed("mode", argz[0]));
                return true;
            }

            // Galaxy brain shit right here
            args = ArrayUtils.remove(args, 0);
        }
        // Gamemode-specific command
        else
        {
            mode = this.mode;
        }

        // Console is required to provide a player name
        if (args.length == 0 && SourceType.ONLY_CONSOLE.matchesSourceType(sender))
        {
            return false;
        }

        if (!sender.hasPermission("basics.command.gamemode.others") && args.length >= 1)
        {
            msg(sender, "basics.command.gamemode.no_permission_others");
            return true;
        }

        if (!sender.hasPermission("basics.command.gamemode." + mode.name().toLowerCase()))
        {
            msg(sender, "basics.command.gamemode.no_permission",
                    Placeholder.unparsed("mode", StringUtils.capitalize(mode.name().toLowerCase()) + " Mode"),
                    Placeholder.component("translated", Component.translatable(mode.translationKey())));
            return true;
        }

        final Optional<Player> target = args.length == 0 ? Optional.ofNullable(playerSender) : getPlayer(args[0]);
        final GameMode outcomeMode = mode;

        target.ifPresentOrElse(player ->
        {
            player.setGameMode(outcomeMode);

            if (playerSender != player)
            {
                msg(player, "basics.command.gamemode.player_set_your_gamemode",
                        Placeholder.unparsed("player", sender.getName()),
                        Placeholder.unparsed("mode", StringUtils.capitalize(outcomeMode.name().toLowerCase()) + " Mode"),
                        Placeholder.component("translated", Component.translatable(outcomeMode.translationKey())));

                msg(sender, "basics.command.gamemode.set_other",
                        Placeholder.unparsed("player", player.getName()),
                        Placeholder.unparsed("mode", StringUtils.capitalize(outcomeMode.name().toLowerCase()) + " Mode"),
                        Placeholder.component("translated", Component.translatable(outcomeMode.translationKey())));
            }
            else
            {
                msg(sender, "basics.command.gamemode.set",
                        Placeholder.unparsed("mode", StringUtils.capitalize(outcomeMode.name().toLowerCase()) + " Mode"),
                        Placeholder.component("translated", Component.translatable(outcomeMode.translationKey())));
            }
        }, () -> msg(sender, "revolution.command.error.player_not_found"));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (this.mode == null && args.length == 1)
        {
            return match(Arrays.stream(GameMode.values()).map(mode -> mode.name().toLowerCase()).filter(mode -> sender.hasPermission("basics.command.gamemode." + mode)).toList(), args[0]);
        }
        else if (this.mode != null && args.length == 1 || this.mode == null && args.length == 2 && sender.hasPermission("basics.command.gamemode.others"))
        {
            return match(getOnlinePlayers(), args[args.length - 1]);
        }
        else
        {
            return null;
        }
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}