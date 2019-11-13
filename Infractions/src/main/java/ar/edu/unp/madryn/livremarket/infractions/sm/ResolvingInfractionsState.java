package ar.edu.unp.madryn.livremarket.infractions.sm;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;

public class ResolvingInfractionsState extends State {
    private static final double DEFAULT_INFRACTION_PROBABILITY = 0.5;

    @Setter
    private ConfigurationSection simulationConfiguration;

    public ResolvingInfractionsState() {
        super("resolving_infractions");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Logging.info("Resolviendo infracciones! (ID = " + purchaseID + ")");

        boolean hasInfraction = this.hasInfraction();

        data.put(MessageCommonFields.HAS_INFRACTIONS, Boolean.toString(hasInfraction));

        Logging.info("Resultado de las infracciones: '" + hasInfraction + "' (ID = " + purchaseID + ")");

        return true;
    }

    private boolean hasInfraction() {
        String probabilityString = simulationConfiguration.getValue("infraction_probability");
        double probability = NumberUtils.toDouble(probabilityString, DEFAULT_INFRACTION_PROBABILITY);

        return (Math.random() < probability);
    }
}
