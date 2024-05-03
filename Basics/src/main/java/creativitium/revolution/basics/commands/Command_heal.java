package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

@CommandParameters(name = "heal",
        description = "Restore your health back to what it was before.",
        usage = "/heal [player]",
        permission = "basics.command.heal",
        source = SourceType.BOTH)
public class Command_heal extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0 && SourceType.ONLY_CONSOLE.matchesSourceType(sender)) return false;

        if (args.length != 1 && !sender.hasPermission("basics.command.heal.others"))
        {
            msg(sender, "basics.command.heal.no_permission");
            return true;
        }

        final Optional<Player> target = args.length == 0 ? Optional.of(playerSender) : getPlayer(args[0]);

        target.ifPresentOrElse(player ->
        {
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
            player.setFoodLevel(20);
            player.setSaturation(5);
            player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
            player.setFireTicks(0);
            player.setRemainingAir(player.getMaximumAir());

            if (player != playerSender)
            {
                msg(sender, "basics.command.heal.other_healed", Placeholder.unparsed("player", player.getName()));
            }

            msg(player, "basics.command.heal.healed");

        }, () -> msg(sender, "revolution.command.error.player_not_found"));

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
