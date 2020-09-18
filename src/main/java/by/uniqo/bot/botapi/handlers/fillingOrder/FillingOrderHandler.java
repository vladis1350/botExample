package by.uniqo.bot.botapi.handlers.fillingOrder;

import by.uniqo.bot.Bot;
import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.InputMessageHandler;
import by.uniqo.bot.cache.UserDataCache;
import by.uniqo.bot.service.LocaleMessageService;
import by.uniqo.bot.service.ReplyMessagesService;
import by.uniqo.bot.utils.Emojis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

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
    private Bot myBot;

    public FillingOrderHandler(UserDataCache userDataCache,
                               ReplyMessagesService messagesService, @Lazy Bot myBot) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.myBot = myBot;
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
        ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard();

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_TOTALNUMBER)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askTotalNumber");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TAPESCOLOR);
        }

        if (botState.equals(BotState.ASK_TAPESCOLOR)) {
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-catalogColor.JPG");
            profileData.setTotalNumber(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askTapesColor");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_MODELNUMBER);
        }

        if (botState.equals(BotState.ASK_MODELNUMBER)) {
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-model.JPG");
            profileData.setTapesColor(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askModelNumber");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_COLOROFMODELTEXT);

        }

        if (botState.equals(BotState.ASK_COLOROFMODELTEXT)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askColorOfModelText");
            profileData.setModelNumber(usersAnswer);
            replyToUser.setReplyMarkup(getButtonsMarkup2());

        }

        if (botState.equals(BotState.ASK_SYMBOLNUMBER)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askSymbolNumber");
            profileData.setColorOfModelText(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            replyToUser.setReplyMarkup(getAnswerForSymbolNumber());
        }

        if (botState.equals(BotState.ASK_NUMBEROFMEN)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askNumberOfMen");
            profileData.setSymbolNumber(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFWOMEN);
        }

        if (botState.equals(BotState.ASK_NUMBEROFWOMEN)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askNumberOfWomen");
            profileData.setNumberOfMen(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFTEACHERS);
        }

        if (botState.equals(BotState.ASK_NUMBEROFTEACHERS)) {
            profileData.setNumberOfWomen(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askNumberOfTeacher");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_SCHOOLNUMBER);
        }

        if (botState.equals(BotState.ASK_SCHOOLNUMBER)) {
            profileData.setNumberOfTeacher(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askSchoolNumber");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
        }

        if (botState.equals(BotState.ASK_ADDITIONALSERVICES)) {
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-additionalOrder.JPG");
            if (profileData.getSchoolNumber()==null) {
                profileData.setSchoolNumber(usersAnswer);
            }
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askAdditionalServices");
            replyToUser.setReplyMarkup(getInlineMessageButtons());
        }
        if (botState.equals(BotState.ASK_CREDENTIALS)) {
            profileData.setCredentials(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askPhoneNumber");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_URLVK);
        }

        if (botState.equals(BotState.ASK_URLVK)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askUrlVK");
            profileData.setPhoneNumber(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_DELIVERYADDRESS);
            replyToUser.setReplyMarkup(getAnswerForVK());
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
            replyToUser.setReplyMarkup(replyKeyboardMarkup);
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return replyToUser;
    }

    private ReplyKeyboard getAnswerForSymbolNumber() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row5 = new KeyboardRow();
        row5.add(new KeyboardButton("reply.ButtonSetForSymbolNumber"));
        keyboard.add(row5);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return  replyKeyboardMarkup;
    }


    private InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonStars = new InlineKeyboardButton().setText("Стразы");
        InlineKeyboardButton buttonScroll = new InlineKeyboardButton().setText("Пригласительный свиток");
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
        LocaleMessageService localeMessageService;
        InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup();
        InlineKeyboardButton goldFoil = new InlineKeyboardButton().setText(messagesService.getReplyText("foil.nameOne"));
        InlineKeyboardButton silverFoil = new InlineKeyboardButton().setText(messagesService.getReplyText("foil.nameTwo"));
        InlineKeyboardButton redFoil = new InlineKeyboardButton().setText(messagesService.getReplyText("foil.nameThree"));
        InlineKeyboardButton blueFoil = new InlineKeyboardButton().setText(messagesService.getReplyText("foil.nameFour"));
        InlineKeyboardButton blackFoil = new InlineKeyboardButton().setText(messagesService.getReplyText("foil.nameFive"));


        //Every button must have callBackData, or else not work !
        goldFoil.setCallbackData("goldFoil");
        silverFoil.setCallbackData("silverFoil");
        redFoil.setCallbackData("redFoil");
        blueFoil.setCallbackData("blueFoil");
        blackFoil.setCallbackData("blackFoil");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(goldFoil);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(silverFoil);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(redFoil);

        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
        keyboardButtonsRow4.add(blueFoil);

        List<InlineKeyboardButton> keyboardButtonsRow5 = new ArrayList<>();
        keyboardButtonsRow5.add(blackFoil);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        rowList.add(keyboardButtonsRow4);
        rowList.add(keyboardButtonsRow5);


        inlineKeyboardMarkup2.setKeyboard(rowList);

        return inlineKeyboardMarkup2;
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();
        row1.add(new KeyboardButton("Сделать заказ"));
        row2.add(new KeyboardButton("Мой заказ"));
        row3.add(new KeyboardButton("Помощь"));
        row4.add(new KeyboardButton("Менеджер"));
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }
    private ReplyKeyboardMarkup getAnswerForVK(){
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row5 = new KeyboardRow();
        row5.add(new KeyboardButton("Пропустить"));
        keyboard.add(row5);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return  replyKeyboardMarkup;
    }

}