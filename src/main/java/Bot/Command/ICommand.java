package Bot.Command;

import java.util.List;

public interface ICommand {

    void handle(CommandContext commandContext);

    String getHelp(CommandContext commandContext);

    String getName();

    default List<String> getAliases() {
        return List.of();
    }
}
