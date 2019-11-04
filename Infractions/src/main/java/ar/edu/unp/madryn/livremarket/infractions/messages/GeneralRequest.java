package ar.edu.unp.madryn.livremarket.infractions.messages;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.MessageCommonFields;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.messages.Operations;
import ar.edu.unp.madryn.livremarket.common.messages.Results;
import ar.edu.unp.madryn.livremarket.common.messages.types.Request;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;

public class GeneralRequest extends Request {
    private static final double DEFAULT_INFRACTION_PROBABILITY = 0.5;

    @Setter
    private CommunicationHandler communicationHandler;
    @Setter
    private ConfigurationSection simulationConfiguration;

    @Override
    public void execute(String operation, Map<String, String> data) {
        System.out.println("Llego la operacion general '" + operation + "' con los datos: " + data);
        switch (operation) {
            case Operations.GET_INFRACTIONS_OPERATION:
                Map<String, String> responseData = new HashMap<>();

                String purchaseID = data.get(MessageCommonFields.PURCHASE_ID);
                responseData.put(Definitions.INFORMATION_REFERENCE_KEY, Results.INFRACTIONS_REFERENCE_ID);
                boolean hasInfraction = this.hasInfraction();
                responseData.put(MessageCommonFields.HAS_INFRACTIONS, Boolean.toString(hasInfraction));
                if (!StringUtils.isEmpty(purchaseID)) {
                    System.err.println("Error: No se pudo obtener el ID de la compra!");
                    responseData.put(MessageCommonFields.PURCHASE_ID, purchaseID);
                }

                System.out.println("Resultado de infracciones: '" + hasInfraction + "' (ID =" + purchaseID + ")");
                System.out.println("Informando infracciones (ID =" + purchaseID + ")");

                communicationHandler.sendMessage(MessageType.RESULT, Definitions.PURCHASES_SERVER_NAME, responseData);
                communicationHandler.sendMessage(MessageType.RESULT, Definitions.PRODUCTS_SERVER_NAME, responseData);

                break;
            default:
                System.err.println("Error: Operacion '" + operation + "' no reconocida!");
        }
    }

    private boolean hasInfraction() {
        String probabilityString = simulationConfiguration.getValue("infraction_probability");
        double probability = NumberUtils.toDouble(probabilityString, DEFAULT_INFRACTION_PROBABILITY);

        return (Math.random() < probability);
    }
}
