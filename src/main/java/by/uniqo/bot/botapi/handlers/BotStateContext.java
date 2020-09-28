package by.uniqo.bot.botapi.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines message handlers for each state.
 */
@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.FILLING_ORDER);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case ASK_TOTALNUMBER:
            case ASK_TAPESCOLOR:
            case ASK_MODELNUMBER:
            case ASK_COLOROFMODELTEXT:
            case ASK_SYMBOLNUMBER:
            case ASK_NUMBEROFMEN:
            case ASK_NUMBEROFWOMEN:
            case ASK_NUMBEROFTEACHERS:
            case ASK_SCHOOLNUMBER:
            case ASK_ADDITIONALSERVICES:
            case ASK_LITTLEBELL:
            case ASK_LITTLEBELLCOLOR:
            case ASK_BIGBELL:
            case ASK_BIGBELLCOLOR:
            case ASK_STARS:
            case ASK_SCROLL:
            case ASK_SCROLLCOLOR:
            case ASK_RIBBON:
            case ASK_OPTION_RIBBON:
            case ASK_INDEX:
            case ASK_CITY:
            case ASK_STREET:
            case ASK_HOME:
            case ASK_APARTMENT:
            case ASK_OPTION_SYMBOL_LAYOUT:
            case ASK_RIBBONCOLOR:
            case ASK_BOWTIE:
            case ASK_BOWTIECOLOR:
            case ASK_CREDENTIALS:
            case ASK_PHONENUMBER:
            case ASK_URLVK:
            case ASK_DELIVERYADDRESS:
            case ASK_COMMENTSTOORDER:
            case FILLING_ORDER:
            case ORDER_FILLED:
            case ASK_UPLOAD_FILE_LIST_WOMEN:
            case ASK_UPLOAD_FILE_LIST_MEN:
            case ASK_TEACHER_STANDARD_RIBBON:
                return true;
            default:
                return false;
        }
    }


}