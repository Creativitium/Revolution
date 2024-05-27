package creativitium.revolution.administration.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
@RequiredArgsConstructor
public class AdminChatEvent extends Event
{
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    //--
    @NotNull
    private final Key source;
    @NotNull
    private final String sender;
    @NotNull
    private final Component prefix;
    @NotNull
    private Component message = Component.empty();

    public AdminChatEvent(@NotNull Key source, @NotNull String sender, @NotNull Component prefix, @NotNull Component message, boolean async)
    {
        super(async);
        //--
        this.source = source;
        this.sender = sender;
        this.prefix = prefix;
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return handlerList;
    }
}
