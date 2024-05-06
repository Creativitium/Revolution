package creativitium.revolution.administration.hooks;

import creativitium.revolution.foundation.templates.RService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook extends RService
{
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
    private net.milkbowl.vault.chat.Chat chat = null;

    @Override
    public void onStart()
    {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
    }

    @Override
    public void onStop()
    {

    }

    public String getPrefix(CommandSender sender)
    {
        if (sender instanceof Player player)
        {
            return chat.getPrimaryGroup(player);
        }
        else
        {
            return "&8[&5CONSOLE&8]";
        }
    }

    public Component getPrefixAsComponent(CommandSender player)
    {
        return legacy.deserialize(getPrefix(player));
    }
}
