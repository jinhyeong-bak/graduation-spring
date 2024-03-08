package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setFrom("dripmidcuk@gmail.com");
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendVerificationCode(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();

        String emailTemplate = "새로운 dripMind 계정 생성 프로세스를 시작해 주셔서 감사합니다."
                + "\r\n"
                + "사용자가 본인임을 확인하려고 합니다. 메시지가 표시되면 다음 확인 코드를 입력하세요.\r\n"
                + "\r\n"
                + "확인 코드: "
                + verificationCode
                + "\r\n";

        message.setTo(to);
        message.setSubject("dripMind 이메일 확인");
        message.setText(emailTemplate);

        mailSender.send(message);
    }


}
