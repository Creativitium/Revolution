package creativitium.revolution.communication.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class CommunicatorChatEvent extends Event
{
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    //--
    private final Key source;
    private final Component sourcePrefix;
    private final String sender;
    private final Component senderDisplay;
    private final Component senderPrefix;
    private final Component message;

    /*public CommunicatorChatEvent(Key source, Component sourcePrefix, String sender, Component senderDisplay, Component senderPrefix, Component message, boolean async)
    {
        super(async);
    }*/

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return handlerList;
    }
}
