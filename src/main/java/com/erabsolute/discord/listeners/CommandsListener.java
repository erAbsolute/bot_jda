package com.erabsolute.discord.listeners;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.erabsolute.discord.commands.Info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;

public class CommandsListener extends ListenerAdapter {

	private Logger logs = LoggerFactory.getLogger(CommandsListener.class);

	private Info info = new Info();

	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		String command = event.getName();
		User user = event.getUser();
		Guild guild = event.getGuild();
		event.deferReply().setEphemeral(true).queue();
		switch (command) {
		case "help":
			System.out.println(user.getName() + "#" + user.getDiscriminator() + " requested the command " + command);
			EmbedBuilder helpEmbed = info.help(user, guild);
			event.getHook().sendMessageEmbeds(helpEmbed.build()).queue();
			break;
		case "ping":
			System.out.println(user.getName() + "#" + user.getDiscriminator() + " requested the command " + command);
			EmbedBuilder pingEmbed = info.ping(event.getJDA(), user, guild);
			event.getHook().sendMessageEmbeds(pingEmbed.build()).setEphemeral(true).queue();
			break;
		case "qrfy":
			System.out.println(user.getName() + "#" + user.getDiscriminator() + " requested the command " + command);
			try (ByteArrayOutputStream baos = info.generateQRCodeImage(event.getOptions().get(0).getAsString());) {
				EmbedBuilder qrfyEmbed = info.qrfy(user, guild);
				event.getHook().sendMessageEmbeds(qrfyEmbed.build()).setEphemeral(true)
						.addFiles(FileUpload.fromData(baos.toByteArray(), "qr.png")).queue();
				break;
			} catch (Exception e) {
				event.getHook().sendMessageEmbeds(info.errorEmbed(user, guild).build()).setEphemeral(true).queue();
				break;
			}
		}
	}

	public void onGuildReady(GuildReadyEvent event) {
		List<CommandData> commandData = new ArrayList<>();
		commandData.add(Commands.slash("help", "Muestra los comandos disponibles."));
		commandData.add(Commands.slash("ping", "Devuelve la latencia del bot."));
		commandData.add(Commands.slash("qrfy", "Genera un código QR con el texto que le pases.")
				.addOption(OptionType.STRING, "input", "Texto que queremos pasar a QR"));
		event.getGuild().updateCommands().addCommands(commandData).queue();
	}

	private static final Pattern quesoRegex = Pattern.compile("(?i).*(que|qe|q|ke|k)\\.?$");
	private static final Pattern discordInvitePattern = Pattern.compile(
			"(https?:\\/\\/)?(www\\.)?(discord\\.(gg|io|me|li)|discordapp\\.com\\/invite)\\/[a-zA-Z0-9]+",
			Pattern.CASE_INSENSITIVE);

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String message = event.getMessage().getContentRaw();
		Matcher queso = quesoRegex.matcher(message);
		Matcher reportInvite = discordInvitePattern.matcher(message);
		if (queso.matches()) {
			event.getMessage().reply("so").queue();
			return;
		} else if (reportInvite.matches()) {
			EmbedBuilder report = info.errorEmbed(event.getAuthor(), event.getGuild());
			report.setDescription("El usuario " + event.getAuthor().getName() + "#"
					+ event.getAuthor().getDiscriminator() + " ha posteado una invitación.");
			event.getMessage().delete().queue();
			event.getChannel().sendMessageEmbeds(report.build()).queue();
			return;
		}

	}
}
