package Bot.Command;

import java.util.List;

public interface ICommand {

    void handle(CommandContext commandContext);

    String getHelp(String prefix);

    String getName();

    String getPermissionLevel();

    default List<String> getAliases() {
        return List.of();
    }
}
