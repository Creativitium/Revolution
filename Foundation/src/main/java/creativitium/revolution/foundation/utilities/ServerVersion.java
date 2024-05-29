package creativitium.revolution.foundation.utilities;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.InputStreamReader;
import java.util.Objects;

/**
 * <h1>ServerVersion</h1>
 * <p>An object representing the server version manifest file, which has been present since 1.14.</p>
 */

@Getter
public class ServerVersion
{
    private static final Gson gson = new Gson();

    private String id;
    private String name;
    @SerializedName("world_version")
    private int worldVersion;
    @SerializedName("series_id")
    private String seriesId;
    @SerializedName("protocol_version")
    private int protocolVersion;
    @SerializedName("pack_version")
    private PackVersion packVersion;
    @SerializedName("build_time")
    private String buildTime;
    @SerializedName("java_component")
    private String javaComponent;
    @SerializedName("java_version")
    private short javaVersion;
    private boolean stable;
    @SerializedName("use_editor")
    private boolean useEditor;

    public static ServerVersion getServerVersion()
    {
        return gson.fromJson(new InputStreamReader(Objects.requireNonNull(Bukkit.class.getClassLoader().getResourceAsStream("version.json"))), ServerVersion.class);
    }

    @Getter
    public static class PackVersion
    {
        private int resource;
        private int data;
    }
}
