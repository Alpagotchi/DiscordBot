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
    public void handle(CommandContext commandContext) {
        final File alpacaStats = new File("src/main/resources/alpacaStats.jpg");
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final Member botCreator = commandContext.getGuild().getMemberById(Config.get("OWNER_ID"));
        final long memberID = commandContext.getMessage().getAuthor().getIdLong();
        final TextChannel channel = commandContext.getChannel();

        final int hunger = IDataBaseManager.INSTANCE.getAlpacaValues(memberID, "hunger");
        final int thirst = IDataBaseManager.INSTANCE.getAlpacaValues(memberID, "thirst");
        final int energy = IDataBaseManager.INSTANCE.getAlpacaValues(memberID, "energy");
        final int joy = IDataBaseManager.INSTANCE.getAlpacaValues(memberID, "joy");

        BufferedImage alpacaIMG = null;

        try {
            alpacaIMG = ImageIO.read(alpacaStats);

        } catch (IOException error) {
            LOGGER.error(error.getMessage());
        }

        if (alpacaIMG == null) {
            LOGGER.error("Could not read alpaca file");
            return;
        }

        Graphics alpakaGraphics = alpacaIMG.getGraphics();
        alpakaGraphics.setFont(new Font("SansSerif", Font.BOLD, 15));

        alpakaGraphics.setColor(getColorOfValues(hunger));
        alpakaGraphics.fillRect(31, 31, (int)(hunger * 1.75), 12);

        alpakaGraphics.setColor(Color.BLACK);
        alpakaGraphics.drawString(hunger + "/100", getPosition(hunger, "front"), 24);

        alpakaGraphics.setColor(getColorOfValues(thirst));
        alpakaGraphics.fillRect(31, 73, (int)(thirst * 1.75), 12);

        alpakaGraphics.setColor(Color.BLACK);
        alpakaGraphics.drawString(thirst + "/100", getPosition(thirst, "front"), 66);

        alpakaGraphics.setColor(getColorOfValues(energy));
        alpakaGraphics.fillRect(420, 31, (int)(energy * 1.75), 12);

        alpakaGraphics.setColor(Color.BLACK);
        alpakaGraphics.drawString(energy + "/100", getPosition(energy, "back"), 24);

        alpakaGraphics.setColor(getColorOfValues(joy));
        alpakaGraphics.fillRect(420, 73, (int)(joy * 1.75), 12);

        alpakaGraphics.setColor(Color.BLACK);
        alpakaGraphics.drawString(joy + "/100", getPosition(joy, "back"), 66);

        File newAlpacaFile = new File("src/main/resources/editedAlpacaStats.jpg");

        try {
            ImageIO.write(alpacaIMG, "jpg", newAlpacaFile);

        } catch (IOException error) {
            LOGGER.error(error.getMessage());
        }

        embedBuilder.setTitle("" + IDataBaseManager.INSTANCE.getNickname(memberID) + "");
        embedBuilder.setDescription("_Have a llamazing day!_");
        embedBuilder.setThumbnail(commandContext.getMember().getUser().getAvatarUrl());
        embedBuilder.setFooter("Created by " + botCreator.getEffectiveName(), botCreator.getUser().getEffectiveAvatarUrl());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setImage("attachment://editedAlpacaStats.jpg");

        channel.sendFile(newAlpacaFile, "editedAlpacaStats.jpg").embed(embedBuilder.build()).queue();
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
