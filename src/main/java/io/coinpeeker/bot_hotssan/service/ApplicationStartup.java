package io.coinpeeker.bot_hotssan.service;

import io.coinpeeker.bot_hotssan.common.CommonConstant;
import io.coinpeeker.bot_hotssan.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HotssanService.class);

    @Autowired
    private MessageUtils messageUtils;

    @Value("${property.hotssan_id}")
    private String apiKey;

    @Value("${property.env")
    private String env;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        String url = CommonConstant.URL_TELEGRAM_BASE + apiKey + CommonConstant.METHOD_TELEGRAM_SENDMESSAGE;

        long chatId = 0L;
        if (StringUtils.equals(env, "dev")) {
            chatId = -294606763L;
        } else if (StringUtils.equals(env, "real")) {
            chatId = -300048567L;
        }

        LOGGER.info("#$#$#$ 새로운 build : {}, chatId : {}", url, chatId);
        messageUtils.sendMessage(url, chatId, "새로운 Build 가 감지되었습니다.");
    }
}
