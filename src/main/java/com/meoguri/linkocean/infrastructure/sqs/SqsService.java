package com.meoguri.linkocean.infrastructure.sqs;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SqsService {

	private final SQSConnectionFactory sqsConnectionFactory;
	private final String queueName;

	public void send(final Object message) {

		Session session = null;

		try {
			// Connection 생성하기.
			SQSConnection connection = sqsConnectionFactory.createConnection();

			makeQueue(connection, queueName);

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Queue queue = session.createQueue(queueName);
			MessageProducer producer = session.createProducer(queue);

			String stringMessage = String.valueOf(message); // json deserialization
			Message textMessage = session.createTextMessage(stringMessage);
			producer.send(textMessage);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void makeQueue(SQSConnection connection, String queueName) throws Exception {
		// Get the wrapped client
		AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();

		// Create an SQS queue named MyQueue, if it doesn't already exist
		if (!client.queueExists(queueName)) {
			client.createQueue(queueName);
		}
	}
}
