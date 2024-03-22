package com.example.demo.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmailServiceTest {

    private String mailSender = "test@gmail.com";
    private String mailReceiver = "user@gmail.com";

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration
                    .aConfig().withUser("test@gmail.com", "password"));

    @Autowired
    EmailService emailService;
    @Test
    void sendVerificationCode_withValidSituation_withoutThrowsException() throws Exception {
        //create random 6 digit
        Random random = new Random();
        StringBuilder verificationCode = new StringBuilder();
        for(int i = 0; i < 6; i++) {
            verificationCode.append((char)(random.nextInt(10) + '0'));
        }

        System.out.println(verificationCode);
        // when
        emailService.sendVerificationCode(mailReceiver, verificationCode.toString());

        // then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        MimeMessage receivedMessage = receivedMessages[0];

        assertEquals(mailReceiver, receivedMessage.getAllRecipients()[0].toString());
        assertEquals("dripMind 이메일 확인", receivedMessage.getSubject().trim());
    }
}