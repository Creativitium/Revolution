package creativitium.revolution.regulation.services;

import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.regulation.Regulation;
import creativitium.revolution.regulation.data.RegPlayer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GlobalRegulator extends RService
{
    private final Map<UUID, RegPlayer> data = new HashMap<>();
    //--
    private YamlConfiguration config;
    private ScheduledThreadPoolExecutor executor;

    public GlobalRegulator()
    {
        super(Regulation.getInstance());
    }

    @Override
    public void onStart()
    {
        // Clear what's in memory already if anything
        data.clear();

        // Load the configuration
        this.config = loadConfig();

        // Reset the monitor
        if (executor != null)
        {
            executor.shutdownNow();
        }

        this.executor = new ScheduledThreadPoolExecutor(1);
        this.executor.scheduleAtFixedRate(() -> data.forEach((uuid, regPlayer) -> regPlayer.decrement()),
                0L, 1L, TimeUnit.SECONDS);
    }

    @Override
    public void onStop()
    {
        executor.shutdown();
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event)
    {
        if (!config.getBoolean("ratelimits.chat.enabled")) return;

        final RegPlayer player = getPlayer(event.getPlayer().getUniqueId());

        if (player.getChat() > config.getInt("ratelimits.chat.threshold", 5))
        {
            event.getPlayer().sendMessage(getMsg("regulation.component.ratelimits.chat"));
            event.setCancelled(true);
        }

        player.setChat(player.getChat() + 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        if (!config.getBoolean("ratelimits.commands.enabled")) return;

        final RegPlayer player = getPlayer(event.getPlayer().getUniqueId());

        if (player.getCommands() > config.getInt("ratelimits.commands.threshold", 5))
        {
            event.getPlayer().sendMessage(getMsg("regulation.component.ratelimits.commands"));
            event.setCancelled(true);
        }

        player.setCommands(player.getCommands() + 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (!config.getBoolean("ratelimits.block_break.enabled")) return;

        final RegPlayer player = getPlayer(event.getPlayer().getUniqueId());

        if (player.getBrokenBlocks() > config.getInt("ratelimits.block_break.threshold", 10))
        {
            final Location loc = event.getPlayer().getLocation();

            Bukkit.getOnlinePlayers().stream().filter(pl -> pl.hasPermission("regulator.broadcast.see_alerts")).forEach(pl ->
                    pl.sendMessage(getMsg("regulation.component.ratelimits.block_break.broadcast",
                            Placeholder.parsed("name", event.getPlayer().getName()),
                            Placeholder.parsed("x", String.valueOf(loc.getBlockX())),
                            Placeholder.parsed("y", String.valueOf(loc.getBlockY())),
                            Placeholder.parsed("z", String.valueOf(loc.getBlockZ())),
                            Placeholder.parsed("world", loc.getWorld().getName()))));
            event.getPlayer().kick(getMsg("regulation.component.ratelimits.block_break"));
            event.setCancelled(true);
        }

        player.setBrokenBlocks(player.getBrokenBlocks() + 1);
    }

    private RegPlayer getPlayer(UUID uuid)
    {
        if (!data.containsKey(uuid))
        {
            data.put(uuid, new RegPlayer());
        }

        return data.get(uuid);
    }

    public YamlConfiguration loadConfig()
    {
        final File file = new File(getPlugin().getDataFolder(), "globals.yml");

        if (!getPlugin().getDataFolder().isDirectory())
        {
            getPlugin().getDataFolder().mkdirs();
        }

        if (!file.exists())
        {
            try
            {
                Files.copy(getPlugin().getResource("globals.yml"), file.toPath());
            }
            catch (IOException ex)
            {
                getPlugin().getSLF4JLogger().error("Failed to load global defaults", ex);
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }
}
