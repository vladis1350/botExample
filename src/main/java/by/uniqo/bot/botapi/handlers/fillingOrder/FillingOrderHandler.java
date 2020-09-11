package by.uniqo.bot.botapi.handlers.fillingOrder;

import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.InputMessageHandler;
import by.uniqo.bot.cache.UserDataCache;
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
 * Формирует анкету пользователя.
 */

@Slf4j
@Component
public class FillingOrderHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public FillingOrderHandler(UserDataCache userDataCache,
                               ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_ORDER)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_TOTALNUMBER);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_ORDER;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_TOTALNUMBER)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askTotalNumber");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TAPESCOLOR);
        }

        if (botState.equals(BotState.ASK_TAPESCOLOR)) {
            profileData.setTotalNumber(Integer.parseInt(usersAnswer));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askTapesColor");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_MODELNUMBER);
        }

        if (botState.equals(BotState.ASK_MODELNUMBER)) {
            profileData.setTapesColor(Integer.parseInt(usersAnswer));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askModelNumber");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_COLOROFMODELTEXT);

        }

        if (botState.equals(BotState.ASK_COLOROFMODELTEXT)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askColorOfModelText");
            profileData.setModelNumber(Integer.parseInt(usersAnswer));
            replyToUser.setReplyMarkup(getButtonsMarkup2());

        }

        if (botState.equals(BotState.ASK_SYMBOLNUMBER)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askSymbolNumber");
            profileData.setColorOfModelText(Integer.parseInt(usersAnswer));
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
        }

        if (botState.equals(BotState.ASK_NUMBEROFMEN)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askNumberOfMen");
            profileData.setSymbolNumber(Integer.parseInt(usersAnswer));
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFWOMEN);
        }

        if (botState.equals(BotState.ASK_NUMBEROFWOMEN)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askNumberOfWomen");
            profileData.setNumberOfMen(Integer.parseInt(usersAnswer));
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFTEACHERS);
        }

        if (botState.equals(BotState.ASK_NUMBEROFTEACHERS)) {
            profileData.setNumberOfWomen(Integer.parseInt(usersAnswer));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askNumberOfTeacher");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_SCHOOLNUMBER);
        }

        if (botState.equals(BotState.ASK_SCHOOLNUMBER)) {
            profileData.setNumberOfTeacher(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askSchoolNumber");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
        }

        if (botState.equals(BotState.ASK_ADDITIONALSERVICES)) {
            if (profileData.getSchoolNumber()==null) {
                profileData.setSchoolNumber(usersAnswer);
            }
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askAdditionalServices");
            replyToUser.setReplyMarkup(getInlineMessageButtons());
        }

//        if (botState.equals(BotState.ASK_LITTLEBELL)) {
//            replyToUser = messagesService.getReplyMessage(chatId, "reply.askLittleBell");
//            profileData.setAdditionalService(usersAnswer);
//            replyToUser.setReplyMarkup(getButtonsMarkup());
//        }
//
        if (botState.equals(BotState.ASK_LITTLEBELLCOLOR)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askLittleBellColor");
//            profileData.setLittleBell(usersAnswer);
            profileData.setLittleBellColor(Integer.parseInt(usersAnswer));
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
        }
//
//        if (botState.equals(BotState.ASK_BIGBELL)) {
//            replyToUser = messagesService.getReplyMessage(chatId, "reply.askBigBell");
//            profileData.setLittleBellColor(Integer.parseInt(usersAnswer));
//            replyToUser.setReplyMarkup(getButtonsMarkup());
//        }
//
        if (botState.equals(BotState.ASK_BIGBELLCOLOR)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askBigBellColor");
//            profileData.setBigBell(usersAnswer);
            profileData.setBigBellColor(Integer.parseInt(usersAnswer));
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
        }
//
//        if (botState.equals(BotState.ASK_STARS)) {
//            profileData.setBigBellColor(Integer.parseInt(usersAnswer));
//            replyToUser = messagesService.getReplyMessage(chatId, "reply.askStars");
//            replyToUser.setReplyMarkup(getButtonsMarkup());
//        }
//
//        if (botState.equals(BotState.ASK_SCROLL)) {
//            profileData.setStars(usersAnswer);
//            replyToUser = messagesService.getReplyMessage(chatId, "reply.askScroll");
//            replyToUser.setReplyMarkup(getButtonsMarkup());
//        }
//
        if (botState.equals(BotState.ASK_SCROLLCOLOR)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askScrollColor");
//            profileData.setScroll(usersAnswer);
            profileData.setScrollColor(Integer.parseInt(usersAnswer));
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
        }
//
//        if (botState.equals(BotState.ASK_RIBBON)) {
//            replyToUser = messagesService.getReplyMessage(chatId, "reply.askRibbon");
//            profileData.setScrollColor(Integer.parseInt(usersAnswer));
//            replyToUser.setReplyMarkup(getButtonsMarkup());
//        }
//
        if (botState.equals(BotState.ASK_RIBBONCOLOR)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askRibbonColor");
//            profileData.setRibbon(usersAnswer);
            profileData.setRibbonColor(Integer.parseInt(usersAnswer));
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
        }
//
//        if (botState.equals(BotState.ASK_BOWTIE)) {
//            replyToUser = messagesService.getReplyMessage(chatId, "reply.askBowtie");
//            profileData.setRibbonColor(Integer.parseInt(usersAnswer));
//            replyToUser.setReplyMarkup(getButtonsMarkup());
//        }
//
        if (botState.equals(BotState.ASK_BOWTIECOLOR)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askBowtieColor");
//            profileData.setBowtie(usersAnswer);
            profileData.setBowtieColor(Integer.parseInt(usersAnswer));
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
        }

        if (botState.equals(BotState.ASK_CREDENTIALS)) {
            profileData.setCredentials(usersAnswer);
//            profileData.setBowtieColor(Integer.parseInt(usersAnswer));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askPhoneNumber");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_URLVK);
        }

        if (botState.equals(BotState.ASK_URLVK)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askUrlVK");
            profileData.setPhoneNumber(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DELIVERYADDRESS);
        }

        if (botState.equals(BotState.ASK_DELIVERYADDRESS)) {
            profileData.setUrlVK(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askDeliveryAddress");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_COMMENTSTOORDER);
        }

        if (botState.equals(BotState.ASK_COMMENTSTOORDER)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askCommentsToOrder");
            profileData.setDeliveryAddress(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ORDER_FILLED);
        }

        if (botState.equals(BotState.ORDER_FILLED)) {
            profileData.setCommentsToOrder(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.OrderFilled");
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return replyToUser;
    }


    private InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonStars = new InlineKeyboardButton().setText("Стразы");
        InlineKeyboardButton buttonScroll = new InlineKeyboardButton().setText("Приветственный свиток");
        InlineKeyboardButton buttonBigBell = new InlineKeyboardButton().setText("Большой колокольчик");
        InlineKeyboardButton buttonLittleBell = new InlineKeyboardButton().setText("Маленький колокольчик");
        InlineKeyboardButton buttonRibbon = new InlineKeyboardButton().setText("Бант");
        InlineKeyboardButton buttonBowtie = new InlineKeyboardButton().setText("Бабочка");
        InlineKeyboardButton buttonNext = new InlineKeyboardButton().setText("Нет");

        //Every button must have callBackData, or else not work !
        buttonStars.setCallbackData("buttonStars");
        buttonScroll.setCallbackData("buttonScroll");
        buttonBigBell.setCallbackData("buttonBigBell");
        buttonLittleBell.setCallbackData("buttonLittleBell");
        buttonRibbon.setCallbackData("buttonRibbon");
        buttonBowtie.setCallbackData("buttonBowtie");
        buttonNext.setCallbackData("buttonNext");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonStars);
        keyboardButtonsRow1.add(buttonScroll);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(buttonBigBell);
        keyboardButtonsRow2.add(buttonLittleBell);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(buttonRibbon);
        keyboardButtonsRow3.add(buttonBowtie);

        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow4.add(buttonNext);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup getButtonsMarkup2() {
        InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonOne = new InlineKeyboardButton().setText("Золотая фольга");
        InlineKeyboardButton button2 = new InlineKeyboardButton().setText("Серебрянная фольга");
        InlineKeyboardButton button3 = new InlineKeyboardButton().setText("Красная фольга");
        InlineKeyboardButton button4 = new InlineKeyboardButton().setText("Синяя фольга");
        InlineKeyboardButton button5 = new InlineKeyboardButton().setText("Черный матовый");


        //Every button must have callBackData, or else not work !
        buttonOne.setCallbackData("buttonOne");
        button2.setCallbackData("button2");
        button3.setCallbackData("button3");
        button4.setCallbackData("button4");
        button5.setCallbackData("button5");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonOne);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(button2);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(button3);

        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow4.add(button4);

        List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
        keyboardButtonsRow5.add(button5);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        rowList.add(keyboardButtonsRow5);


        inlineKeyboardMarkup2.setKeyboard(rowList);

        return inlineKeyboardMarkup2;
    }


}