package creativitium.revolution.basics.services;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.foundation.utilities.MM;
import creativitium.revolution.foundation.utilities.Shortcuts;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicsService extends RService
{
    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("(&(?i)[a-fklmnor\\d]{1})");

    private static final List<EntityPotionEffectEvent.Cause> GOD_IMMUNE_EFFECTS = List.of(
            EntityPotionEffectEvent.Cause.POTION_SPLASH,
            EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD,
            EntityPotionEffectEvent.Cause.ARROW,
            EntityPotionEffectEvent.Cause.ATTACK,
            EntityPotionEffectEvent.Cause.FOOD,
            EntityPotionEffectEvent.Cause.WITHER_ROSE,
            EntityPotionEffectEvent.Cause.WARDEN,
            EntityPotionEffectEvent.Cause.PATROL_CAPTAIN,
            EntityPotionEffectEvent.Cause.BEACON,
            EntityPotionEffectEvent.Cause.DOLPHIN
    );

    @Getter
    @Setter
    private Location spawnpoint = null;

    public BasicsService()
    {
        super(Basics.getInstance());
    }

    @Override
    public void onStart()
    {
        loadSpawn();
    }

    @Override
    public void onStop()
    {
    }

    public void loadSpawn()
    {
        final File spawn = new File(getPlugin().getDataFolder(), "spawn.yml");

        if (spawn.exists())
        {
            final FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(spawn);
            spawnpoint = fileConfiguration.getLocation("location");
        }
    }

    public void saveSpawn()
    {
        final File spawn = new File(getPlugin().getDataFolder(), "spawn.yml");
        final YamlConfiguration config = new YamlConfiguration();
        config.set("location", spawnpoint);

        try
        {
            config.save(spawn);
        }
        catch (Exception ex)
        {
            getPlugin().getSLF4JLogger().error("Failed to save spawnpoint data", ex);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final BPlayer data = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId());

        // Update last known player name
        if (!data.getName().equalsIgnoreCase(player.getName()))
        {
            data.setName(player.getName());
        }

        if (data.getNickname() != null)
        {
            player.displayName(data.getNickname());
        }

        if (getPlugin().getConfig().isList("autoexec.onJoin"))
        {
            getPlugin().getConfig().getStringList("autoexec.onJoin").forEach(player::performCommand);
        }

        event.joinMessage(getMsg("basics.components.join_message",
                Placeholder.component("display", player.displayName()),
                Placeholder.component("prefix", base.getVaultHook().getPrefixAsComponent(player)),
                Placeholder.unparsed("username", player.getName())));


        data.setLastOnline(Instant.now().getEpochSecond());
        data.setLastIP(Objects.requireNonNull(event.getPlayer().getAddress()).getAddress().getHostAddress());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        event.quitMessage(getMsg("basics.components.leave_message",
                Placeholder.component("display", player.displayName()),
                Placeholder.component("prefix", base.getVaultHook().getPrefixAsComponent(player)),
                Placeholder.unparsed("username", player.getName())));

        final BPlayer data = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId());
        data.setLoginLocation(event.getPlayer().getLocation());
        data.setLastOnline(Instant.now().getEpochSecond());

        if (data.getLastIP() == null || !data.getLastIP().equalsIgnoreCase(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress()))
        {
            data.setLastIP(Objects.requireNonNull(event.getPlayer().getAddress()).getAddress().getHostAddress());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        final Player player = event.getPlayer();

        event.deathMessage(getMsg("basics.components.death_message",
                Placeholder.component("display", player.displayName()),
                Placeholder.component("prefix", base.getVaultHook().getPrefixAsComponent(player)),
                Placeholder.unparsed("username", player.getName())));

        if (player.hasPermission("basics.command.back"))
        {
            ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).setLastLocation(player.getLocation());
            player.sendMessage(getMsg("basics.general.use_back"));
        }
    }

    @EventHandler
    public void formatPlayerChat(AsyncChatEvent event)
    {
        if (getPlugin().getConfig().getBoolean("formatChatMessages", true));
        {
            final String message = ((TextComponent) event.originalMessage()).content();
            final Matcher useLegacy = AMPERSAND_PATTERN.matcher(message);
            final Component outcome = useLegacy.find() ? LegacyComponentSerializer.legacyAmpersand().deserialize(message)
                    : MM.getNonExploitable().deserialize(message);

            event.message(outcome);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncChatEvent event)
    {
        final BPlayer data = (BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(event.getPlayer().getUniqueId());

        event.renderer((source, sourceDisplayName, message, viewer) -> getMsg("basics.components.chat",
                Placeholder.component("tag", data.getTag() != null ? data.getTag().append(Component.space()) : Component.empty()),
                Placeholder.component("display", event.getPlayer().displayName()),
                Placeholder.component("nickname", data.getNickname() != null ? data.getNickname() : Component.text(event.getPlayer().getName())),
                Placeholder.unparsed("name", event.getPlayer().getName()),
                Placeholder.component("message", message)));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        final Player player = event.getPlayer();

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND && player.hasPermission("basics.command.back"))
        {
            ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).setLastLocation(player.getLocation());
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player player && ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player player && ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }

        if (event.getDamager() instanceof Player player
                && ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).isBerserkEnabled()
                && player.getInventory().getItemInMainHand().getType() == Material.AIR
                && !event.isCancelled())
        {
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, Double.MAX_VALUE);
        }
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event)
    {
        if (event.getEntity() instanceof Player player
                && (GOD_IMMUNE_EFFECTS.contains(event.getCause()))
                && ((BPlayer) Shortcuts.getService(Key.key("basics", "primary")).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        if (getPlugin().getConfig().getBoolean("respawnAtSpawn", true) && spawnpoint != null)
        {
            event.setRespawnLocation(spawnpoint);
        }
    }
}
