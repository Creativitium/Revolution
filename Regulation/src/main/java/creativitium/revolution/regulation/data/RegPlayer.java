package creativitium.revolution.regulation.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegPlayer
{
    private int brokenBlocks = 0;
    private int chat = 0;
    private int commands = 0;

    public void decrement()
    {
        brokenBlocks = 0;
        chat = Math.max(chat - 5, 0);
        commands = Math.max(commands - 5, 0);
    }

    public void reset()
    {
        this.brokenBlocks = 0;
        this.chat = 0;
        this.commands = 0;
    }
}
