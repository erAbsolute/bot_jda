package com.erabsolute.discord.internals;

import java.util.ArrayList;
import java.util.List;

import com.erabsolute.discord.listeners.CommandsListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class BotEngine {
	public void startBot() {
		List<GatewayIntent> intents = new ArrayList<>();
		intents.add(GatewayIntent.MESSAGE_CONTENT);
		String token = System.getenv("botToken");
		JDA api = JDABuilder.createDefault(token).addEventListeners(new CommandsListener()).enableIntents(intents)
				.build();
	}
}
