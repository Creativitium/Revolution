package creativitium.revolution.administration.services;

import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.data.APlayer;
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

public class AdminChatService extends RService
{
    public AdminChatService()
    {
        super(Administration.getInstance());
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

    public void sendAdminChat(CommandSender sender, Component message)
    {
        Component sm = getMsg("administration.components.staffchat",
                Placeholder.unparsed("name", sender.getName()),
                Placeholder.component("prefix", Foundation.getInstance().getVaultHook().getPrefixAsComponent(sender)),
                Placeholder.component("message", message));

        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("administration.components.staffchat"))
                .forEach(player -> player.sendMessage(sm));
        getPlugin().getComponentLogger().info(sm);
    }

    public void sendAdminChat(CommandSender sender, String message)
    {
        sendAdminChat(sender, PlainTextComponentSerializer.plainText().deserialize(message));
    }
}
