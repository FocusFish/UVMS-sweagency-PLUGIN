package fish.focus.uvms.plugins.sweagency.producer;

import java.time.Instant;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fish.focus.schema.exchange.movement.v1.MovementType;
import se.havochvatten.service.library.messaging.vessel_position_events.Operation;
import se.havochvatten.service.library.messaging.vessel_position_events.PositionEvent;

@RequestScoped
public class RemoteProducer {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteProducer.class);

    private static final String TOPIC = "vessel-positions";
    private static final Long TIME_TO_LIVE = 4*24*60*60*1000L; // 96h

    @Resource(mappedName = "java:/HavRemote")
    private ConnectionFactory connectionFactory;

    private Jsonb jsonb = JsonbBuilder.create();

    public boolean sendMessage(MovementType movement) {
        try (Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            Topic positionsTopic = session.createTopic(TOPIC);
            MessageProducer producer = session.createProducer(positionsTopic);
            PositionEvent event = createPositionEvent(movement);
            TextMessage message = session.createTextMessage(jsonb.toJson(event));
            message.setStringProperty("version", "1.0.0");
            message.setStringProperty("sentAt", Instant.now().toString());
            producer.send(message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, TIME_TO_LIVE);
        } catch (Exception e) {
            LOG.error("Failed to send position ({}) to remote topic!", movement.getGuid(), e);
            return false;
        }
        return true;
    }

    private PositionEvent createPositionEvent(MovementType movement) {
        PositionEvent event = new PositionEvent();
        event.setId(movement.getGuid());
        event.setOperation(Operation.CREATE);
        return event;
    }
}
