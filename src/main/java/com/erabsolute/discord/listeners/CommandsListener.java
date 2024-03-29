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

	private Info info = new Info();

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		String command = event.getName();
		User user = event.getUser();
		Guild guild = event.getGuild();
		event.deferReply().setEphemeral(true).queue();
		switch (command) {
		case "help":
			logger.info("{} requested the command {}", user.getName(), command);
			EmbedBuilder helpEmbed = info.help(user, guild);
			event.getHook().sendMessageEmbeds(helpEmbed.build()).queue();
			break;
		case "ping":
			EmbedBuilder pingEmbed = info.ping(event.getJDA(), user, guild);
			event.getHook().sendMessageEmbeds(pingEmbed.build()).queue();
			break;
		case "chatgpt":
			logger.info("{} requested the command {}", user.getName(), command);
			EmbedBuilder chatEmbed = info.chatGpt(user, guild, event.getOptions().get(0).getAsString());
			event.getHook().sendMessageEmbeds(chatEmbed.build()).queue();
			break;
		case "info":
			logger.info("{} requested the command {}", user.getName(), command);
			EmbedBuilder infoEmbed = info.info(user, guild, event.getOptions().get(0).getAsString());
			event.getHook().sendMessageEmbeds(infoEmbed.build()).queue();
			break;
		case "qrfy":
			logger.info("{} requested the command {}", user.getName(), command);
			try (ByteArrayOutputStream baos = info.generateQRCodeImage(event.getOptions().get(0).getAsString());) {
				EmbedBuilder qrfyEmbed = info.qrfy(user, guild);
				event.getHook().sendMessageEmbeds(qrfyEmbed.build())
						.addFiles(FileUpload.fromData(baos.toByteArray(), "qr.png")).queue();
				break;
			} catch (Exception e) {
				logger.info("{} crashed the command {}", user.getName(), command);
				event.getHook().sendMessageEmbeds(info.errorEmbed(user, guild).build()).queue();
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
		commandData.add(Commands.slash("chatgpt", "Manda un mensaje a ChatGPT y devuelve la respuesta de este.")
				.addOption(OptionType.STRING, "mensaje", "Mensaje para ChatGPT"));
		commandData.add(Commands.slash("info", "Muestra la información del usuario dado.")
				.addOption(OptionType.STRING, "username", "Usuario que se quiere buscar"));
		event.getGuild().updateCommands().addCommands(commandData).queue();
	}

	private static final Pattern discordInvitePattern = Pattern.compile(
			"(?i).*(https?:\\/\\/)?(www\\.)?(discord\\.(gg|io|me|li)|discordapp\\.com\\/invite)\\/[a-zA-Z0-9]+",
			Pattern.CASE_INSENSITIVE);

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String message = event.getMessage().getContentRaw();
		Matcher reportInvite = discordInvitePattern.matcher(message);
		if (reportInvite.matches()) {
			EmbedBuilder report = info.errorEmbed(event.getAuthor(), event.getGuild());
			report.setDescription("El usuario " + event.getAuthor().getName() + "#"
					+ event.getAuthor().getDiscriminator() + " ha posteado una invitación.");
			event.getMessage().delete().queue();
			event.getChannel().sendMessageEmbeds(report.build()).queue();
			return;
		}

	}
}
