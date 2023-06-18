package creativitium.revolution.messages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import creativitium.revolution.Revolution;
import creativitium.revolution.templates.RService;
import creativitium.revolution.utilities.MM;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class MessageService extends RService
{
    private final File prefixesFile = new File(plugin.getDataFolder(), "prefixes.json");
    private final File messagesFile = new File(plugin.getDataFolder(), "messages.json");
    private final Gson gson = new Gson();
    //--
    @Getter
    private Map<String, String> prefixes = new HashMap<>();
    @Getter
    private Map<String, String> messages = new HashMap<>();

    @Override
    public void onStart()
    {
        load();
    }

    @Override
    public void onStop()
    {
    }

    public void load()
    {
        if (prefixesFile.exists())
        {
            try
            {
                prefixes.clear();
                prefixes = gson.fromJson(Files.newBufferedReader(prefixesFile.toPath(), StandardCharsets.UTF_8),
                        new TypeToken<Map<String, String>>() {}.getType());
                Revolution.getSlf4jLogger().info(prefixes.size() + " prefixes loaded.");
            }
            catch (Throwable ex)
            {
                Revolution.getSlf4jLogger().info("FUCK", ex);
            }
        }

        if (messagesFile.exists())
        {
            try
            {
                messages.clear();
                messages = gson.fromJson(Files.newBufferedReader(messagesFile.toPath(), StandardCharsets.UTF_8),
                        new TypeToken<Map<String, String>>() {}.getType());
                Revolution.getSlf4jLogger().info(messages.size() + " messages loaded.");
            }
            catch (Throwable ex)
            {
                Revolution.getSlf4jLogger().info("FUCK", ex);
            }
        }
    }

    /**
     * Get the untranslated message at a given codename.
     * @param message   The codename of the message you are trying to get
     * @return          The untranslated message itself (or the codename if it doesn't exist)
     */
    public String getRawMessage(String message)
    {
        return messages.getOrDefault(message, message);
    }

    /**
     * Get the message at a given codename.
     * @param message   The codename of the message you are trying to get
     * @return          The message itself (or the codename if it doesn't exist)
     */
    public Component getMessage(String message)
    {
        return getMessage(message, new TagResolver[0]);
    }

    /**
     * Get the message at a given codename.
     * @param message       The codename of the message you are trying to get
     * @param placeholders  The additional placeholders you want to use in the message
     * @return              The message itself (or the codename if it doesn't exist)
     */
    public Component getMessage(String message, TagResolver... placeholders)
    {
        List<TagResolver> resolvers = new ArrayList<>();
        resolvers.addAll(Arrays.stream(placeholders).toList());
        resolvers.addAll(plugin.msg.yourMom());
        return MM.getMiniMessageAll().deserialize(getRawMessage(message), resolvers.toArray(new TagResolver[0]));
    }

    public List<TagResolver.Single> yourMom()
    {
        return prefixes.entrySet().stream().map(entry -> Placeholder.parsed(entry.getKey(), entry.getValue())).toList();
    }

    public void adminChatMessage(CommandSender sender, String message)
    {
        Component sm = getMessage("revolution.components.staffchat.admin",
                Placeholder.unparsed("name", sender.getName()),
                Placeholder.unparsed("message", message));

        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("revolution.components.staffchat.admin"))
                .forEach(player -> player.sendMessage(sm));
        plugin.getComponentLogger().info(sm);
    }
}
