package by.uniqo.bot.botapi.handlers.buttonsHundler;

import by.uniqo.bot.service.LocaleMessageService;
import by.uniqo.bot.service.ReplyMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class ButtonsHandler {
    @Autowired
    private ReplyMessagesService messagesService;

    public SendMessage getMessageAndButtonLeaveUnchanged(long chatId) {
        String message = messagesService.getReplyText("reply.askModelNumber");
        return new SendMessage(chatId, message).setReplyMarkup(getButtonLeaveUnchanged());
    }

    public SendMessage getMessageAndButtonOptionRibbon(long chatId) {
        String message = messagesService.getReplyText("reply.askOptionRibbon");
        return new SendMessage(chatId, message).setReplyMarkup(getButtonsOptionRibbon());
    }

    public SendMessage getMessageAndButtonStandardRibbon(long chatId) {
        String message = messagesService.getReplyText("reply.askStandardRibbon");
        return new SendMessage(chatId, message).setReplyMarkup(getButtonsStandardRibbon());
    }

    private InlineKeyboardMarkup getButtonLeaveUnchanged() {
        InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup();
        InlineKeyboardButton goldFoil = new InlineKeyboardButton().setText(messagesService.getReplyText("reply.ButtonLeaveUnchanged"));

        //Every button must have callBackData, or else not work !
        goldFoil.setCallbackData("buttonLeaveUnchanged");


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(goldFoil);


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


    private InlineKeyboardMarkup getButtonsStandardRibbon() {
        LocaleMessageService localeMessageService;
        InlineKeyboardMarkup inlineKeyboardMarkup2 = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonStandard = new InlineKeyboardButton().setText(messagesService.getReplyText("ribbon.standard"));

        //Every button must have callBackData, or else not work !
        buttonStandard.setCallbackData("standard");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonStandard);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup2.setKeyboard(rowList);

        return inlineKeyboardMarkup2;
    }
}
