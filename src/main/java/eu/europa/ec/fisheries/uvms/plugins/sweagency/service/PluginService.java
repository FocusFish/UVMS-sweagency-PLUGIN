package eu.europa.ec.fisheries.uvms.plugins.sweagency.service;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.CommandType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ReportType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.ReportTypeType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.SettingListType;
import eu.europa.ec.fisheries.uvms.plugins.sweagency.StartupBean;
import eu.europa.ec.fisheries.uvms.plugins.sweagency.dto.Movement;
import eu.europa.ec.fisheries.uvms.plugins.sweagency.mapper.MovementMapper;

@LocalBean
@Stateless
public class PluginService {

    @EJB
    StartupBean startupBean;
    
    @Inject
    private MessageRestClient restClient;
    
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
                Movement movement = MovementMapper.mapToMovement(movementType);
                int status = restClient.postMessage(movement, report.getRecipientInfo());
                LOG.debug("status: " + status);
                if (status != 200) {
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