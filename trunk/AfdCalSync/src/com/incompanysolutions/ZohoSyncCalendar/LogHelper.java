package com.incompanysolutions.ZohoSyncCalendar;

import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class LogHelper {

	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_PORT = "465";
	private static final String emailMsgTxt = "Test Message Contents";
	private static final String emailSubjectTxt = "A test from gmail";
	private static final String emailFromAddress = "";
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private static final String[] sendTo = { ""};
	
	
	public static void postMail(String subject, String message) 	{
		try{
	    String recipients[ ] = {"soporte@incompanysolutions.com"};
	    String from = "noReply@perfcircle.com";
	     //Set the host smtp address
	     
	     Properties props = new Properties();
	     props.put("mail.smtp.host", SMTP_HOST_NAME);
	     props.put("mail.smtp.auth", "true");
	     props.put("mail.debug", "true");
	     props.put("mail.smtp.port", SMTP_PORT);
	     props.put("mail.smtp.socketFactory.port", SMTP_PORT);
	     props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
	     props.put("mail.smtp.socketFactory.fallback", "false");
	     
	     
	     Session session = Session.getDefaultInstance(props,
	    		 new javax.mail.Authenticator() {

	    		 protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
	    		 return new javax.mail.PasswordAuthentication("rrodriguez@incompanysolutions.com", "monomono");
	    		 }
	    		 });

	     session.setDebug(false);

	    // create a message
	    Message msg = new MimeMessage(session);

	    // set the from and to address
	    InternetAddress addressFrom = new InternetAddress(from);
	    msg.setFrom(addressFrom);

	    InternetAddress[] addressTo = new InternetAddress[recipients.length]; 
	    for (int i = 0; i < recipients.length; i++)
	    {
	        addressTo[i] = new InternetAddress(recipients[i]);
	    }
	    msg.setRecipients(Message.RecipientType.TO, addressTo);
	   

	    // Optional : You can also set your custom headers in the Email if you Want
	    msg.addHeader("MyHeaderName", "myHeaderValue");

	    // Setting the Subject and Content Type
	    msg.setSubject(subject);
	    msg.setContent(message, "text/plain");
	    Transport.send(msg);
		}
	    catch (Exception e) {
			// TODO: handle exception
		}
	    
		}
}
