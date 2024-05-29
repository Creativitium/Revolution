package creativitium.revolution.foundation.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <h1>CommandParameters</h1>
 * <p>A series of parameters assigned to an RCommand class that are used during registration.</p>
 * <p>This is required for commands with no constructor to be registered.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandParameters
{
    /**
     * The name this command will be registered with
     * @return  String
     */
    String name();

    /**
     * The description this command will be registered with
     * @return  String
     */
    String description();

    /**
     * The usage for this command
     * @return  String
     */
    String usage();

    /**
     * A series of alternate names this command will be registered with
     * @return  String[]
     */
    String[] aliases() default {};

    /**
     * The permission node required to execute this command
     * @return  String
     */
    String permission();

    /**
     * What kinds of CommandSenders are allowed to execute this command
     * @return  SourceType
     */
    SourceType source();
}
