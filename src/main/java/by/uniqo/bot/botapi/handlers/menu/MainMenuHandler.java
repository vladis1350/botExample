package by.uniqo.bot.botapi.handlers.menu;

import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.InputMessageHandler;
import by.uniqo.bot.service.MainMenuService;
import by.uniqo.bot.service.ReplyMessagesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MainMenuHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private MainMenuService mainMenuService;

    public MainMenuHandler(ReplyMessagesService messagesService, MainMenuService mainMenuService) {
        this.messagesService = messagesService;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(), messagesService.getReplyText("reply.ShowMainMenu"));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }


}
