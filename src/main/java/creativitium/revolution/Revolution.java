package creativitium.revolution;

import creativitium.revolution.guardian.GuardianService;
import creativitium.revolution.messages.MessageService;
import creativitium.revolution.players.PlayerListenerService;
import creativitium.revolution.players.PlayerService;
import creativitium.revolution.sentinel.SentinelService;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Revolution extends JavaPlugin
{
    @Getter
    private static Revolution instance;
    @Getter
    private static final Logger slf4jLogger = LoggerFactory.getLogger("Revolution");
    //--
    public CommandLoader commandLoader;
    //--
    public RServiceGroup critical = new RServiceGroup();
    public MessageService msg;
    //--
    public RServiceGroup listeners = new RServiceGroup();
    public GuardianService gua;
    public SentinelService sen;
    public PlayerService pls;
    public PlayerListenerService plis;
    //--

    @Override
    public void onEnable()
    {
        instance = this;

        // Critical stuff
        msg = critical.addService(NamespacedKey.fromString("rvl:messages"), new MessageService());
        critical.startServices();

        // Load commands
        commandLoader = new CommandLoader("creativitium.revolution.commands");

        // Listeners
        gua = listeners.addService(NamespacedKey.fromString("rvl:guardian"), new GuardianService());
        sen = listeners.addService(NamespacedKey.fromString("rvl:sentinel"), new SentinelService());
        pls = listeners.addService(NamespacedKey.fromString("rvl:players"), new PlayerService());
        plis = listeners.addService(NamespacedKey.fromString("rvl:playerlistener"), new PlayerListenerService());
        listeners.startServices();
    }

    @Override
    public void onDisable()
    {
        listeners.stopServices();
    }
}
