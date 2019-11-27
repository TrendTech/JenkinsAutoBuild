package com.ajiang.auto;

import com.ajiang.auto.consts.Consts;
import com.ajiang.auto.model.RequestType;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by dmatafonov on 02.05.2017.
 */
public class TeleMessage {

	private static Logger logger = LoggerFactory.getLogger(TeleMessage.class);

	public static void sendMessageRequest(int chat_id, String message) throws IOException {
		final StringBuilder sendMessage = new StringBuilder(
				"https://api.telegram.org/bot" + Consts.BOT_API_KEY + "/" + RequestType.sendMessage.name());
		sendMessage.append("?chat_id=").append(chat_id);
		sendMessage.append("&text=").append(URLEncoder.encode(message,"UTF-8"));
		logger.info(sendMessage.toString());
		HttpGet sendMessageRequest = new HttpGet(sendMessage.toString());
		HttpClient client = HttpClientBuilder.create().build();
		client.execute(sendMessageRequest);
	}

	public static void main(String[] args) throws IOException {
		TeleMessage.sendMessageRequest(Consts.BOT_CHAT_ID,"here is my text.\n and this is a new line \n another new line");
	}
}

