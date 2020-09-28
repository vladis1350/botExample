package by.uniqo.bot.botapi.handlers.fillingOrder;

import by.uniqo.bot.Bot;
import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.InputMessageHandler;
import by.uniqo.bot.botapi.handlers.buttonsHundler.ButtonsHandler;
import by.uniqo.bot.cache.UserDataCache;
import by.uniqo.bot.service.LocaleMessageService;
import by.uniqo.bot.service.ReplyMessagesService;
import by.uniqo.bot.utils.Emojis;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Формирует анкету пользователя.
 */

@Slf4j
@Component
public class FillingOrderHandler implements InputMessageHandler {
    @Autowired
    private ButtonsHandler buttonsHandler;
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
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-colorInscription.JPG");
            profileData.setModelNumber(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askColorOfModelText");
            replyToUser.setReplyMarkup(getButtonsMarkup2());

        }
        /**Выбор именных или неименных лент*/

        if (botState.equals(BotState.ASK_OPTION_SYMBOL_LAYOUT)) {
            profileData.setSymbolNumber(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_OPTION_RIBBON);
            replyToUser = buttonsHandler.getMessageAndButtonOptionRibbon(userId);
        }
        if (botState.equals(BotState.ASK_OPTION_RIBBON)) {
            System.out.println(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFMEN);
            replyToUser = buttonsHandler.getMessageAndButtonOptionRibbon(userId);
        }
        if (botState.equals(BotState.ASK_UPLOAD_FILE_LIST_MEN)) {
            System.out.println(inputMsg);
//            replyToUser = messagesService.getReplyMessage(chatId, "reply.askUploadListWomen");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_UPLOAD_FILE_LIST_WOMEN);
        }
        if (botState.equals(BotState.ASK_UPLOAD_FILE_LIST_WOMEN)) {
            System.out.println(inputMsg);

//            try {
//                uploadFile(inputMsg.getDocument().getFileName(), inputMsg.getDocument().getFileId());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askUploadListWomen");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFTEACHERS);
        }

        /**---------------------------------------------------------*/

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
//            replyToUser.setReplyMarkup(buttonsHandler.getMessageAndButtonStandardRibbon(chatId));
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
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_INDEX);
        }

        if (botState.equals(BotState.ASK_INDEX)) {
            profileData.setPhoneNumber(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askIndex");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CITY);
        }
        if (botState.equals(BotState.ASK_CITY)) {
            profileData.setIndex(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askCity");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_STREET);
        }
        if (botState.equals(BotState.ASK_STREET)) {
            profileData.setCity(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askStreet");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_HOME);
        }
        if (botState.equals(BotState.ASK_HOME)) {
            profileData.setStreet(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askHome");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_APARTMENT);
        }
        if (botState.equals(BotState.ASK_APARTMENT)) {
            profileData.setHome(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askApartment");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_COMMENTSTOORDER);
        }

        if (botState.equals(BotState.ASK_COMMENTSTOORDER)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askCommentsToOrder");
            profileData.setApartment(usersAnswer);
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

    public void uploadFile(String file_name, String file_id) throws IOException {
        URL url = new URL("https://api.telegram.org/bot"+myBot.getBotToken()+"/getFile?file_id="+file_id);
        BufferedReader in = new BufferedReader(new InputStreamReader( url.openStream()));
        String res = in.readLine();
        JSONObject jresult = new JSONObject(res);
        JSONObject path = jresult.getJSONObject("result");
        String file_path = path.getString("file_path");
        URL downoload = new URL("https://api.telegram.org/file/bot" + myBot.getBotToken() + "/" + file_path);
        FileOutputStream fos = new FileOutputStream("D:/" + file_name);
        System.out.println("Start upload");
        ReadableByteChannel rbc = Channels.newChannel(downoload.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        System.out.println("Uploaded!");
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