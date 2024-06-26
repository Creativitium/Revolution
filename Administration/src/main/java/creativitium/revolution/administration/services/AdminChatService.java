package creativitium.revolution.administration.services;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.data.APlayer;
import creativitium.revolution.administration.event.AdminChatEvent;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.foundation.utilities.Shortcuts;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class AdminChatService extends RService
{
    public AdminChatService()
    {
        super(Administration.getInstance());
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event)
    {
        Player player = event.getPlayer();

        if (player.hasPermission("administration.components.staffchat"))
        {
            APlayer data = (APlayer) Shortcuts.getService(Key.key("administration", "admin_preferences")).getPlayerData(player.getUniqueId());
            if (data.isAdminChatEnabled())
            {
                sendAdminChat(player, event.message());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAdminChat(AdminChatEvent event)
    {
        final Component sm = getMsg("administration.components.staffchat",
                Placeholder.unparsed("name", event.getSender()),
                Placeholder.component("prefix", event.getPrefix()),
                Placeholder.component("message", event.getMessage()));

        Audience.audience(Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("administration.components.staffchat")).toList())
                .sendMessage(sm);

        getPlugin().getComponentLogger().info(sm);
    }

    public void sendAdminChat(@NotNull Key source, @NotNull String sender, @NotNull Component prefix, @NotNull Component message, boolean async)
    {
        new AdminChatEvent(source, sender, prefix, message, async).callEvent();
    }

    public void sendAdminChat(CommandSender sender, Component message)
    {
        sendAdminChat(Key.key(Key.MINECRAFT_NAMESPACE, "chat"),
                sender.getName(),
                Foundation.getInstance().getVaultHook().getPrefixAsComponent(sender),
                message,
                true);
    }

    public void sendAdminChat(CommandSender sender, String message)
    {
        sendAdminChat(Key.key(Key.MINECRAFT_NAMESPACE, "command"),
                sender.getName(),
                Foundation.getInstance().getVaultHook().getPrefixAsComponent(sender),
                PlainTextComponentSerializer.plainText().deserialize(message),
                false);
    }
}
