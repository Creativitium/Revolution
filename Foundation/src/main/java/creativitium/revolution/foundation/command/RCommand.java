package creativitium.revolution.foundation.command;

import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.utilities.MM;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

/**
 * <h1>RCommand</h1>
 * <p>A custom command wrapper that generates a working {@link org.bukkit.command.Command} on runtime which can be
 * registered using a {@link creativitium.revolution.foundation.CommandLoader}.</p>
 */
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
     * Broadcast a message to the server.
     * @param text          String
     */
    protected void broadcast(String text)
    {
        Bukkit.broadcast(MM.getMiniMessageAll().deserialize(text));
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
     * Get a parsed message with optional placeholders.
     * @param text          String
     * @param placeholders  TagResolver...
     * @return              Component
     */
    protected Component getMessage(String text, TagResolver... placeholders)
    {
        return foundation.getMessageService().getMessage(text, placeholders);
    }

    /**
     * Gets a list of all online players' names.
     * @return  A List<String> containing the usernames of all online players
     */
    protected final List<String> getOnlinePlayers()
    {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList();
    }

    /**
     * Gets an OfflinePlayer.
     * @param nameOrUuid    A String representing either a player's username or their UUID
     * @return              OfflinePlayer
     */
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

    /**
     * Gets an optional OfflinePlayer if they are cached by the server.
     * @param name  A String representing a player's username
     * @return      An Optional with an OfflinePlayer if present, otherwise it returns an empty Optional
     */
    protected final Optional<OfflinePlayer> getOfflinePlayerIfCached(String name)
    {
        return Optional.ofNullable(Bukkit.getOfflinePlayerIfCached(name));
    }

    /**
     * Gets an optional Player if they are currently on the server.
     * @param nameOrUuid    A String representing either a player's username or their UUID
     * @return              An Optional with a Player if they are present on the server, otherwise it returns an empty
     *                      Optional
     */
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
     * @param input A String representing the argument given
     * @return      A List<String> representing the potential arguments that match the given argument
     */
    protected final List<String> match(List<String> args, String input)
    {
        return args.stream().filter(string -> string.equalsIgnoreCase(input)
                || string.toLowerCase().startsWith(input.toLowerCase())).toList();
    }

    /**
     * <h2>RCommandInternal</h2>
     * <p>An internal class that does all the legwork for RCommand's functions under the hood</p>
     */
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
                Component exceptionMessage = Component.text(ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName());
                if (sender.hasPermission("foundation.command.see_stacktrace"))
                {
                    exceptionMessage = exceptionMessage.hoverEvent(HoverEvent.showText(Component.translatable("chat.copy.click").color(NamedTextColor.WHITE).appendNewline().appendNewline().append(Component.text(ExceptionUtils.getStackTrace(ex).replaceAll("\t", "    ")).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ExceptionUtils.getStackTrace(ex)));
                }

                external.msg(sender, "revolution.command.error.internal", Placeholder.component("exception", exceptionMessage));
                getPlugin().getSLF4JLogger().error("Command exception details:", ex);
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
                Component exceptionMessage = Component.text(ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName());
                if (sender.hasPermission("foundation.command.see_stacktrace"))
                {
                    exceptionMessage = exceptionMessage.hoverEvent(HoverEvent.showText(Component.translatable("chat.copy.click").color(NamedTextColor.WHITE).appendNewline().appendNewline().append(Component.text(ExceptionUtils.getStackTrace(ex).replaceAll("\t", "    ")).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC))))
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, ExceptionUtils.getStackTrace(ex)));
                }

                external.msg(sender, "revolution.command.error.internal", Placeholder.component("exception", exceptionMessage));
                getPlugin().getSLF4JLogger().error("Command exception details:", ex);
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
