package by.uniqo.bot.botapi.handlers.startOrder;

import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.InputMessageHandler;
import by.uniqo.bot.service.ReplyMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Спрашивает пользователя- хочет ли он сделать заказ. Меню первого уровня
 */

@Slf4j
@Component
public class StartOrderHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;

    public StartOrderHandler(ReplyMessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.ASK_START;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();

        SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.askStart");
        replyToUser.setReplyMarkup(getInlineMessageButtons());

        return replyToUser;
    }

    private InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonStartOrder = new InlineKeyboardButton().setText("Оформить заказ");
        InlineKeyboardButton buttonPaymentAndDelivery = new InlineKeyboardButton().setText("Оплата и доставка");
        InlineKeyboardButton buttonPromotionsAndDiscounts = new InlineKeyboardButton().setText("СКИДКИ И АКЦИИ");
        InlineKeyboardButton buttonCallForManager = new InlineKeyboardButton().setText("Связаться с менеджером");

        //Every button must have callBackData, or else not work !
        buttonStartOrder.setCallbackData("buttonStartOrder");
        buttonPaymentAndDelivery.setCallbackData("buttonPaymentAndDelivery");
        buttonPromotionsAndDiscounts.setCallbackData("buttonPromotionsAndDiscounts");
        buttonCallForManager.setCallbackData("buttonCallForManager");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonStartOrder);
        keyboardButtonsRow1.add(buttonPaymentAndDelivery);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonPromotionsAndDiscounts);
        keyboardButtonsRow2.add(buttonCallForManager);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


}