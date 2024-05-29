package creativitium.revolution.communication.boilerplate;

import creativitium.revolution.administration.event.AdminChatEvent;
import creativitium.revolution.communication.Communication;
import creativitium.revolution.communication.event.CommunicatorChatEvent;
import creativitium.revolution.foundation.templates.RService;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.BroadcastMessageEvent;

public abstract class Communicator extends RService
{
    public Communicator()
    {
        super(Communication.getInstance());
    }

    /*@EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event)
    {
        handleChatMessage(event.getPlayer().getName(),
                Foundation.getInstance().getVaultHook().getPrefixAsComponent(event.getPlayer()),
                event.message());
    }*/

    @EventHandler
    public final void onAdminChat(AdminChatEvent event)
    {
        if (event.getSource().equals(getIdentifier()))
        {
            return;
        }

        handleAdminChatMessage(event.getSource(),
                getMsg("communication.prefix." + event.getSource().namespace()),
                event.getSender(),
                event.getPrefix(),
                event.getMessage());
    }

    @EventHandler(ignoreCancelled = true)
    public final void onBroadcast(BroadcastMessageEvent event)
    {
        handleBroadcast(event.message());
    }

    @EventHandler
    public final void onExternalCommunicatorChat(CommunicatorChatEvent event)
    {
        if (event.getSource().equals(getIdentifier()))
        {
            return;
        }

        handleCommunicatorMessage(event);
    }

    public final void forwardChat(Key source, String sender, Component display, Component prefix, Component message)
    {
        Bukkit.getScheduler().runTask(getPlugin(), () -> new CommunicatorChatEvent(source,
                getMsg("communication.prefix.platform." + source.value()), sender, display, prefix, message).callEvent());
    }

    /**
     * Send server-wide broadcasts to the platform this communicator is for.
     * @param message   Component
     */
    public abstract void handleBroadcast(Component message);

    /**
     * Send admin chat messages to the platform this communicator is for.
     * @param source        Key
     * @param sourcePrefix  Component
     * @param sender        String
     * @param prefix        Component
     * @param message       Component
     */
    public abstract void handleAdminChatMessage(Key source, Component sourcePrefix, String sender, Component prefix, Component message);

    /**
     * Handle messages from other communicators.
     * @param event CommunicatorChatEvent
     */
    public abstract void handleCommunicatorMessage(CommunicatorChatEvent event);



    /**
     * Perform checks, preferably at startup, before we set up our communicator.
     */
    public void sanityCheck()
    {
    }

    public abstract Key getIdentifier();
}
