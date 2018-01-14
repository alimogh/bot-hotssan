package io.coinpeeker.bot_hotssan.service;

import io.coinpeeker.bot_hotssan.common.CommonConstant;
import io.coinpeeker.bot_hotssan.module.HotssanUpdateHandler;
import io.coinpeeker.bot_hotssan.utils.AuthUtils;
import io.coinpeeker.bot_hotssan.utils.Commander;
import io.coinpeeker.bot_hotssan.utils.HttpUtils;
import io.coinpeeker.bot_hotssan.utils.MessageUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class HotssanService implements HotssanUpdateHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(HotssanService.class);

    @Value("${property.hotssan_id}")
    private String apiKey;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private Commander commander;

    @Override
    public void deleteWebhook() {
        String deleteWebhookUrl = CommonConstant.URL_TELEGRAM_BASE + apiKey + CommonConstant.METHOD_TELEGRAM_DELETE_WEBHOOK;
        try {
            httpUtils.get(deleteWebhookUrl);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public boolean setWebhook(String url) {
        deleteWebhook();

        String setWebhookUrl = CommonConstant.URL_TELEGRAM_BASE + apiKey + CommonConstant.METHOD_TELEGRAM_SET_WEBHOOK;

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("url", url + "/webhook"));

        try {
            httpUtils.post(setWebhookUrl, params);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void updateHandler(Update update) {

        // validation check
        if (update == null) {
            LOGGER.error("#$#$#$ update object is null");
            return ;
        }

        String url = CommonConstant.URL_TELEGRAM_BASE + apiKey + CommonConstant.METHOD_TELEGRAM_SENDMESSAGE;
        long chatId = update.getMessage().getChatId();
        String instruction = update.getMessage().getText();
        StringBuilder message = new StringBuilder();

        if (!AuthUtils.isAuthenticated(chatId)) {
            message.append("등록되지 않은 사용자입니다.");
        } else {
            message.append(commander.execute(instruction));
        }

        messageUtils.sendMessage(url, chatId, message.toString());
    }
}
