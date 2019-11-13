package ar.edu.unp.madryn.livremarket.deliveries.sm;

import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.sm.State;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

public class CalculatingCostState extends State {
    private static final double DEFAULT_MAX_DELIVERY_COST = 100.0;

    @Setter
    private ConfigurationSection simulationConfiguration;

    public CalculatingCostState() {
        super("calculating_cost");
    }

    @Override
    public Boolean reset(Map<String, String> data) {
        return true;
    }

    @Override
    public Boolean process(Map<String, String> data) {
        String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);

        Logging.info("Calculando costo de envio! (ID = " + purchaseID + ")");

        double cost = this.calculateCost();

        data.put(MessageCommonFields.DELIVERY_COST, String.valueOf(cost));

        Logging.info("Costo calculado: '" + cost + "' (ID = " + purchaseID + ")");

        return true;
    }

    private double calculateCost() {
        String probabilityString = simulationConfiguration.getValue("max_delivery_cost");
        double probability = NumberUtils.toDouble(probabilityString, DEFAULT_MAX_DELIVERY_COST);

        DecimalFormat df = new DecimalFormat("#.##");

        df.setRoundingMode(RoundingMode.FLOOR);

        return new Double(df.format(Math.random() * probability));
    }
}
