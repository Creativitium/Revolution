package creativitium.revolution.foundation.hook;

import creativitium.revolution.foundation.templates.RService;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

@Getter
public class VaultHook extends RService
{
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
    //--
    private Chat chat = null;
    private Economy economy = null;

    @Override
    public void onStart()
    {
        if (!setupEconomy())
        {
            base.getSLF4JLogger().warn("Economy was not setup!");
        }

        if (!setupChat())
        {
            base.getSLF4JLogger().warn("Chat was not setup!");
        }
    }

    @Override
    public void onStop()
    {
    }

    private boolean setupEconomy()
    {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
        {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
        {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupChat()
    {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public String getPrefix(CommandSender sender)
    {
        if (chat == null)
        {
            return "";
        }

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
