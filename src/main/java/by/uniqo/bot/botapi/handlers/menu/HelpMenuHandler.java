package by.uniqo.bot.botapi.handlers.menu;

import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.InputMessageHandler;
import by.uniqo.bot.service.MainMenuService;
import by.uniqo.bot.service.ReplyMessagesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class HelpMenuHandler implements InputMessageHandler {
    private MainMenuService mainMenuService;
    private ReplyMessagesService messagesService;

    public HelpMenuHandler(MainMenuService mainMenuService, ReplyMessagesService messagesService) {
        this.mainMenuService = mainMenuService;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(),
                messagesService.getReplyText("reply.ShowHelpMenu"));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_HELP_MENU;
    }
}
