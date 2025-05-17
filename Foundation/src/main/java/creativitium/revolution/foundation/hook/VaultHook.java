package creativitium.revolution.foundation.hook;

import creativitium.revolution.foundation.templates.RService;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

@Getter
public class VaultHook extends RService
{
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
    //--
    private Chat chat = null;

    @Override
    public void onStart()
    {
        if (!setupChat())
        {
            base.getSLF4JLogger().warn("Chat was not setup!");
        }
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);

        if (rsp != null)
        {
            chat = rsp.getProvider();
        }

        return chat != null;
    }

    public String getPrimaryGroup(CommandSender sender)
    {
        if (chat == null)
        {
            return "";
        }

        if (sender instanceof Player player)
        {
            return chat.getPrimaryGroup(player);
        }
        else if (sender instanceof ConsoleCommandSender)
        {
            return "CONSOLE";
        }
        else if (sender instanceof RemoteConsoleCommandSender)
        {
            return "RCON";
        }
        else
        {
            return "";
        }
    }

    public String getPrefix(CommandSender sender)
    {
        if (chat == null)
        {
            return "";
        }

        if (sender instanceof Player player)
        {
            return chat.getPlayerPrefix(player);
        }
        else if (sender instanceof ConsoleCommandSender)
        {
            return "&8[&5CONSOLE&8]";
        }
        else if (sender instanceof RemoteConsoleCommandSender)
        {
            return "&8[&5RCON&8]";
        }
        else
        {
            return "";
        }
    }

    public Component getPrefixAsComponent(CommandSender player)
    {
        return legacy.deserialize(getPrefix(player));
    }

    public Component getPrimaryGroupAsComponent(CommandSender player)
    {
        return legacy.deserialize(getPrimaryGroup(player));
    }
}
