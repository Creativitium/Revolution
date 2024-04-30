package creativitium.revolution.foundation.command;

import creativitium.revolution.foundation.Foundation;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class RCommand
{
    protected final Foundation foundation = Foundation.getInstance();
    //--
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final String usage;
    @Getter
    private final List<String> aliases;
    //--
    @Getter
    private final String permission;
    @Getter
    private final SourceType source;
    //--
    @Getter
    private final RCommandInternal internalCommand;

    /**
     * Constructor for RCommand that does not take any parameters on its own, but rather pulls them from the
     *  CommandParameters annotation. If the annotation is not present, the command will fail to initialize.
     */
    public RCommand()
    {
        if (!getClass().isAnnotationPresent(CommandParameters.class))
        {
            throw new IllegalStateException("Parameters were not defined for this command!");
        }

        //--
        CommandParameters parameters = getClass().getAnnotation(CommandParameters.class);
        //--
        this.name = parameters.name();
        this.description = parameters.description();
        this.usage = parameters.usage();
        this.aliases = Arrays.asList(parameters.aliases());
        //--
        this.permission = parameters.permission();
        this.source = parameters.source();
        //--
        internalCommand = new RCommandInternal(this);
    }

    /**
     * Constructor for RCommand that takes parameters on its own and does not require an annotation to be included
     *  as part of the class. Useful for creating a single boilerplate/template command that will be generated
     *  dynamically during runtime.
     * @param name          String
     * @param description   String
     * @param usage         String
     * @param aliases       String[]
     * @param permission    String
     * @param source        SourceType
     */
    public RCommand(String name, String description, String usage, String[] aliases, String permission, SourceType source)
    {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = Arrays.asList(aliases);
        //--
        this.permission = permission;
        this.source = source;
        //--
        internalCommand = new RCommandInternal(this);
    }

    public abstract boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args);

    /**
     * Get the plugin that is responsible for the command. This should never be null because if it is, the server will
     *  crash upon attempting to initialize the commands.
     * @return  Plugin
     */
    public abstract @NotNull Plugin getPlugin();

    @Nullable
    public List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return Collections.emptyList();
    }

    /**
     * Send a message to a CommandSender with optional placeholders.
     * @param sender        CommandSender
     * @param text          String
     * @param placeholders  TagResolver...
     */
    protected void msg(CommandSender sender, String text, TagResolver... placeholders)
    {
        sender.sendMessage(foundation.getMessageService().getMessage(text, placeholders));
    }

    /**
     * Broadcast a message to the server with optional placeholders.
     * @param text          String
     * @param placeholders  TagResolver...
     */
    protected void broadcast(String text, TagResolver... placeholders)
    {
        Bukkit.broadcast(foundation.getMessageService().getMessage(text, placeholders));
    }

    /**
     * Broadcast an administrative action to the server as a Component.
     * @param sender    CommandSender
     * @param action    Component
     */
    protected void action(CommandSender sender, Component action)
    {
        Bukkit.broadcast(foundation.getMessageService().getMessage("revolution.action.format", Placeholder.unparsed("name", sender.getName()), Placeholder.component("action", action)));
    }

    /**
     * Broadcast an administrative action to the server with optional placeholders.
     * @param sender        CommandSender
     * @param text          String
     * @param placeholders  TagResolver...
     */
    protected void action(CommandSender sender, String text, TagResolver... placeholders)
    {
        action(sender, foundation.getMessageService().getMessage(text, placeholders));
    }

    /**
     * Gets a list of all online players' names.
     * @return  List<String>
     */
    protected final List<String> getOnlinePlayers()
    {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
    }

    protected final OfflinePlayer getOfflinePlayer(String nameOrUuid)
    {
        try
        {
            return Bukkit.getOfflinePlayer(UUID.fromString(nameOrUuid));
        }
        catch (Exception ignored)
        {
            return Bukkit.getOfflinePlayer(nameOrUuid);
        }
    }

    protected final Optional<OfflinePlayer> getOfflinePlayerIfCached(String name)
    {
        return Optional.ofNullable(Bukkit.getOfflinePlayerIfCached(name));
    }

    protected final Optional<Player> getPlayer(String nameOrUuid)
    {
        try
        {
            return Optional.ofNullable(Bukkit.getPlayer(UUID.fromString(nameOrUuid)));
        }
        catch (Exception ignored)
        {
            return Optional.ofNullable(Bukkit.getPlayer(nameOrUuid));
        }
    }

    /**
     * Get all the Strings in a list that start with the given input String.
     * @param args  List<String>
     * @param input String
     * @return      List<String>
     */
    protected final List<String> match(List<String> args, String input)
    {
        return args.stream().filter(string -> string.toLowerCase().equalsIgnoreCase(input) || string.toLowerCase().startsWith(input.toLowerCase())).toList();
    }

    private static class RCommandInternal extends Command implements PluginIdentifiableCommand
    {
        private final RCommand external;

        protected RCommandInternal(RCommand command)
        {
            super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
            setPermission(command.getPermission());
            permissionMessage(Foundation.getInstance().getMessageService().getMessage("revolution.command.error.no_permission.command"));
            external = command;
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args)
        {
            if (!testPermissionSilent(sender) || !external.getSource().matchesSourceType(sender))
            {
                sender.sendMessage(Objects.requireNonNull(permissionMessage()));
                return true;
            }

            try
            {
                if (!external.run(sender, sender instanceof Player player ? player : null, commandLabel, args))
                {
                    external.msg(sender, "revolution.command.error.usage", Placeholder.unparsed("usage", getUsage()));
                }
            }
            catch (Throwable ex)
            {
                external.msg(sender, "revolution.command.error.internal", TagResolver.resolver("exception", Tag.inserting(Component.text(ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName()))));
                ex.printStackTrace();
            }

            return true;
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
        {
            List<String> options = null;

            try
            {
                if (testPermissionSilent(sender) && external.getSource().matchesSourceType(sender))
                {
                    options = external.tabCompleteOptions(sender, sender instanceof Player player ? player : null, alias, args);
                }
            }
            catch (Throwable ex)
            {
                external.msg(sender, "revolution.command.error.internal", TagResolver.resolver("exception", Tag.inserting(Component.text(ex.getMessage()))));
                ex.printStackTrace();
            }

            return options != null ? options : new ArrayList<>();
        }

        @Override
        public @NotNull Plugin getPlugin()
        {
            return external.getPlugin();
        }
    }
}
