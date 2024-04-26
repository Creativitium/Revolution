package creativitium.revolution.basics.services;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.Warp;
import creativitium.revolution.foundation.templates.RService;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarpsService extends RService
{
    private Map<String, Warp> warps = new HashMap<>();
    private final File warpsFile = new File(getPlugin().getDataFolder(), "warps.yml");

    public WarpsService()
    {
        super(Basics.getInstance());
    }

    @Override
    public void onStart()
    {
        load();
    }

    @Override
    public void onStop()
    {
        save();
    }

    @EventHandler
    public void onSignModified(SignChangeEvent event)
    {
        final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();
        if (event.line(0) != null && event.line(1) != null && plainText.serialize(Objects.requireNonNull(event.line(0))).equalsIgnoreCase("[Warp]"))
        {
            event.line(0, Component.text("[Warp]").color(warpExists(plainText.serialize(event.line(1))) ? NamedTextColor.DARK_BLUE : NamedTextColor.DARK_RED));
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null
                && event.getClickedBlock().getState() instanceof Sign sign)
        {
            final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();

            if (plainText.serialize(sign.line(0)).equalsIgnoreCase("[Warp]"))
            {
                event.setCancelled(true);
                final Player player = event.getPlayer();
                final String warpName = plainText.serialize(sign.line(1));
                getWarp(warpName).ifPresentOrElse(warp ->
                {
                    if (warp.getPosition() == null)
                    {
                        player.sendMessage(base.getMessageService().getMessage("basics.command.warp.corrupted"));
                        removeWarp(warpName);
                        return;
                    }

                    player.sendMessage(base.getMessageService().getMessage("basics.command.warp.teleporting",
                            Placeholder.unparsed("warp", warpName)));
                    player.teleportAsync(warp.getPosition(), PlayerTeleportEvent.TeleportCause.COMMAND);
                }, () -> player.sendMessage(base.getMessageService().getMessage("basics.command.warp.not_found")));
            }
        }
    }

    public void load()
    {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(warpsFile);
        configuration.getKeys(false).forEach(key -> {
            try
            {
                warps.put(key, configuration.getSerializable(key, Warp.class));
            }
            catch (Throwable ex)
            {
                getPlugin().getSLF4JLogger().warn("An error occurred while attempting to load warp {}", key, ex);
                warps.remove(key);
            }
        });
    }

    public void save()
    {
        final YamlConfiguration configuration = new YamlConfiguration();
        warps.forEach(configuration::set);
        try
        {
            configuration.save(warpsFile);
        }
        catch (Exception ex)
        {
            Basics.getInstance().getSLF4JLogger().error("Failed to save warps file!", ex);
        }
    }

    public int getWarpCount()
    {
        return warps.size();
    }

    public List<String> getWarpNames()
    {
        return warps.keySet().stream().sorted(String::compareTo).toList();
    }

    public List<String> getWarpNamesBy(UUID uuid)
    {
        return warps.entrySet().stream().filter(entry -> entry.getValue().getBy().equals(uuid)).map(Map.Entry::getKey).sorted(String::compareTo).toList();
    }

    public Optional<Warp> getWarp(String name)
    {
        return Optional.ofNullable(warps.get(name));
    }

    public boolean warpExists(String name)
    {
        return warps.containsKey(name);
    }

    public boolean addWarp(String name, Warp warp, Player requester)
    {
        if (warpExists(name))
        {
            if (warps.get(name).getBy() != requester.getUniqueId() && !requester.hasPermission("basics.command.warp.others"))
            {
                return false;
            }

            warps.remove(name);
        }

        warps.put(name, warp);
        return true;
    }

    public void removeWarp(String name)
    {
        warps.remove(name);
    }
}
