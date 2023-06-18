package creativitium.revolution.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandParameters
{
    String name();

    String description();

    String usage();

    String[] aliases() default {};

    String permission();

    SourceType source();
}
