package Bot.Command.MemberCommands;

import Bot.Command.CommandContext;
import Bot.Command.ICommand;
import Bot.Database.IDataBaseManager;
import net.dv8tion.jda.api.entities.TextChannel;

public class InventoryCommand implements ICommand {
    @Override
    public void handle(CommandContext commandContext) {
        final TextChannel channel = commandContext.getChannel();
        final long memberID = commandContext.getGuild().getMember(commandContext.getAuthor()).getIdLong();

        channel.sendMessage("\uD83D\uDCE6 Your inventory contains `" + IDataBaseManager.INSTANCE.getAlpaca(memberID, "inventory") + "` items").queue();
    }

    @Override
    public String getHelp(CommandContext commandContext) {
        return "`Usage: " + IDataBaseManager.INSTANCE.getPrefix(commandContext.getGuild().getIdLong()) + "inventory`\nDisplays your inventory with the bought items from the shop";
    }

    @Override
    public String getName() {
        return "inventory";
    }
}
