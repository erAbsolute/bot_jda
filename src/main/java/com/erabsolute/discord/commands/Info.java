package com.erabsolute.discord.commands;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class Info {

	Logger logger = LoggerFactory.getLogger(this.getClass());

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
		EmbedBuilder embed = _getDefaultEmbed(user, guild);
		long ping = jda.getGatewayPing();
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

	public EmbedBuilder qrfy(User user, Guild guild) {
		EmbedBuilder embed = _getDefaultEmbed(user, guild);
		embed.setImage("attachment://qr.png");
		embed.setThumbnail(null);
		return embed;
	}

	public EmbedBuilder chatGpt(User user, Guild guild, String message) {
		EmbedBuilder embed = _getDefaultEmbed(user, guild);
		OpenAiService chatGpt = new OpenAiService(System.getenv("openAiToken"), Duration.ofSeconds(180));
		List<ChatMessage> messageList = new ArrayList<>();

		messageList.add(new ChatMessage("user", message));
		ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder().messages(messageList)
				.model("gpt-3.5-turbo").build();
		ChatCompletionResult chatResponse = chatGpt.createChatCompletion(chatCompletionRequest);
		String chatResponseContent = "> ChatGPT dice:"
				.concat(chatResponse.getChoices().get(0).getMessage().getContent());

		embed.setThumbnail(null);
		embed.setColor(Color.green);
		embed.setDescription(chatResponseContent);

		logger.info("{} tokens have been used on this call.", chatResponse.getUsage().getTotalTokens());
		return embed;
	}

	public ByteArrayOutputStream generateQRCodeImage(String barcodeText) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QRCodeWriter barcodeWriter = new QRCodeWriter();
		File logoFile = new File("resources/logo.png");
		Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 4096, 4096, hints);
		BufferedImage logo = ImageIO.read(logoFile);
		BufferedImage qr = MatrixToImageWriter.toBufferedImage(bitMatrix);
		BufferedImage combined = new BufferedImage(4096, 4096, BufferedImage.TYPE_INT_ARGB);
		// paint both images, preserving the alpha channels
		Graphics g = combined.getGraphics();
		g.drawImage(qr, 0, 0, null);
		g.drawImage(logo, 1488, 1488, null);
		g.fillRect(20, 20, 100, 100);
		g.dispose();

		ImageIO.write(combined, "png", baos);
		return baos;
	}

	public EmbedBuilder info(User user, Guild guild, String userRequested) {
		EmbedBuilder embed = _getDefaultEmbed(user, guild);
		User userToPrint = user;
		embed.setTitle("Info del usuario:");
		
		if (StringUtils.isNotBlank(userRequested)) {
			List<Member> users = guild.getMembersByName(userRequested, true);
			if (users.isEmpty()) {
				embed.setColor(Color.red);
				embed.setDescription("No se ha encontrado al usuario " + userRequested + ".");
			} else {
				userToPrint = users.get(0).getUser();
			}
		}
		
		embed.setThumbnail(userToPrint.getAvatarUrl());
		embed.setDescription("> Usuario: " + userToPrint.getAsMention() +
				"\n > ID del usuario " + userToPrint.getId());
		return embed;
	}
	
	public EmbedBuilder errorEmbed(User user, Guild guild) {
		EmbedBuilder embed = _getDefaultEmbed(user, guild);
		embed.setColor(Color.red);
		embed.setDescription("El comando o los parámetros introducidos, no son válidos.");
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
