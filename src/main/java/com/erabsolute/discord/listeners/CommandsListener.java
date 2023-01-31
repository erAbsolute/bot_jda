package com.erabsolute.discord.listeners;

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
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

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
			EmbedBuilder embed = info.help(user, guild);
			event.getHook().sendMessageEmbeds(embed.build()).queue();
			break;
		case "ping":
			EmbedBuilder pingEmbed = info.ping(event.getJDA(), user, guild);
			event.getHook().sendMessageEmbeds(pingEmbed.build()).setEphemeral(true).queue();
			break;
		}
	}

	public void onGuildReady(GuildReadyEvent event) {
		List<CommandData> commandData = new ArrayList<>();
		commandData.add(Commands.slash("help", "Muestra los comandos disponibles"));
		commandData.add(Commands.slash("ping", "Devuelve la latencia del bot"));
		event.getGuild().updateCommands().addCommands(commandData).queue();
	}
}
