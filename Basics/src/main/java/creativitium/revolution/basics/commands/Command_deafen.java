package creativitium.revolution.basics.commands;

import creativitium.revolution.basics.Basics;
import creativitium.revolution.foundation.Foundation;
import creativitium.revolution.foundation.command.CommandParameters;
import creativitium.revolution.foundation.command.RCommand;
import creativitium.revolution.foundation.command.SourceType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@CommandParameters(name = "deafen",
        description = "Bless players with your holy... \"music\".",
        usage = "/deafen [intensity]",
        permission = "basics.command.deafen",
        source = SourceType.BOTH)
public class Command_deafen extends RCommand
{
    private List<BukkitTask> music = new ArrayList<>();
    private BossBar nowPlaying = null;
    // This doesn't work because the RNG algorithm "L32X64MixRandom" isn't available with my JRE
    //final RandomGenerator random = RandomGeneratorFactory.getDefault().create(Instant.now().getEpochSecond());
    private final Random random = new Random(Instant.now().getEpochSecond());

    @Override
    public boolean run(CommandSender sender, Player playerSender, String commandLabel, String[] args)
    {
        int intensity = 10;

        if (args.length >= 1 && args[0].equalsIgnoreCase("stop"))
        {
            music.forEach(BukkitTask::cancel);
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.stopAllSounds();
                player.hideBossBar(nowPlaying);
            });
            music.clear();
            msg(sender, "basics.command.deafen.stopped");
            return true;
        }

        if (!music.isEmpty() && !music.stream().allMatch(BukkitTask::isCancelled))
        {
            msg(sender, "basics.command.deafen.someone_is_already_playing_music");
            return true;
        }

        if (args.length >= 1)
        {
            try
            {
                intensity = Math.abs(Integer.parseInt(args[0]));
            }
            catch (NumberFormatException ex)
            {
                msg(sender, "revolution.command.error.invalid_number", Placeholder.unparsed("number", args[1]));
                return true;
            }
        }

        final org.bukkit.Sound[] sounds = org.bukkit.Sound.values();
        int composition = random.nextInt(1, 23);

        nowPlaying = BossBar.bossBar(getMessage("basics.command.deafen.now_playing",
                Placeholder.unparsed("composer", sender.getName()),
                Placeholder.unparsed("number", String.valueOf(composition))), 0F, BossBar.Color.RED, BossBar.Overlay.PROGRESS);

        Bukkit.getOnlinePlayers().forEach(player -> player.showBossBar(nowPlaying));

        final int finalIntensity = intensity;

        IntStream.range(0, intensity).forEach(number ->
        {
            music.add(Bukkit.getScheduler().runTaskLater(getPlugin(), () ->
            {
                Bukkit.getOnlinePlayers().forEach(victim ->
                {
                    victim.playSound(Sound.sound(sounds[random.nextInt(sounds.length)].key(), Sound.Source.MASTER,
                            99999f, random.nextFloat(-1F, 1F)), Sound.Emitter.self());
                });

                nowPlaying.progress((float) number / finalIntensity);
            }, number * 5L));
        });

        Bukkit.getScheduler().runTaskLater(getPlugin(), () ->
        {
            Bukkit.getOnlinePlayers().forEach(player -> player.hideBossBar(nowPlaying));
            music.clear();
        }, (finalIntensity * 5L));
        return true;
    }

    @Override
    public @NotNull Plugin getPlugin()
    {
        return Basics.getInstance();
    }
}
