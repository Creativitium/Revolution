package creativitium.revolution.foundation.utilities;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class BuildVersion
{
    private static final Gson GSON = new Gson();
    private static final Map<Plugin, BuildVersion> cache = new HashMap<>();

    @SerializedName("git.branch")
    private String branch;
    @SerializedName("git.build.time")
    private String buildTime;
    @SerializedName("git.commit.id")
    private String commitId;
    @SerializedName("git.commit.time")
    private String commitTime;
    @SerializedName("git.dirty")
    private boolean dirty;

    public boolean isDevelopmentBuild()
    {
        return dirty || !branch.equalsIgnoreCase("main");
    }

    @Override
    public String toString()
    {
        return String.format("===== BUILD DETAILS =====\n - Branch: %s\n - Build Date: %s\n - Commit: %s\n - Commit Date: %s\n - Changes Committed: %s\n=========================",
                branch, buildTime, commitId, commitTime, !dirty);
    }

    public TagResolver.Single[] toPlaceholders()
    {
        return new TagResolver.Single[] {
                Placeholder.parsed("branch", branch),
                Placeholder.parsed("build_date", buildTime),
                Placeholder.parsed("commit", commitId),
                Placeholder.parsed("commit_date", commitTime),
                Placeholder.parsed("changes_committed", String.valueOf(!dirty))};
    }

    public static Optional<BuildVersion> getVersion(Plugin plugin)
    {
        // Avoid pulling the data if we already have it cached
        if (cache.containsKey(plugin))
        {
            return Optional.ofNullable(cache.get(plugin));
        }

        final InputStream resource = plugin.getResource("build.json");

        if (resource == null)
        {
            cache.put(plugin, null);
            return Optional.empty();
        }

        final BuildVersion result = GSON.fromJson(new InputStreamReader(resource), BuildVersion.class);
        cache.put(plugin, result);

        return Optional.ofNullable(result);
    }
}
