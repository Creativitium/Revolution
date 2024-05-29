package creativitium.revolution.integration.discord;

import com.google.common.base.Strings;
import creativitium.revolution.administration.Administration;
import creativitium.revolution.administration.event.AdminChatEvent;
import creativitium.revolution.foundation.templates.RService;
import creativitium.revolution.integration.Integration;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.mcdiscordreserializer.discord.DiscordSerializer;
import github.scarsz.discordsrv.dependencies.mcdiscordreserializer.minecraft.MinecraftSerializer;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Adm2DiscordSRV extends RService
{
    private static final Key SOURCE = Key.key("integration", "discordsrv_administration");
    private static final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();

    public Adm2DiscordSRV()
    {
        super(Integration.getInstance());
    }

    @Override
    public void onStart()
    {
        DiscordSRV.api.subscribe(this);
    }

    @Override
    public void onStop()
    {
        DiscordSRV.api.unsubscribe(this);
    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void adminChatMessage(DiscordGuildMessageReceivedEvent event)
    {
        if (!DiscordSRV.config().getBoolean("DiscordChatChannelDiscordToMinecraft") ||
                event.getAuthor().getId().equalsIgnoreCase(DiscordSRV.getPlugin().getJda().getSelfUser().getId()) ||
                !event.getChannel().getId().equalsIgnoreCase(getPlugin().getConfig().getString("discord.adminChatChannelId", ""))) return;

        final Member member = event.getMember();
        final Component role = member.getRoles().isEmpty() ? Component.text("No Roles") : Component.text(member.getRoles().get(0).getName()).color(TextColor.color(member.getRoles().get(0).getColorRaw()));
        Component message;

        // Respect the server owner's DiscordSRV configuration
        if (DiscordSRV.config().getBooleanElse("Experiment_MCDiscordReserializer_ToMinecraft", false))
        {
            /* The following code needs some explanation so you can understand the mindset behind it.
             *
             * We want to process the messages coming in from Discord as markdown-styled text for a bit of formatting
             *   consistency between Discord and Minecraft (e.g. bold messages from Discord show up as bold in-game). To
             *   achieve this, we need to serialize the Discord message using something that supports Markdown.
             *
             * There is no Markdown serializer in Adventure by default, and we don't want to bloat up this plugin with
             *   dependencies that might not even get used on runtime if the server owner chooses not to install a
             *   Discord plugin, so instead we use an internal dependency that DiscordSRV itself uses (called
             *   MCDiscordSerializer) to actually serialize the message.
             *
             * However, there is a problem. DiscordSRV and its included dependencies use a relocated version of
             *   Adventure which is entirely separate from what is included in Paper. No idea why they decided to do
             *   this, but it's possible that they wanted to use Adventure internally still but at the same time still
             *   support Spigot, which doesn't include it. Because the two variants are completely separate, they are
             *   assumed to be at best extremely sensitive to version differences and at most just worst incompatible.
             *
             * To work around this, I've simply used the relocated Adventure's GSON serializer to make it spit out a
             *   JSON-formatted text component and then just fed the outcome to the built-in Adventure's GSON serializer.
             */

            message = GsonComponentSerializer.gson().deserialize(github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(MinecraftSerializer.INSTANCE.serialize(event.getMessage().getContentRaw())));
        }
        else
        {
            message = plainText.deserialize(event.getMessage().getContentRaw());
        }

        final List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (!attachments.isEmpty())
        {
            message = message.append(Component.space()).append(Component.join(JoinConfiguration.separator(Component.space()),
                    attachments.stream().map(attachment -> getMsg("integration.discord.adminchat.attachment",
                            Placeholder.parsed("url", attachment.getUrl()))).toList()));
        }

        final Component result = message;

        Bukkit.getScheduler().runTask(getPlugin(), () -> Administration.getInstance().getAdminChatService().sendAdminChat(SOURCE,
                member.getNickname() != null ? member.getNickname() : event.getAuthor().getName(),
                getMsg("integration.discord.adminchat.prefix", Placeholder.component("role", role)),
                result,
                false));
    }

    @EventHandler
    public void onAdminChat(AdminChatEvent event)
    {
        final boolean useNative = getPlugin().getConfig().getBoolean("discordsrv.useNativeChannelGrabber");
        if (event.getSource() == SOURCE || !useNative && Strings.isNullOrEmpty(getPlugin().getConfig().getString("discord.adminChatChannelId"))) return;
        String msg;

        // Respect the server owner's DiscordSRV configuration
        if (DiscordSRV.config().getBooleanElse("Experiment_MCDiscordReserializer_ToDiscord", false))
        {
            msg = DiscordSerializer.INSTANCE.serialize(github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(GsonComponentSerializer.gson().serialize(event.getMessage())));
        }
        else
        {
            msg = plainText.serialize(event.getMessage());
        }

        (useNative ? DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("admin") : DiscordUtil.getTextChannelById(getPlugin().getConfig().getString("discord.adminChatChannelId"))).sendMessage(
                plainText.serialize(getMsg("integration.discord.adminchat.format",
                    Placeholder.unparsed("name", event.getSender()),
                    Placeholder.component("prefix", event.getPrefix()),
                    Placeholder.unparsed("message", msg))))
                .queue();
    }
}
