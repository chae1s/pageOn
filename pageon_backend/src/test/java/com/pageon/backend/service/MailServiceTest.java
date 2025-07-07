package com.pageon.backend.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@ActiveProfiles("test")
@DisplayName("mailService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @InjectMocks
    private MailService mailService;
    @Mock
    private JavaMailSender javaMailSender;
    
    @Test
    @DisplayName("임시 비밀번호 메일이 정상적으로 전송된다.")
    void sendTemporaryPassword_shouldSendMail() {
        // given
        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        String email = "test@mail.com";
        String tempPassword = "testPassword";

        //when
        mailService.sendTemporaryPassword(email, tempPassword);
        
        // then
        verify(javaMailSender).send(mailCaptor.capture());

        SimpleMailMessage simpleMailMessage = mailCaptor.getValue();

        assertEquals(email, simpleMailMessage.getTo()[0]);
        assertEquals("[pageOn]임시 비밀번호 안내", simpleMailMessage.getSubject());
        assertTrue(simpleMailMessage.getText().contains(tempPassword));
        
    }
    
    

}