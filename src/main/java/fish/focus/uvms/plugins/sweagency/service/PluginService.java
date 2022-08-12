package fish.focus.uvms.plugins.sweagency.service;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fish.focus.schema.exchange.common.v1.AcknowledgeTypeType;
import fish.focus.schema.exchange.common.v1.CommandType;
import fish.focus.schema.exchange.common.v1.KeyValueType;
import fish.focus.schema.exchange.common.v1.ReportType;
import fish.focus.schema.exchange.common.v1.ReportTypeType;
import fish.focus.schema.exchange.movement.v1.MovementPoint;
import fish.focus.schema.exchange.movement.v1.MovementType;
import fish.focus.schema.exchange.service.v1.SettingListType;
import fish.focus.uvms.plugins.sweagency.StartupBean;
import fish.focus.uvms.plugins.sweagency.producer.RemoteProducer;

@LocalBean
@Stateless
public class PluginService {

    @EJB
    StartupBean startupBean;
    
    @Inject
    private RemoteProducer remoteProducer;
    
    @Inject
    @Metric(name = "hav_outgoing", absolute = true)
    private Counter havOutgoing;

    private static final Logger LOG = LoggerFactory.getLogger(PluginService.class);

    public AcknowledgeTypeType setReport(ReportType report) {
        LOG.debug(startupBean.getRegisterClassName() + ".report(" + report.getType().name() + ")");
        LOG.debug("timestamp: " + report.getTimestamp());
        MovementType movementType = report.getMovement();
        if (movementType != null && ReportTypeType.MOVEMENT.equals(report.getType())) {
            MovementPoint pos = movementType.getPosition();
            if (pos != null) {
                boolean sentToMq = remoteProducer.sendMessage(movementType);
                LOG.debug("sentToMq: {}", sentToMq);
                if (!sentToMq) {
                    return AcknowledgeTypeType.NOK;
                }
                havOutgoing.inc();
            }
        }
        return AcknowledgeTypeType.OK;
    }

    public AcknowledgeTypeType setCommand(CommandType command) {
        return AcknowledgeTypeType.NOK;
    }

    public AcknowledgeTypeType setConfig(SettingListType settings) {
        LOG.info(startupBean.getRegisterClassName() + ".setConfig()");
        try {
            for (KeyValueType values : settings.getSetting()) {
                LOG.debug("Setting [ " + values.getKey() + " : " + values.getValue() + " ]");
                startupBean.getSettings().put(values.getKey(), values.getValue());
            }
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            LOG.error("Failed to set config in {}", startupBean.getRegisterClassName());
            return AcknowledgeTypeType.NOK;
        }

    }

    public AcknowledgeTypeType start() {
        LOG.info(startupBean.getRegisterClassName() + ".start()");
        try {
            startupBean.setIsEnabled(Boolean.TRUE);
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            startupBean.setIsEnabled(Boolean.FALSE);
            LOG.error("Failed to start {}", startupBean.getRegisterClassName());
            return AcknowledgeTypeType.NOK;
        }

    }

    public AcknowledgeTypeType stop() {
        LOG.info(startupBean.getRegisterClassName() + ".stop()");
        try {
            startupBean.setIsEnabled(Boolean.FALSE);
            return AcknowledgeTypeType.OK;
        } catch (Exception e) {
            startupBean.setIsEnabled(Boolean.TRUE);
            LOG.error("Failed to stop {}", startupBean.getRegisterClassName());
            return AcknowledgeTypeType.NOK;
        }
    }
}