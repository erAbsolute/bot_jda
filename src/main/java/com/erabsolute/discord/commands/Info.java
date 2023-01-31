package com.erabsolute.discord.commands;

import java.awt.Color;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class Info {

	public EmbedBuilder help(User user, Guild guild) {
		EmbedBuilder embed = _getDefaultEmbed(user, guild);
		String des = "Comandos disponibles:\n\n" + "> help: Muestra este panel de ayuda\n"
				+ "> ping: Te devuelve el ping del bot\n"
				+ "> reiniciar: Reinicia el bot, hace falta tener el rol <@&980096058820534273>\n"
				+ "> mostrarinfo: Muestra datos del usuario seleccionado\n";
		embed.setDescription(des);
		return embed;
	}

	public EmbedBuilder ping(JDA jda, User user, Guild guild) {
		long ping = jda.getGatewayPing();
		EmbedBuilder embed = _getDefaultEmbed(user, guild);
		if (ping <= 30) {
			embed.setColor(Color.green);
		} else if (ping >= 31 && ping <= 150) {
			embed.setColor(Color.yellow);
		} else {
			embed.setColor(Color.red);
		}
		embed.setDescription("La latencia es de " + ping + " ms.");
		return embed;
	}

	private EmbedBuilder _getDefaultEmbed(User user, Guild guild) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.lightGray);
		eb.setThumbnail("https://i.imgur.com/spli5GS.jpeg");
		eb.setAuthor("erAbsolute", "https://github.com/erAbsolute");
		eb.setFooter("Solicitado por: " + user.getName() + " · " + guild.getName(), user.getAvatarUrl());
		return eb;
	}
}
