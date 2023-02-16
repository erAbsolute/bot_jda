package com.erabsolute.discord.listeners;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.erabsolute.discord.commands.Info;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;

public class CommandsListener extends ListenerAdapter {
	Logger logs = LoggerFactory.getLogger(CommandsListener.class);

	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		Info info = new Info();
		String command = event.getName();
		User user = event.getUser();
		Guild guild = event.getGuild();
		event.deferReply().setEphemeral(true).queue();
		switch (command) {
		case "help":
			EmbedBuilder helpEmbed = info.help(user, guild);
			event.getHook().sendMessageEmbeds(helpEmbed.build()).queue();
			break;
		case "ping":
			EmbedBuilder pingEmbed = info.ping(event.getJDA(), user, guild);
			event.getHook().sendMessageEmbeds(pingEmbed.build()).setEphemeral(true).queue();
			break;
		case "qrfy":
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				baos = info.generateQRCodeImage(event.getOptions().get(0).getAsString());
			} catch (Exception e) {
				event.getHook().sendMessageEmbeds(info.errorEmbed(user, guild).build()).setEphemeral(true).queue();
				break;
			}
			EmbedBuilder qrfyEmbed = info.qrfy(user, guild);
			event.getHook().sendMessageEmbeds(qrfyEmbed.build()).setEphemeral(true)
					.addFiles(FileUpload.fromData(baos.toByteArray(), "qr.png")).queue();
			break;
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
}
