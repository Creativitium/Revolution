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
    @Getter
    private final List<Plugin> externalPlugins = new ArrayList<>();

    @Override
    public void onStart()
    {
        messages.clear();
        prefixes.clear();

        // Load the base messages first
        if (!externalPlugins.contains(base))
        {
            externalPlugins.add(base);
        }

        externalPlugins.forEach(this::importFrom);
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

        if (!prefixesFile.exists() && plugin.getResource("prefixes.json") != null)
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
                int before = prefixes.size();
                prefixes.putAll(gson.fromJson(Files.newBufferedReader(prefixesFile.toPath(), StandardCharsets.UTF_8),
                        new TypeToken<Map<String, String>>() {}.getType()));
                Foundation.getSlf4jLogger().info("{} prefixes loaded from plugin {}", prefixes.size() - before, plugin.getName());
            }
            catch (Throwable ex)
            {
                Foundation.getSlf4jLogger().info("Failed to load prefixes file", ex);
            }
        }

        if (!messagesFile.exists() && plugin.getResource("messages.json") != null)
        {
            try
            {
                Files.copy(Objects.requireNonNull(plugin.getResource("messages.json")), messagesFile.toPath());
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
                int before = messages.size();
                messages.putAll(gson.fromJson(Files.newBufferedReader(messagesFile.toPath(), StandardCharsets.UTF_8),
                        new TypeToken<Map<String, String>>() {}.getType()));
                Foundation.getSlf4jLogger().info("{} messages loaded from plugin {}", messages.size() - before, plugin.getName());
            }
            catch (Throwable ex)
            {
                Foundation.getSlf4jLogger().info("Failed to load messages file", ex);
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
        resolvers.addAll(getAllPrefixes());
        return MM.getMiniMessageAll().deserialize(getRawMessage(message), resolvers.toArray(new TagResolver[0]));
    }

    /**
     * Get a list of all known prefixes as TagResolvers.
     * @return  A List of TagResolvers consisting of all the prefixes,
     */
    public List<TagResolver.Single> getAllPrefixes()
    {
        return prefixes.entrySet().stream().map(entry -> Placeholder.parsed(entry.getKey(), entry.getValue())).toList();
    }
}
