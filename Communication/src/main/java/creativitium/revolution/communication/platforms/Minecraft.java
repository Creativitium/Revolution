package creativitium.revolution.communication.platforms;

import creativitium.revolution.communication.boilerplate.Communicator;
import creativitium.revolution.communication.event.CommunicatorChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;

public class Minecraft extends Communicator
{
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @Override
    public void handleBroadcast(Component message)
    {
        // Already handled in-game
    }

    @Override
    public void handleAdminChatMessage(Key source, Component sourcePrefix, String sender, Component prefix, Component message)
    {
        // Already handled in-game
    }

    @Override
    public void handleCommunicatorMessage(CommunicatorChatEvent event)
    {
        final Component message = getMsg("communication.format.platform.minecraft",
                Placeholder.component("platformprefix", event.getSourcePrefix()),
                Placeholder.parsed("user", event.getSender()),
                Placeholder.component("userprefix", event.getSenderPrefix()),
                Placeholder.component("display", event.getSenderDisplay()),
                Placeholder.component("message", event.getMessage()));

        Audience.audience(Bukkit.getOnlinePlayers().stream().toList()).sendMessage(message);
        getPlugin().getComponentLogger().info(message);
    }

    @Override
    public Key getIdentifier()
    {
        return Key.key("communicator", "minecraft");
    }
}
