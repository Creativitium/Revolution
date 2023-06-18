package creativitium.revolution.utilities;

import lombok.Getter;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

public class MM
{
    @Getter
    private static final MiniMessage miniMessageAll = MiniMessage.builder().build();

    @Getter
    private static final MiniMessage lessExploitable = MiniMessage.builder().tags(TagResolver.resolver(
            StandardTags.color(),
            StandardTags.rainbow(),
            StandardTags.gradient(),
            StandardTags.decorations(TextDecoration.ITALIC),
            StandardTags.decorations(TextDecoration.BOLD),
            StandardTags.decorations(TextDecoration.OBFUSCATED),
            StandardTags.decorations(TextDecoration.STRIKETHROUGH),
            StandardTags.decorations(TextDecoration.UNDERLINED))).build();

    @Getter
    private static final MiniMessage nonExploitable = MiniMessage.builder().tags(TagResolver.resolver(
            StandardTags.color(),
            StandardTags.rainbow(),
            StandardTags.gradient(),
            StandardTags.decorations(TextDecoration.ITALIC),
            StandardTags.decorations(TextDecoration.BOLD),
            StandardTags.decorations(TextDecoration.STRIKETHROUGH),
            StandardTags.decorations(TextDecoration.UNDERLINED))).build();
}
