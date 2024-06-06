package creativitium.revolution.basics.commands;

import com.google.common.collect.Lists;
import creativitium.revolution.basics.Basics;
import creativitium.revolution.basics.data.BPlayer;
import creativitium.revolution.basics.data.BPlayerService;
import creativitium.revolution.basics.data.MailMessage;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import creativitium.revolution.foundation.utilities.Shortcuts;
import creativitium.revolution.foundation.utilities.Util;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CommandParameters(name = "mail",
        description = "Send and receive mail!",
        usage = "/mail <send <player> <message> | read [page] | clear>",
        aliases = {"email"},
        permission = "basics.command.mail",
        source = SourceType.BOTH)
public class MailCmd extends RCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        if (args.length == 0) return false;

        Optional<BPlayer> target;

        switch (args[0].toLowerCase())
        {
            case "send" ->
            {
                if (args.length < 3) return false;
                target = ((BPlayerService) Shortcuts.getService(Key.key("basics", "primary"))).getPlayerData(args[1]);
                final String message = String.join(" ", ArrayUtils.subarray(args, 2, args.length));

                target.ifPresentOrElse(player ->
                {
                    final MailMessage mailMessage = MailMessage.builder()
                            .senderName(sender.getName())
                            .senderId(SourceType.ONLY_IN_GAME.matchesSourceType(sender) ? playerSender.getUniqueId() : null)
                            .message(message)
                            .timestamp(Instant.now().getEpochSecond())
                            .build();

                    player.getMail().add(mailMessage);

                    msg(sender, "basics.command.mail.sent", Placeholder.parsed("name", player.getName()));
                    msg(sender, "basics.mail.format",
                            Placeholder.parsed("timestamp", Util.SHORT_DATE_FORMAT.format(new Date(mailMessage.getTimestamp() * 1000))),
                            Placeholder.parsed("name", sender.getName()),
                            TagResolver.resolver("read_indicator", Tag.styling(text ->
                            {
                                if (mailMessage.isRead())
                                {
                                    text.color(NamedTextColor.GRAY);
                                    text.decorate(TextDecoration.ITALIC);
                                }
                            })),
                            Placeholder.unparsed("message", message));
                }, () -> msg(sender, "revolution.command.error.player_not_found"));
            }
            case "read" ->
            {
                if (SourceType.ONLY_CONSOLE.matchesSourceType(sender))
                {
                    msg(sender, "revolution.command.error.no_permission.subcommand");
                    return true;
                }
                target = Optional.of(((BPlayerService) Shortcuts.getService(Key.key("basics", "primary"))).getPlayerData(playerSender.getUniqueId()));
                final List<List<MailMessage>> partitioned = Lists.partition(target.get().getMail(), 5);

                if (partitioned.isEmpty())
                {
                    msg(sender, "basics.command.mail.read.no_mail");
                    return true;
                }

                // Internal number, increment by 1 when displaying to the user
                int page = Math.max(Math.min(args.length > 1 ? Integer.parseInt(args[1]) - 1 : 0, partitioned.size()), 0);

                // Header
                msg(sender, "basics.mail.header",
                        Placeholder.unparsed("page", String.valueOf(page + 1)),
                        Placeholder.unparsed("pages", String.valueOf(partitioned.size())));

                // Messages
                partitioned.get(page).forEach(mailMessage ->
                {
                    msg(sender, "basics.mail.format",
                            Placeholder.parsed("timestamp", Util.SHORT_DATE_FORMAT.format(new Date(mailMessage.getTimestamp() * 1000))),
                            Placeholder.parsed("name", mailMessage.getSenderName()),
                            TagResolver.resolver("read_indicator", Tag.styling(text ->
                            {
                                if (mailMessage.isRead())
                                {
                                    text.color(NamedTextColor.GRAY);
                                    text.decorate(TextDecoration.ITALIC);
                                }
                            })),
                            Placeholder.unparsed("message", mailMessage.getMessage()));
                    mailMessage.setRead(true);
                });

                // Footer
                final Component previous = Component.text("<<").decorate(TextDecoration.BOLD);
                final Component next = Component.text(">>").decorate(TextDecoration.BOLD);
                msg(sender, "basics.mail.footer",
                        Placeholder.component("prev", page == 0 ? previous.color(NamedTextColor.DARK_GRAY) :
                                previous.color(NamedTextColor.WHITE).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/mail read " + page))), // It's already decremented, so we don't need to do anything about it
                        Placeholder.component("next", page + 1 >= partitioned.size() ? next.color(NamedTextColor.DARK_GRAY) :
                                next.color(NamedTextColor.WHITE).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/mail read " + (page + 2)))), // We need to increment it by 2 instead of 1 because it's pre-decremented
                        Placeholder.unparsed("page", String.valueOf(page + 1)),
                        Placeholder.unparsed("pages", String.valueOf(partitioned.size())));
            }
            case "clear" ->
            {
                if (SourceType.ONLY_CONSOLE.matchesSourceType(sender))
                {
                    msg(sender, "revolution.command.error.no_permission.subcommand");
                    return true;
                }
                target = Optional.of(((BPlayerService) Shortcuts.getService(Key.key("basics", "primary"))).getPlayerData(playerSender.getUniqueId()));

                target.get().getMail().clear();
                msg(sender, "basics.command.mail.cleared");
            }
            default ->
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
