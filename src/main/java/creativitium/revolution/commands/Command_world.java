package creativitium.revolution.commands;

import creativitium.revolution.templates.RCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandParameters(name = "world",
        description = "Teleport to other worlds.",
        usage = "/world <world> [player]",
        permission = "revolution.command.world",
        source = SourceType.ONLY_IN_GAME
)
public class Command_world extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            return false;
        }

        final World world = Bukkit.getWorld(StringUtils.join(args, " "));
        if (world == null)
        {
            msg(sender, "revolution.command.world.world_not_found");
            return true;
        }

        return true;
    }
}
