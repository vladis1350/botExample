package by.uniqo.bot.botapi.handlers;

import by.uniqo.bot.Bot;
import by.uniqo.bot.botapi.handlers.buttonsHundler.ButtonsHandler;
import by.uniqo.bot.botapi.handlers.fillingOrder.UserProfileData;
import by.uniqo.bot.cache.UserDataCache;
import by.uniqo.bot.service.LocaleMessageService;
import by.uniqo.bot.service.MainMenuService;
import by.uniqo.bot.service.ReplyMessagesService;
import by.uniqo.bot.utils.Emojis;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.*;
import java.net.URL;

/**
 * @author get inspired by Sergei Viacheslaev's video
 */
@Component
@Slf4j
public class TelegramFacade {
    @Autowired
    private ButtonsHandler buttonsHandler;
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;
    private Bot myBot;
    private ReplyMessagesService messagesService;


    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService,
                          @Lazy Bot myBot, ReplyMessagesService messagesService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
        this.myBot = myBot;
        this.messagesService = messagesService;
    }

    @SneakyThrows
    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;
        SendDocument replyDocument = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }

        Message message = update.getMessage();

        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        } else if (message != null && message.hasDocument()) {
            log.info("New document from User:{}, userId: {}, chatId: {},  with text: {}, file_id: {}, file_name: {}, mite_type: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText(), message.getDocument().getFileId(),
                    message.getDocument().getFileName(), message.getDocument().getMimeType());

            replyMessage = inputListMenAndWomen(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();

        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.ASK_START;
                break;
            case "Сделать заказ":
                botState = BotState.FILLING_ORDER;
                break;
            case "Мой заказ":
                myBot.sendDocument(chatId, "Ваш заказ", getUsersProfile(userId));
                botState = BotState.SHOW_USER_ORDER;
                break;
            case "Помощь":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }

    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        LocaleMessageService localeMessageService;

        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");


        //From Destiny choose buttons
        if (buttonQuery.getData().equals("buttonStartOrder")) {
            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askTotalNumber"));

            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TAPESCOLOR);
        } else if (buttonQuery.getData().equals("buttonPromotionsAndDiscounts")) {
            callBackAnswer = sendAnswerCallbackQuery("У нас для вас скидки", false, buttonQuery);
        } else if (buttonQuery.getData().equals("buttonPaymentAndDelivery")) {
            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.PaymentAndDelivery"));

        } else if (buttonQuery.getData().equals("buttonCallForManager")) {
            callBackAnswer = sendAnswerCallbackQuery("+375xx xxx xx xx", true, buttonQuery);
            //From menus in additional services
        } else if (buttonQuery.getData().equals("buttonStars")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setStars("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonScroll")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setScroll("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonBigBell")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setBigBell("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonLittleBell")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setLittleBell("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonRibbon")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setRibbon("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);

        } else if (buttonQuery.getData().equals("buttonBowtie")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setBowtie("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            callBackAnswer = sendAnswerCallbackQuery("Что-нибудь еще?", true, buttonQuery);
            ;

        } else if (buttonQuery.getData().equals("buttonNext")) {
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_CREDENTIALS);
            callBackAnswer = new SendMessage(chatId, "Теперь заполним инфо по доставке\n" +
                    "Укажите полное ФИО");
        }


        //From ModelColorText choose buttons
        else if (buttonQuery.getData().equals("goldFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameOne"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_OPTION_SYMBOL_LAYOUT);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");
            callBackAnswer = buttonsHandler.getMessageAndButtonLeaveUnchanged(chatId);

        } else if (buttonQuery.getData().equals("silverFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameTwo"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_OPTION_SYMBOL_LAYOUT);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");
            callBackAnswer = buttonsHandler.getMessageAndButtonLeaveUnchanged(chatId);

        } else if (buttonQuery.getData().equals("redFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameThree"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_OPTION_SYMBOL_LAYOUT);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");
            callBackAnswer = buttonsHandler.getMessageAndButtonLeaveUnchanged(chatId);

        } else if (buttonQuery.getData().equals("blueFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameFour"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_OPTION_SYMBOL_LAYOUT);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");
            callBackAnswer = buttonsHandler.getMessageAndButtonLeaveUnchanged(chatId);

        } else if (buttonQuery.getData().equals("blackFoil")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setColorOfModelText(messagesService.getReplyText("foil.nameFive"));
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_OPTION_SYMBOL_LAYOUT);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-symbol.JPG");
            callBackAnswer = buttonsHandler.getMessageAndButtonLeaveUnchanged(chatId);

        } else if (buttonQuery.getData().equals("buttonLeaveUnchanged")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_OPTION_RIBBON);
            callBackAnswer = buttonsHandler.getMessageAndButtonOptionRibbon(chatId);

        } else if (buttonQuery.getData().equals("nameRibbon")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
//            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_UPLOAD_FILE_LIST_WOMEN);
            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askUploadListMen"));

        } else if (buttonQuery.getData().equals("unNameRibbon")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFWOMEN);
            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askNumberOfMen"));

        } else if (buttonQuery.getData().equals("standard")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setNumberOfTeacher(userProfileData.getNumberOfTeacher() + " - стандарт");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
//            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askSchoolNumber"));
            callBackAnswer = buttonsHandler.getMessageAndButtonSkipNumberSchool(userId);

        } else if (buttonQuery.getData().equals("continueEntryData")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
//            callBackAnswer = new SendMessage(chatId, messagesService.getReplyText("reply.askSchoolNumber"));
            callBackAnswer = buttonsHandler.getMessageAndButtonSkipNumberSchool(userId);

        } else if (buttonQuery.getData().equals("skipApartment")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setApartment("нет");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ORDER_FILLED);
            callBackAnswer = buttonsHandler.getMessageAndButtonsSkipComment(userId);

        } else if (buttonQuery.getData().equals("skipComment")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setCommentsToOrder("нет");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_SEND_ORDER_MANAGER);
            callBackAnswer = buttonsHandler.getMessageAndButtonSendOrderManager(chatId);

        } else if (buttonQuery.getData().equals("buttonSkipNumberSchool")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setSchoolNumber("нет");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_ADDITIONALSERVICES);
            myBot.sendPhoto(chatId, messagesService.getReplyMessage("reply.askStart2", Emojis.ARROWDOWN), "static/images/Web-additionalOrder.JPG");
            callBackAnswer = buttonsHandler.getMessageAndButtonsAdditionalService(chatId);

        } else if (buttonQuery.getData().equals("sendOrderManager")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            if (userProfileData.getListMenId() != null && userProfileData.getListWomenId() != null)
            myBot.sendDocumentToManager(367568142, buttonQuery.getFrom().getFirstName() +
                    buttonQuery.getFrom().getLastName(), userProfileData.getListMenId());
            myBot.sendDocumentToManager(367568142, buttonQuery.getFrom().getFirstName() +
                    buttonQuery.getFrom().getLastName(), userProfileData.getListWomenId());
            myBot.sendDocument(367568142, buttonQuery.getFrom().getFirstName() +
                    buttonQuery.getFrom().getLastName(), getUsersProfile(userId));
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            callBackAnswer = buttonsHandler.getMessageAndMainMenu(chatId);

        } else {
                userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            }


            return callBackAnswer;
        }

        private SendMessage inputListMenAndWomen (Message message){
            int userId = message.getFrom().getId();
            long chatId = message.getChatId();

            BotState botState = userDataCache.getUsersCurrentBotState(userId);
            SendMessage replyToUser = null;

            if (botState.equals(BotState.ASK_UPLOAD_FILE_LIST_WOMEN)) {
                UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
                userProfileData.setListMenId(message.getDocument().getFileId());
                userDataCache.saveUserProfileData(message.getFrom().getId(), userProfileData);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.askUploadListWomen");
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_NUMBEROFTEACHERS);
            }
            if (botState.equals(BotState.ASK_NUMBEROFTEACHERS)) {
                UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
                userProfileData.setListWomenId(message.getDocument().getFileId());
                userDataCache.saveUserProfileData(message.getFrom().getId(), userProfileData);
//            uploadFile(message.getDocument().getFileName(), message.getDocument().getFileId());
                replyToUser = messagesService.getReplyMessage(chatId, "reply.askNumberOfTeacher");
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TEACHER_STANDARD_RIBBON);
            }

            return replyToUser;
        }

        private AnswerCallbackQuery sendAnswerCallbackQuery (String text,boolean alert, CallbackQuery callbackquery){
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
            answerCallbackQuery.setShowAlert(alert);
            answerCallbackQuery.setText(text);
            return answerCallbackQuery;
        }

        @SneakyThrows
        public File getUsersProfile ( int userId){
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            File profileFile = ResourceUtils.getFile("classpath:static/docs/Your_order.TXT");

            try (FileWriter fw = new FileWriter(profileFile.getAbsoluteFile());
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(userProfileData.toString());
            }


            return profileFile;
        }


        @SneakyThrows
        private void uploadFile (String file_name, String file_id) {
            URL url = new URL("https://api.telegram.org/bot" + myBot.getBotToken() + "/getFile?file_id=" + file_id);


            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String res = in.readLine();
            JSONObject jresult = new JSONObject(res);
            JSONObject path = jresult.getJSONObject("result");
            String file_path = path.getString("file_path");
            URL downoload = new URL("https://api.telegram.org/file/bot" + myBot.getBotToken() + "/" + file_path);

            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(1331718111L);
            sendDocument.setDocument(file_id);
            sendDocument.setCaption(file_name);

            myBot.execute(sendDocument);
        }


    }