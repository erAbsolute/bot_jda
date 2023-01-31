package com.erabsolute.discord;

import com.erabsolute.discord.internals.BotEngine;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	BotEngine botEngine = new BotEngine();
		botEngine.startBot();
    }
}
