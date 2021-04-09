package com.springboot.service;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
	
	@Value("${test.email.from}")
	private String fromEmail;
	
	@Value("${test.email.password}")
	private String emailPassword;
	
	@Value("${test.email.subject}")
	private String subject;
	
	@Value("${test.email.body}")
	private String body;
	
	
	@Override
	public void sendMail(String email) {//throws AddressException, MessagingException, IOException {
		   Properties props = new Properties();
		   props.put("mail.smtp.auth", "true");
		   props.put("mail.smtp.starttls.enable", "true");
		   props.put("mail.smtp.host", "smtp.gmail.com");
		   props.put("mail.smtp.port", "587");
		   
		   Session session = Session.getInstance(props, new javax.mail.Authenticator() {
		      protected PasswordAuthentication getPasswordAuthentication() {
		         return new PasswordAuthentication(fromEmail, emailPassword);
		      }
		   });
		   try
		   {
		   Message msg = new MimeMessage(session);
		   msg.setFrom(new InternetAddress(emailPassword, false));

		   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
		   msg.setSubject(subject);
		   msg.setContent("Congrats email", "text/html");
		   msg.setSentDate(new Date());

		   MimeBodyPart messageBodyPart = new MimeBodyPart();
		   messageBodyPart.setContent(body, "text/html");

		   Multipart multipart = new MimeMultipart();
		   multipart.addBodyPart(messageBodyPart);

		   msg.setContent(multipart);
		   Transport.send(msg);  
		   }
		   catch(Exception e) {
			   System.err.println(e.getMessage());;
		   }
		}
}
