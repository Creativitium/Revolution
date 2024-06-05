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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <h1>BuildVersion</h1>
 * <p>An object representing a Revolution plugin's build manifest file, which is automatically generated and included
 * during the compilation process.</p>
 * <p>Build manifests are not required in external projects that utilize Revolution's systems to function, but you are
 * still recommended to utilize them in the interest of consistency.</p>
 */
@Getter
public class BuildVersion
{
    private static final Gson GSON = new Gson();
    private static final Map<Plugin, BuildVersion> cache = new HashMap<>();

    @SerializedName("git.branch")
    private String branch;
    @SerializedName("git.dirty")
    private boolean dirty;
    @SerializedName("git.remote.origin.url")
    private String remoteOrigin;
    @SerializedName("git.tags")
    private String tags;

    @SerializedName("git.build.number")
    private int buildNumber;
    @SerializedName("git.build.time")
    private String buildTime;
    @SerializedName("git.build.user.name")
    private String buildUser;
    @SerializedName("git.build.version")
    private String buildVersion;

    @SerializedName("git.commit.id")
    private String commitId;
    @SerializedName("git.commit.time")
    private String commitTime;
    @SerializedName("git.closest.tag.name")
    private String closestTag;
    @SerializedName("git.closest.tag.commit.count")
    private String closestTagCommitCount;

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

    public List<TagResolver.Single> getAsPlaceholders()
    {
        return List.of(Placeholder.parsed("branch", getBranch()),
                Placeholder.parsed("dirty", String.valueOf(isDirty())),
                Placeholder.parsed("remote_origin", getRemoteOrigin()),
                Placeholder.parsed("tags", getTags()),
                // Build
                Placeholder.parsed("build_date", getBuildTime()),
                Placeholder.parsed("build_number", String.valueOf(getBuildNumber())),
                Placeholder.parsed("build_user", getBuildUser()),
                Placeholder.parsed("build_version", getBuildVersion()),
                // Commits
                Placeholder.parsed("commit_id", getCommitId()),
                Placeholder.parsed("commit_time", getCommitTime()),
                Placeholder.parsed("closest_tag", getClosestTag()),
                Placeholder.parsed("closest_tag_commit_count", getClosestTagCommitCount()));
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
