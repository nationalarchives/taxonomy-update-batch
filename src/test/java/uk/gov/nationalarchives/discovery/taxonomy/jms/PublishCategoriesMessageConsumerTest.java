package uk.gov.nationalarchives.discovery.taxonomy.jms;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.nationalarchives.discovery.taxonomy.service.ProcessMessageService;

import javax.jms.JMSException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jcharlet on 8/1/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PublishCategoriesMessageConsumerTest {

    @Mock
    ProcessMessageService processMessageService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testHandleMessage() throws JMSException {
        //given my messaging system set up with mocks
        PublishCategoriesMessageConsumer publishCategoriesMessageConsumer = new PublishCategoriesMessageConsumer(processMessageService);

        //and a message containing categories to publish
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setJMSMessageID("test id");
        message.setText("C00001;C00002");

        //when I receive a message
        publishCategoriesMessageConsumer.handleMessage(message);

        //then the service start publishing each of those categories
        verify(processMessageService, times(1)).publishCategory(Matchers.eq("C00001"));
        verify(processMessageService, times(1)).publishCategory(Matchers.eq("C00002"));
    }
}
