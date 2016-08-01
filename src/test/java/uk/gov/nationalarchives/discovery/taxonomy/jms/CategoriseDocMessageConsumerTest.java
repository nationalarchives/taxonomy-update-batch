package uk.gov.nationalarchives.discovery.taxonomy.jms;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.nationalarchives.discovery.taxonomy.service.ProcessMessageService;

import javax.jms.JMSException;

import static org.mockito.Mockito.verify;

/**
 * Created by jcharlet on 8/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CategoriseDocMessageConsumerTest {

    @Mock
    ProcessMessageService processMessageService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testHandleMessage() throws JMSException {
        //given my messaging system set up with mocks
        CategoriseDocMessageConsumer categoriseDocMessageConsumer = new CategoriseDocMessageConsumer(processMessageService);

        //and a message containing documents to categorise
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setJMSMessageID("test id");
        message.setText("A123456;A123457");

        //when I receive a message
        categoriseDocMessageConsumer.handleMessage(message);

        //then the service start categorising those messages
        verify(processMessageService).categoriseDocuments(Mockito.anyList());
    }
}
