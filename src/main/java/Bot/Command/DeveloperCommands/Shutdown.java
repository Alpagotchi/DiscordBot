package Bot.Command.DeveloperCommands;

import Bot.Command.CommandContext;
import Bot.Command.ICommand;
import Bot.Utils.PermissionLevel;
import net.dv8tion.jda.api.exceptions.PermissionException;

public class Shutdown implements ICommand {
	@Override
	public void execute(CommandContext ctx) throws PermissionException {
		if (!PermissionLevel.DEVELOPER.hasPermission(ctx.getMember())) {
			ctx.getChannel().sendMessage("<:RedCross:782229279312314368> This is a **developer-only** command").queue();
			return;
		}

		ctx.getChannel().sendMessage("<:GreenTick:782229268914372609> " + ctx.getJDA().getSelfUser().getName() + " is shutting down...").complete();
		System.exit(0);
	}

	@Override
	public String getHelp(String prefix) {
		return "`Usage: " + prefix + "shutdown\n" + (this.getAliases().isEmpty() ? "`" : "Aliases: " + this.getAliases() + "`\n") + "Shutdowns the bot";
	}

	@Override
	public String getName() {
		return "shutdown";
	}

	@Override
	public Enum<PermissionLevel> getPermissionLevel() {
		return PermissionLevel.DEVELOPER;
	}
}
