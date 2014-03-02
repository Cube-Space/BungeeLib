package net.cubespace.lib.Command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * The Command String itself. Can also contain Arguments. For example "money pay"
     * @return
     */
    String command();

    /**
     * The amount of Arguments needed to execute this Command.
     * @return
     */
    int arguments();
}
