package Bot.Command.MemberCommands;

import Bot.Command.CommandContext;
import Bot.Command.ICommand;
import Bot.Command.PermissionLevel;
import Bot.Config;
import Bot.Database.IDataBaseManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class MyAlpaca implements ICommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyAlpaca.class);

    @Override
    public void execute(CommandContext commandContext) {

        if (!IDataBaseManager.INSTANCE.isUserInDB(commandContext.getAuthorID())) {
            commandContext.getChannel().sendMessage("<:RedCross:782229279312314368> You do not own a alpaca, use **" + commandContext.getPrefix() + "init** first").queue();
            return;
        }

        BufferedImage alpacaIMG = null;
        final File alpacaStats = new File("src/main/resources/alpacaStats.jpg");

        try {
            alpacaIMG = ImageIO.read(alpacaStats);

        } catch (IOException error) {
            LOGGER.error(error.getMessage());
        }

        if (alpacaIMG == null) {
            LOGGER.error("Could not read alpaca file");
            return;
        }

        final int hunger = IDataBaseManager.INSTANCE.getAlpacaValues(commandContext.getAuthorID(), "hunger");
        final int thirst = IDataBaseManager.INSTANCE.getAlpacaValues(commandContext.getAuthorID(), "thirst");
        final int energy = IDataBaseManager.INSTANCE.getAlpacaValues(commandContext.getAuthorID(), "energy");
        final int joy = IDataBaseManager.INSTANCE.getAlpacaValues(commandContext.getAuthorID(), "joy");

        Graphics alpacaGraphics = alpacaIMG.getGraphics();
        alpacaGraphics.setFont(new Font("SansSerif", Font.BOLD, 15));
        alpacaGraphics.setColor(Color.BLACK);

        alpacaGraphics.drawString(hunger + "/100", getPosition(hunger, "front"), 24);
        alpacaGraphics.drawString(thirst + "/100", getPosition(thirst, "front"), 66);
        alpacaGraphics.drawString(energy + "/100", getPosition(energy, "back"), 24);
        alpacaGraphics.drawString(joy + "/100", getPosition(joy, "back"), 66);

        alpacaGraphics.setColor(getColorOfValues(hunger));
        alpacaGraphics.fillRect(31, 31, (int) (hunger * 1.75), 12);

        alpacaGraphics.setColor(getColorOfValues(thirst));
        alpacaGraphics.fillRect(31, 73, (int) (thirst * 1.75), 12);

        alpacaGraphics.setColor(getColorOfValues(energy));
        alpacaGraphics.fillRect(420, 31, (int) (energy * 1.75), 12);

        alpacaGraphics.setColor(getColorOfValues(joy));
        alpacaGraphics.fillRect(420, 73, (int) (joy * 1.75), 12);

        File newAlpacaFile = new File("src/main/resources/editedAlpacaStats.jpg");

        try {
            ImageIO.write(alpacaIMG, "jpg", newAlpacaFile);

        } catch (IOException error) {
            LOGGER.error(error.getMessage());
        }

        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final Member botCreator = commandContext.getGuild().getMemberById(Config.get("OWNER_ID"));

        embedBuilder.setTitle("" + IDataBaseManager.INSTANCE.getNickname(commandContext.getAuthorID()) + "");
        embedBuilder.setDescription("_Have a llamazing day!_");
        embedBuilder.setThumbnail(commandContext.getMember().getUser().getAvatarUrl());
        embedBuilder.setFooter("Created by " + botCreator.getEffectiveName(), botCreator.getUser().getEffectiveAvatarUrl());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setImage("attachment://editedAlpacaStats.jpg");

        commandContext.getChannel().sendFile(newAlpacaFile, "editedAlpacaStats.jpg").embed(embedBuilder.build()).queue();
    }

    @Override
    public String getHelp(String prefix) {
        return "`Usage: " + prefix + "myalpaca\n" + (this.getAliases().isEmpty() ? "`" : "Aliases: " + this.getAliases() + "`\n") + "Shows the stats of your alpaca";
    }

    @Override
    public String getName() {
        return "myalpaca";
    }

    @Override
    public Enum<PermissionLevel> getPermissionLevel() {
        return PermissionLevel.MEMBER;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ma", "stats", "alpaca");
    }

    private Color getColorOfValues(int value) {
        if (value >= 80) {
            return Color.GREEN;

        } else if (value >= 60) {
            return Color.YELLOW;

        } else if (value >= 40) {
            return Color.ORANGE;

        } else if (value >= 20) {
            return Color.RED;

        } else
            return Color.BLACK;
    }

    private int getPosition(int value, String position) {
        if (value == 100) {
            return position.equalsIgnoreCase("front") ? 145 : 534;

        } else if (value >= 10) {
            return position.equalsIgnoreCase("front") ? 155 : 544;

        } else {
            return position.equalsIgnoreCase("front") ? 165 : 554;
        }
    }
}
