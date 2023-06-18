package creativitium.revolution.templates;

import creativitium.revolution.Revolution;
import creativitium.revolution.commands.CommandParameters;
import creativitium.revolution.commands.SourceType;
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
    protected final Revolution plugin = Revolution.getInstance();
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

    @Nullable
    public List<String> tabCompleteOptions(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        return Collections.emptyList();
    }

    protected void msg(CommandSender sender, String text, TagResolver... placeholders)
    {
        sender.sendMessage(plugin.msg.getMessage(text, placeholders));
    }

    protected void broadcast(String text, TagResolver... placeholders)
    {
        Bukkit.broadcast(plugin.msg.getMessage(text, placeholders));
    }

    protected void action(CommandSender sender, Component action)
    {
        Bukkit.broadcast(plugin.msg.getMessage("revolution.action.format", Placeholder.unparsed("name", sender.getName()), Placeholder.component("action", action)));
    }

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

    private static class RCommandInternal extends Command implements PluginIdentifiableCommand
    {
        private final RCommand external;

        protected RCommandInternal(RCommand command)
        {
            super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
            setPermission(command.getPermission());
            permissionMessage(Revolution.getInstance().msg.getMessage("revolution.command.error.no_permission.command"));
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
                    sender.sendMessage(Component.text(getUsage()));
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
            return Revolution.getInstance();
        }
    }
}
