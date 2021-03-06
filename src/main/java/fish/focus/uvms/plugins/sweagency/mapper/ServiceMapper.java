package fish.focus.uvms.plugins.sweagency.mapper;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fish.focus.schema.exchange.plugin.types.v1.PluginType;
import fish.focus.schema.exchange.service.v1.CapabilityListType;
import fish.focus.schema.exchange.service.v1.CapabilityType;
import fish.focus.schema.exchange.service.v1.CapabilityTypeType;
import fish.focus.schema.exchange.service.v1.ServiceType;
import fish.focus.schema.exchange.service.v1.SettingListType;
import fish.focus.schema.exchange.service.v1.SettingType;

public class ServiceMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceMapper.class);
    
    public static ServiceType getServiceType(String serviceClassName, String fluxDisplayName, String description, PluginType fluxType, String responseMessageName) {
        if (responseMessageName == null) {
            throw new IllegalArgumentException("Response message must be provided!");
        }
        if (serviceClassName == null) {
            throw new IllegalArgumentException("ServiceClassName message must be provided!");
        }
        ServiceType serviceType = new ServiceType();
        serviceType.setDescription(description);
        serviceType.setName(fluxDisplayName);
        serviceType.setServiceClassName(serviceClassName);
        serviceType.setServiceResponseMessageName(responseMessageName);
        serviceType.setPluginType(fluxType);
        return serviceType;
    }

    public static SettingListType getSettingsListTypeFromMap(ConcurrentHashMap<String, String> settings) {
        SettingListType settingListType = new SettingListType();
        for (Map.Entry<String, String> tmp : settings.entrySet()) {
            SettingType setting = new SettingType();
            setting.setKey(tmp.getKey());
            setting.setValue(tmp.getValue());
            settingListType.getSetting().add(setting);
        }
        return settingListType;
    }

    public static CapabilityListType getCapabilitiesListTypeFromMap(ConcurrentHashMap<String, String> capabilities) {
        CapabilityListType capabilityListType = new CapabilityListType();
        for (Map.Entry<String, String> tmp : capabilities.entrySet()) {
            CapabilityType setting = new CapabilityType();

            try {
                setting.setType(CapabilityTypeType.valueOf(tmp.getKey()));
            } catch (Exception e) {
                LOG.error("Error when parsing to Enum type from String KEY: {}", tmp.getKey());
            }
            setting.setValue(tmp.getValue());
            capabilityListType.getCapability().add(setting);
        }
        return capabilityListType;
    }

    public static void mapToMapFromProperties(ConcurrentHashMap<String, String> map, Properties props, String registerClassName) {
        for (Object col : props.keySet()) {
            if (col.getClass().isAssignableFrom(String.class)) {
                String keyString = (String) col;
                if (registerClassName != null) {
                    keyString = registerClassName.concat("." + keyString);
                }
                String valueString = (String) props.get(col);
                map.put(keyString, valueString);
            }
        }
    }

}