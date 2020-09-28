package by.uniqo.bot.botapi.handlers.buttonsHundler;

import by.uniqo.bot.service.LocaleMessageService;
import by.uniqo.bot.service.ReplyMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class ButtonsHandler {
    @Autowired
    private ReplyMessagesService messagesService;

    public SendMessage getMessageAndButtonLeaveUnchanged(long chatId) {
        String message = messagesService.getReplyText("reply.askChangeModelNumber");
        return new SendMessage(chatId, message).setReplyMarkup(getButtonLeaveUnchanged());
    }

    public SendMessage getMessageAndButtonOptionRibbon(long chatId) {
        String message = messagesService.getReplyText("reply.askOptionRibbon");
        return new SendMessage(chatId, message).setReplyMarkup(getButtonsOptionRibbon());
    }

    public SendMessage getMessageAndMainMenu(long chatId) {
        String message = messagesService.getReplyText("reply.OrderFilled");
        return new SendMessage(chatId, message).setReplyMarkup(getMainMenuKeyboard());
    }

    public SendMessage getMessageAndButtonSkipNumberSchool(long chatId) {
        String message = messagesService.getReplyText("reply.askSchoolNumber");
        return new SendMessage(chatId, message).setReplyMarkup(getButtonSkipNumberSchool());
    }

    public SendMessage getMessageAndButtonsAdditionalService(long chatId) {
        String message = messagesService.getReplyText("reply.askAdditionalServices");
        return new SendMessage(chatId, message).setReplyMarkup(getButtonsAdditionalServices());
    }

    public SendMessage getMessageAndButtonsSkipComment(long chatId) {
        String message = messagesService.getReplyText("reply.askCommentsToOrder");
        return new SendMessage(chatId, message).setReplyMarkup(getButtonsSkipComment());
    }

    private InlineKeyboardMarkup getButtonsSkipComment() {
        LocaleMessageService localeMessageService;
        InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonSkipComment = new InlineKeyboardButton().setText(messagesService.getReplyText("btn.skipComment"));

        //Every button must have callBackData, or else not work !
        buttonSkipComment.setCallbackData("skipComment");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonSkipComment);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup2.setKeyboard(rowList);

        return inlineKeyboardMarkup2;
    }

    private InlineKeyboardMarkup getButtonSkipNumberSchool() {
        InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonSkipNumberSchool = new InlineKeyboardButton().setText(messagesService.getReplyText("btn.buttonSkipNumberSchool"));

        //Every button must have callBackData, or else not work !
        buttonSkipNumberSchool.setCallbackData("buttonSkipNumberSchool");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonSkipNumberSchool);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup2.setKeyboard(rowList);

        return inlineKeyboardMarkup2;
    }

    private InlineKeyboardMarkup getButtonLeaveUnchanged() {
        InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonLeaveUnchanged = new InlineKeyboardButton().setText(messagesService.getReplyText("reply.ButtonLeaveUnchanged"));

        //Every button must have callBackData, or else not work !
        buttonLeaveUnchanged.setCallbackData("buttonLeaveUnchanged");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonLeaveUnchanged);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup2.setKeyboard(rowList);

        return inlineKeyboardMarkup2;
    }

    private InlineKeyboardMarkup getButtonsOptionRibbon() {
        LocaleMessageService localeMessageService;
        InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup();
        InlineKeyboardButton nameRibbon = new InlineKeyboardButton().setText(messagesService.getReplyText("ribbon.name"));
        InlineKeyboardButton unNameRibbon = new InlineKeyboardButton().setText(messagesService.getReplyText("ribbon.unName"));

        //Every button must have callBackData, or else not work !
        nameRibbon.setCallbackData("nameRibbon");
        unNameRibbon.setCallbackData("unNameRibbon");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(nameRibbon);
        keyboardButtonsRow1.add(unNameRibbon);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

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

    private InlineKeyboardMarkup getButtonsAdditionalServices() {
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

}
