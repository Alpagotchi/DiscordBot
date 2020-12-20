package Bot.Command.MemberCommands;

import Bot.Command.CommandContext;
import Bot.Command.ICommand;
import Bot.Database.IDataBaseManager;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class Wallet implements ICommand {

    @Override
    public void handle(CommandContext commandContext) {
        final long memberID = commandContext.getGuild().getMember(commandContext.getAuthor()).getIdLong();
        final TextChannel channel = commandContext.getChannel();
        final int balance = IDataBaseManager.INSTANCE.getInventory(memberID, "currency");

        channel.sendMessage("\uD83D\uDCB5 Your current balance is **" + (balance == 1 ? "** fluffy" : "** fluffies")).queue();
    }

    @Override
    public String getHelp(String prefix) {
        return "`Usage: " + prefix + "wallet\n" + (this.getAliases().isEmpty() ? "`" : "Aliases: " + this.getAliases() + "`\n") + "Shows your current balance of fluffies";
    }

    @Override
    public String getName() {
        return "wallet";
    }

    @Override
    public String getPermissionLevel() {
        return "member";
    }

    @Override
    public List<String> getAliases() {
        return List.of("balance", "money");
    }
}
