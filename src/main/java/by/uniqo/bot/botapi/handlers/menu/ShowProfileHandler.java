package by.uniqo.bot.botapi.handlers.menu;

import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.InputMessageHandler;
import by.uniqo.bot.botapi.handlers.fillingOrder.UserProfileData;
import by.uniqo.bot.cache.UserDataCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class ShowProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;

    public ShowProfileHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        final int userId = message.getFrom().getId();
        final UserProfileData profileData = userDataCache.getUserProfileData(userId);

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        return new SendMessage(message.getChatId(), String.format( "%sсумма лент %sцвет лент %sномер макета %sцвет текста макета" +
                        " %sномер символа %sкол-во парней %sкол-во девушек %sкол-во преподавателей %sномер школы %sцвет колокола" +
                        " %sцвет большого колокола %sцвет свитка %sцвет банта %sцвет бабочки %sФИО %sтелефонный номер %sссылка в вк" +
                        " %sадрес доставки %sкоменты %s",
                "Данные по вашему заказу", profileData.getTotalNumber(), profileData.getTapesColor(), profileData.getModelNumber(),
                profileData.getColorOfModelText(), profileData.getSymbolNumber(), profileData.getNumberOfMen(), profileData.getNumberOfWomen(),
                profileData.getNumberOfTeacher(), profileData.getSchoolNumber(), profileData.getLittleBellColor(),
                profileData.getBigBellColor(), profileData.getScrollColor(), profileData.getRibbonColor(), profileData.getBowtieColor(),
                profileData.getCredentials(), profileData.getPhoneNumber(), profileData.getUrlVK(), profileData.getDeliveryAddress(),
                profileData.getCommentsToOrder()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_ORDER;
    }
}