package com.tfit.BdBiProcSrvShEduOmc.config;

/**
 * @author user
 * @since 2020/6/17 16:12
 */

import io.sentry.Sentry;
import io.sentry.event.Event;
import io.sentry.event.helper.ShouldSendEventCallback;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SentryConfiguration {

    @PostConstruct
    void init() {
        initSentry();
    }

    private static void initSentry() {
        Sentry.init("https://43c3fe06d48c4072ae40eebd2914c658@sentry.sunshinelunch.com/24?environment=local");
        Sentry.getStoredClient().addShouldSendEventCallback(new ShouldSendEventCallback() {
            @Override
            public boolean shouldSend(Event event) {
                if (event.getMessage().contains("foo")) {
                    return false;
                }
                return true;
            }
        });
    }

}