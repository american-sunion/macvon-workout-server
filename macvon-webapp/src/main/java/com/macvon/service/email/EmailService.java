package com.macvon.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.macvon.domain.email.Mail;
import com.macvon.utils.AppUtils;

/**
 * send email....
 * 
 * @author xunwu
 *
 */
@Service
public class EmailService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SpringTemplateEngine templateEngine;
	@Autowired
	private Environment env;

	@Value("${email.digest}")
	private String CONFIGSET;

	@Async
	public void sendMessage(Mail mail) {
		try {
			String accesskey = AppUtils.getConfigVal("accesskey", env);
			String secretKey = AppUtils.getConfigVal("secretkey", env);

			AWSCredentials credentials = new BasicAWSCredentials(accesskey, secretKey);
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					.withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials))
					.build();
			Context context = new Context();
			context.setVariables(mail.getModel());

			String SUBJECT = mail.getSubject();
			String HTMLBODY = templateEngine.process(mail.getTemplate(), context);
			String TO = mail.getTo();
			String FROM = mail.getFrom();
			SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(TO))
					.withMessage(new Message()
							.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(HTMLBODY)))
							// .withText(new Content()
							// .withCharset("UTF-8").withData(TEXTBODY)))
							.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
					.withSource(FROM)
					.withConfigurationSetName(CONFIGSET);
			client.sendEmail(request);
			logger.info("Email sent to " + TO+ "!");
		} catch (Exception ex) {
			System.out.println("The email was not sent. Error message: " + ex.getMessage());
		}
	}

}
