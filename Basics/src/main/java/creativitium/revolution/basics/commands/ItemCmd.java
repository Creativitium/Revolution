package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@CommandParameters(name = "item",
        description = "Give yourself items.",
        usage = "/item <type> [amount] [damage] [nbt]",
        aliases = {"i"},
        permission = "basics.command.item",
        source = SourceType.ONLY_IN_GAME)
public class ItemCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length < 1) return false;

        final Material material = Material.matchMaterial(args[0].toLowerCase());
        if (material == null || material.isAir() || !material.isItem())
        {
            msg(sender, "revolution.command.error.item_not_found");
            return true;
        }

        int amount, damage;

        try
        {
            amount = args.length > 1 ? Integer.parseInt(args[1]) : 1;
            damage = args.length > 2 ? Integer.parseInt(args[2]) : 0;
        }
        catch (NumberFormatException ex)
        {
            msg(sender, "revolution.command.error.invalid_number_unknown");
            return true;
        }

        // Ugly hack to make custom item NBT work without relying on the version-locking piece of shit known as NMS
        ItemMeta extraNbt = null;
        if (args.length > 3 && sender.hasPermission("basics.command.item.nbt"))
        {
            try
            {
                extraNbt = Bukkit.getItemFactory().createItemStack(material.getKey().asString()
                        + String.join(" ", ArrayUtils.subarray(args, 3, args.length))).getItemMeta();
            }
            catch (IllegalArgumentException ex)
            {
                msg(sender, "basics.command.give.invalid_nbt");
            }
        }

        final ItemStack stack = new ItemStack(material, Math.min(6400, amount));

        if (extraNbt != null)
            stack.setItemMeta(extraNbt);

        Damageable meta = (Damageable) stack.getItemMeta();
        meta.setDamage(damage);
        stack.setItemMeta(meta);

        msg(sender, "basics.command.item.given",
                Placeholder.unparsed("amount", String.valueOf(amount)),
                Placeholder.unparsed("damage", String.valueOf(damage)),
                Placeholder.unparsed("type", StringUtils.capitalise(material.name().toLowerCase())),
                Placeholder.component("translated_type", Component.translatable(material.translationKey())));

        playerSender.getInventory().addItem(stack);
        return true;
    }

    @Override
    public @Nullable List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length != 1) return null;

        return match(Arrays.stream(Material.values()).filter(material -> !material.isAir() && material.isItem())
                .map(material -> material.getKey().asString()).toList(), args[0]);
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
