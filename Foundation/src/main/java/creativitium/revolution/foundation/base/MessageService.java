package creativitium.revolution.foundation.base;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.foundation.utilities.MM;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class MessageService extends RService
{
    private final Gson gson = new Gson();
    //--
    @Getter
    private final Map<String, String> prefixes = new HashMap<>();
    @Getter
    private final Map<String, String> messages = new HashMap<>();
    //--
    private final List<Plugin> externalPlugins = new ArrayList<>();

    @Override
    public void onStart()
    {
        messages.clear();

        // Load the base messages first
        if (!externalPlugins.contains(base))
        {
            externalPlugins.add(base);
        }

        externalPlugins.forEach(this::importFrom);
    }

    @Override
    public void onStop()
    {
    }

    @Override
    public Plugin getPlugin()
    {
        return Foundation.getInstance();
    }

    public void importFrom(Plugin plugin)
    {
        if (!externalPlugins.contains(plugin))
        {
            externalPlugins.add(plugin);
        }

        // Let's make sure this gets created first and foremost
        if (!plugin.getDataFolder().exists())
        {
            plugin.getDataFolder().mkdirs();
        }

        final File prefixesFile = new File(plugin.getDataFolder(), "prefixes.json");
        final File messagesFile = new File(plugin.getDataFolder(), "messages.json");

        if (!prefixesFile.exists())
        {
            try
            {
                Files.copy(Objects.requireNonNull(plugin.getResource("prefixes.json")), prefixesFile.toPath());
            }
            catch (Throwable ex)
            {
                Foundation.getSlf4jLogger().error("Failed to copy prefixes file", ex);
            }
        }

        if (prefixesFile.exists())
        {
            try
            {
                prefixes.putAll(gson.fromJson(Files.newBufferedReader(prefixesFile.toPath(), StandardCharsets.UTF_8),
                        new TypeToken<Map<String, String>>()
                        {
                        }.getType()));

                Foundation.getSlf4jLogger().info(prefixes.size() + " prefixes loaded from plugin " + plugin.getName());
            }
            catch (Throwable ex)
            {
                Foundation.getSlf4jLogger().info("FUCK", ex);
            }
        }

        if (!messagesFile.exists())
        {
            try
            {
                Files.copy(Objects.requireNonNull(getPlugin().getResource("messages.json")), messagesFile.toPath());
            }
            catch (Throwable ex)
            {
                Foundation.getSlf4jLogger().error("Failed to copy messages file", ex);
            }
        }

        if (messagesFile.exists())
        {
            try
            {
                messages.putAll(gson.fromJson(Files.newBufferedReader(messagesFile.toPath(), StandardCharsets.UTF_8),
                        new TypeToken<Map<String, String>>() {}.getType()));
                Foundation.getSlf4jLogger().info(messages.size() + " messages loaded from plugin " + plugin.getName());
            }
            catch (Throwable ex)
            {
                Foundation.getSlf4jLogger().info("FUCK", ex);
            }
        }
    }

    public void reload()
    {
        onStart();
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
        resolvers.addAll(yourMom());
        return MM.getMiniMessageAll().deserialize(getRawMessage(message), resolvers.toArray(new TagResolver[0]));
    }

    public List<TagResolver.Single> yourMom()
    {
        return prefixes.entrySet().stream().map(entry -> Placeholder.parsed(entry.getKey(), entry.getValue())).toList();
    }
}
