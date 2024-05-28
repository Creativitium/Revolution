package creativitium.revolution.communication.platforms;

import com.google.common.base.Strings;
import creativitium.revolution.administration.Administration;
import creativitium.revolution.communication.boilerplate.Communicator;
import creativitium.revolution.communication.event.CommunicatorChatEvent;
import dev.vankka.mcdiscordreserializer.discord.DiscordSerializer;
import dev.vankka.mcdiscordreserializer.minecraft.MinecraftSerializer;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Discord extends Communicator
{
    private JDA discordBot;

    @Override
    public void onStart()
    {
        sanityCheck();

        discordBot = JDABuilder.createDefault(getPlugin().getConfig().getString("discord.token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.playing("Minecraft"))
                .setAutoReconnect(true)
                .build();

        discordBot.addEventListener(new EventHandler(this));
    }

    @Override
    public void onStop()
    {
        if (discordBot != null)
        {
            discordBot.shutdown();
        }
    }

    @Override
    public void handleBroadcast(Component message)
    {
        sendMessage(getPlugin().getConfig().getString("discord.channels.server_chat"), message.decorate(TextDecoration.BOLD));
    }

    @Override
    public void handleAdminChatMessage(Key source, Component sourcePrefix, String sender, Component prefix, Component message)
    {
        sendMessage(getPlugin().getConfig().getString("discord.channels.admin_chat"), getMsg("communication.format.platform.discord",
                Placeholder.component("platformprefix", sourcePrefix),
                Placeholder.parsed("user", sender),
                Placeholder.component("userprefix", prefix.append(Component.space())),
                Placeholder.component("display", Component.text(sender)),
                Placeholder.component("message", message)));
    }

    @Override
    public void handleCommunicatorMessage(CommunicatorChatEvent event)
    {
        sendMessage(getPlugin().getConfig().getString("discord.channels.server_chat"), getMsg("communication.format.platform.discord",
                Placeholder.component("platformprefix", event.getSourcePrefix()),
                Placeholder.parsed("user", event.getSender()),
                Placeholder.component("userprefix", event.getSenderPrefix().append(Component.space())),
                Placeholder.component("display", event.getSenderDisplay()),
                Placeholder.component("message", event.getMessage())));
    }

    public void sendMessage(final String channelId, final Component message)
    {
        if (discordBot == null || Strings.isNullOrEmpty(channelId) || message == null) return;
        final TextChannel channel = discordBot.getTextChannelById(channelId);

        if (channel != null)
        {
            channel.sendMessage(DiscordSerializer.INSTANCE.serialize(message)).queue();
        }
        else
        {
            getPlugin().getSLF4JLogger().warn("Can't send message to channel ID {} because it doesn't exist!", channelId);
        }
    }

    @Override
    public void sanityCheck()
    {
        if (Strings.isNullOrEmpty(getPlugin().getConfig().getString("discord.token")))
        {
            throw new IllegalArgumentException("The token is blank!");
        }
    }

    @Override
    public Key getIdentifier()
    {
        return Key.key("communication", "discord");
    }

    @RequiredArgsConstructor
    public static class EventHandler extends ListenerAdapter
    {
        private final Discord discord;

        @Override
        public void onMessageReceived(@NotNull MessageReceivedEvent event)
        {
            if (discord.discordBot == null || event.getAuthor() == discord.discordBot.getSelfUser() || event.getChannel().getType() != ChannelType.TEXT) return;
            final TextChannel channel = event.getChannel().asTextChannel();

            if (!channel.getId().equalsIgnoreCase(discord.getPlugin().getConfig().getString("discord.channels.server_chat", "")) && !channel.getId().equalsIgnoreCase(discord.getPlugin().getConfig().getString("discord.channels.admin_chat", "")))
            {
                return;
            }

            final Member member = event.getMember();
            final String name = member.getNickname() != null ? member.getNickname() : event.getAuthor().getName();
            final Component memberPrefix;
            if (!member.getRoles().isEmpty())
            {
                // Highest role
                final Role role = member.getRoles().get(0);
                memberPrefix = discord.getMsg("communication.prefix.platform.discord_role", Placeholder.component("role", Component.text(role.getName()).color(TextColor.color(role.getColorRaw()))));
            }
            else
            {
                memberPrefix = discord.getMsg("communication.prefix.platform.discord_role", Placeholder.component("role", Component.text("Member").color(NamedTextColor.WHITE)));
            }
            Component message = MinecraftSerializer.INSTANCE.serialize(event.getMessage().getContentRaw());

            final List<Message.Attachment> attachments = event.getMessage().getAttachments();
            if (!attachments.isEmpty())
            {
                if (!event.getMessage().getContentRaw().isEmpty())
                {
                    message = message.append(Component.space());
                }

                message = message.append(Component.join(JoinConfiguration.separator(Component.space()),
                        attachments.stream().map(attachment -> discord.getMsg("communication.suffix.platform.discord_attachment",
                                Placeholder.parsed("url", attachment.getUrl()))).toList()));
            }

            if (channel.getId().equalsIgnoreCase(discord.getPlugin().getConfig().getString("discord.channels.server_chat", "")))
            {
                discord.forwardChat(discord.getIdentifier(), name, Component.text(name), memberPrefix, message);
            }
            else
            {
                Administration.getInstance().getAdminChatService().sendAdminChat(discord.getIdentifier(), name, memberPrefix, message, true);
            }
        }

        @Override
        public void onReady(@NotNull ReadyEvent event)
        {
            discord.sendMessage(discord.getPlugin().getConfig().getString("discord.channels.server_chat"),
                    discord.getMsg("communication.messages.server_has_started"));
        }
    }
}