package creativitium.revolution.basics.services;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.foundation.utilities.MM;
import creativitium.revolution.foundation.utilities.Shortcuts;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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

    public BasicsService()
    {
        super(Basics.getInstance());
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();
        final BPlayer data = (BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId());

        // Update last known player name
        if (!data.getName().equalsIgnoreCase(player.getName()))
        {
            data.setName(player.getName());
        }

        if (data.getNickname() != null)
        {
            player.displayName(data.getNickname());
        }

        event.joinMessage(base.getMessageService().getMessage("basics.components.join_message",
                Placeholder.component("display", player.displayName()),
                Placeholder.unparsed("username", player.getName())));


        data.setLastOnline(Instant.now().getEpochSecond());
        data.setLastIP(Objects.requireNonNull(event.getPlayer().getAddress()).getAddress().getHostAddress());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        final Player player = event.getPlayer();

        event.quitMessage(base.getMessageService().getMessage("basics.components.leave_message",
                Placeholder.component("display", player.displayName()),
                Placeholder.unparsed("username", player.getName())));

        final BPlayer data = (BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId());
        data.setLoginLocation(event.getPlayer().getLocation());
        data.setLastOnline(Instant.now().getEpochSecond());

        if (!data.getLastIP().equalsIgnoreCase(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress()))
        {
            data.setLastIP(Objects.requireNonNull(event.getPlayer().getAddress()).getAddress().getHostAddress());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        final Player player = event.getPlayer();

        event.deathMessage(base.getMessageService().getMessage("basics.components.death_message",
                Placeholder.component("display", player.displayName()),
                Placeholder.unparsed("username", player.getName())));

        if (player.hasPermission("basics.command.back"))
        {
            ((BPlayer) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(player.getUniqueId())).setLastLocation(player.getLocation());
            player.sendMessage(base.getMessageService().getMessage("basics.general.use_back"));
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
        final BPlayer data = (BPlayer) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(event.getPlayer().getUniqueId());

        event.renderer((source, sourceDisplayName, message, viewer) -> base.getMessageService().getMessage("basics.components.chat",
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
            ((BPlayer) Shortcuts.getExternalPlayerService(Basics.getInstance()).getPlayerData(player.getUniqueId())).setLastLocation(player.getLocation());
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageEvent event)
    {
        if (event.getEntity() instanceof Player player && ((BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() instanceof Player player && ((BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }

        if (event.getDamager() instanceof Player player
                && ((BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId())).isBerserkEnabled()
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
                && ((BPlayer) Shortcuts.getExternalPlayerService(getPlugin()).getPlayerData(player.getUniqueId())).isGodEnabled())
        {
            event.setCancelled(true);
        }
    }
}
