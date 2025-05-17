package creativitium.revolution.basics.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.command.SourceType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CustomCommandLoader
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final List<CustomCommand> commands = new ArrayList<>();
    private final File folder;

    public CustomCommandLoader(File folder)
    {
        this.folder = folder;
    }

    public void loadCommands()
    {
        // Unregister existing commands
        commands.forEach(command -> command.unregister(Bukkit.getCommandMap()));
        commands.clear();

        if (folder.exists() && !folder.isDirectory())
        {
            Basics.getInstance().getSLF4JLogger().warn("Failed to register commands as the intended folder isn't a folder!");
            return;
        }

        List<CustomCommand> output = !folder.isDirectory() ? loadDefaults() :
                Arrays.stream(Objects.requireNonNull(folder.listFiles())).filter(file -> file.getName().endsWith(".json"))
                .map(file -> {
                    try
                    {
                        return new CustomCommand(gson.fromJson(new FileReader(file), CustomCommand.CommandMeta.class));
                    }
                    catch (Exception ex)
                    {
                        return null;
                    }
                }).filter(Objects::nonNull).toList();

        output.forEach(command ->
        {
            commands.add(command);
            Bukkit.getCommandMap().register("basics-yml", command);
        });
    }

    public List<CustomCommand> loadDefaults()
    {
        folder.mkdirs();

        final List<CustomCommand> defaults = List.of(
                // /motd
                new CustomCommand(CustomCommand.CommandMeta.builder()
                        .name("motd")
                        .description("View the message of the day.")
                        .usage("/motd")
                        .aliases(List.of("messageoftheday"))
                        .permission("basics.command.motd")
                        .sourceType(SourceType.BOTH)
                        .headerEnabled(false)
                        .footerEnabled(false)
                        .messages(List.of(
                                "<gray>Welcome to <white>Creativitium</white>: <i>Creative, Reimagined</i>.",
                                "<dark_gray>--------------------------------------------------------------------",
                                "<gray>Feel free to explore our multiple worlds where you can build to your heart's content.",
                                "<gray>Make sure to read <white>/rules</white> and also have fun.",
                                "<dark_gray>--------------------------------------------------------------------"))
                        .build()),
                // /rules
                new CustomCommand(CustomCommand.CommandMeta.builder()
                        .name("rules")
                        .description("View the server's rules.")
                        .usage("/rules [page]")
                        .permission("basics.command.rules")
                        .sourceType(SourceType.BOTH)
                        .headerEnabled(true)
                        .footerEnabled(true)
                        .messages(List.of(
                                "<gray>- This is a proof of concept ruleset for the /rules command.",
                                "<gray>- Server owners should modify this by going into <white>/basics/commands/rules.json</white> and modifying the appropriate values.",
                                "<gray>- This system supports MiniMessage of all flavors, meaning you have a massive amount of legroom to work with for formatting.",
                                "<rainbow>- So you can have text like this,",
                                "<gradient:#003399:black><b>- or this,",
                                "<gold><font:uniform>- or even this.",
                                "<green>- Isn't technology wonderful?"))
                        .build())
                );

        defaults.forEach(command ->
        {
            // Save the command
            try
            {
                final FileWriter writer = new FileWriter(new File(folder, command.getMeta().getName().toLowerCase() + ".json"));
                gson.toJson(command.getMeta(), writer);
                writer.close();
            }
            catch (Exception ex)
            {
                Basics.getInstance().getSLF4JLogger().warn("Failed to save default command {}", command.getMeta().getName(), ex);
            }

        });

        return defaults;
    }
}
