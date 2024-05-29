package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@CommandParameters(name = "speed",
        description = "Change how fast you go.",
        usage = "/speed [walk | fly] <1-10>",
        permission = "basics.command.speed",
        source = SourceType.ONLY_IN_GAME)
public class Command_speed extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 || args.length > 2) return false;

        float defaultSpeed = playerSender.isFlying() ? 0.1f : 0.2f;
        float userSpeed = Math.min(Float.parseFloat(args.length == 1 ? args[0] : args[1]), 10);
        float actualSpeed = (userSpeed < 1f ? userSpeed * defaultSpeed : (((userSpeed - 1) / 9) * (1f - defaultSpeed)) + defaultSpeed);

        boolean whichToAffect;

        if (args.length == 1)
        {
            whichToAffect = playerSender.isFlying();
        }
        else
        {
            switch (args[0].toLowerCase())
            {
                case "fly", "flight" -> whichToAffect = true;
                case "walk", "ground" -> whichToAffect = false;
                default ->
                {
                    return false;
                }
            };
        }

        if (whichToAffect)
        {
            playerSender.setFlySpeed(actualSpeed);
            msg(sender, "basics.command.speed.flight_speed_set", Placeholder.unparsed("number", String.valueOf(userSpeed)));
        }
        else
        {
            playerSender.setWalkSpeed(actualSpeed);
            msg(sender, "basics.command.speed.walk_speed_set", Placeholder.unparsed("number", String.valueOf(userSpeed)));
        }

        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 || args.length > 2) return null;

        if (args.length == 1)
        {
            final List<String> possibilities = new ArrayList<>();
            possibilities.addAll(IntStream.range(1, 11).mapToObj(String::valueOf).toList());
            possibilities.addAll(List.of("fly", "walk"));
            return match(possibilities, args[0]);
        }
        else
        {
            if (List.of("fly", "flight", "walk", "ground").contains(args[0].toLowerCase()))
            {
                return match(IntStream.range(1, 11).mapToObj(String::valueOf).toList(), args[1]);
            }
        }

        return null;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
