package Bot.Command.MemberCommands;

import Bot.Command.CommandContext;
import Bot.Command.ICommand;
import Bot.Utils.PermissionLevel;
import Bot.Database.IDataBaseManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class Delete implements ICommand {
	private final EventWaiter waiter;
	private final String[] emoteIDs = {"782229268914372609", "782229279312314368"};
	private final EnumSet<Permission> permissions = EnumSet.of(Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_HISTORY);

	public Delete(EventWaiter waiter) {
		this.waiter = waiter;
	}

	@Override
	public void execute(CommandContext ctx) throws PermissionException {
		if (!IDataBaseManager.INSTANCE.isUserInDB(ctx.getAuthorID())) {
			ctx.getChannel().sendMessage("<:RedCross:782229279312314368> You don't own an alpaca, so " + ctx.getJDA().getSelfUser().getName() + " stores no data about you").queue();
			return;
		}

		permissions.forEach((permission) -> {
			if (!ctx.getGuild().getSelfMember().hasPermission(ctx.getChannel(), permission)) {
				throw new PermissionException(permission.getName());
			}
		});

		ctx.getChannel().sendMessage("⚠ Are you sure you want to delete your data? You **permanently** lose all progress").queue((message) -> {
			message.addReaction("GreenTick:" + emoteIDs[0]).queue();
			message.addReaction("RedCross:" + emoteIDs[1]).queue();

			this.waiter.waitForEvent(
					GuildMessageReactionAddEvent.class,
					(event) -> event.getMessageIdLong() == message.getIdLong()
							&& event.getMember().equals(ctx.getMember())
							&& event.getReactionEmote().isEmote()
							&& Arrays.asList(emoteIDs).contains(event.getReactionEmote().getEmote().getId()),
					(event) -> {
						if (event.getReactionEmote().getEmote().getIdLong() == 782229268914372609L) {
							IDataBaseManager.INSTANCE.deleteDBEntry(ctx.getAuthorID());
							message.editMessage("<:GreenTick:782229268914372609> Personal data successfully deleted").queue();
						} else if (event.getReactionEmote().getEmote().getIdLong() == 782229279312314368L) {
							message.editMessage("<:RedCross:782229279312314368> Delete process cancelled").queue();
						}
						message.clearReactions().queue();
					},
					90L, TimeUnit.SECONDS,
					() -> {
						message.clearReactions().queue();
						message.editMessage("<:RedCross:782229279312314368> Answer timed out").queue();
					}
			);
		});
	}

	@Override
	public String getHelp(String prefix) {
		return "`Usage: " + prefix + "delete\n" + (this.getAliases().isEmpty() ? "`" : "Aliases: " + this.getAliases() + "`\n") + "Deletes all of your stored data";
	}

	@Override
	public String getName() {
		return "delete";
	}

	@Override
	public Enum<PermissionLevel> getPermissionLevel() {
		return PermissionLevel.MEMBER;
	}
}
