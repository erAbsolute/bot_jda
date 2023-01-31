package com.erabsolute.discord.internals;

import com.erabsolute.discord.listeners.CommandsListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class BotEngine {
	public void startBot() {
		String token = System.getenv("botToken");
		JDA api = JDABuilder.createDefault(token).addEventListeners(new CommandsListener()).build();
	}
}
