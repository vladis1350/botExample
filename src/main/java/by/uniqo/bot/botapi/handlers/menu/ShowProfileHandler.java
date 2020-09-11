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
        return new SendMessage(message.getChatId(), String.format( "%s%nОбщая сумма лент %s%nЦвет лент %s%nНомер макета %s%nЦвет текста макета" +
                        " %s%nНомер символа на ленте %s%nКол-во парней %s%nКол-во девушек %s%nКол-во преподавателей %s%nНомер школы %s%nСтразы " +
                        "%s%nМаленький колокольчик %s%nЦвет маленького колокольчика %s%nБольшой колокольчик" +
                        " %s%nЦвет большого колокола %s%nПригласительный свиток %s%nЦвет свитка %s%nБант %s%nЦвет банта %s%nБабочка " +
                        "%s%nЦвет бабочки %s%nФИО %s%nТелефонный номер %s%nСсылка в вк" +
                        " %s%nАдрес доставки %s%nКомментарии к заказу %s",
                "Данные по вашему заказу", profileData.getTotalNumber(), profileData.getTapesColor(), profileData.getModelNumber(),
                profileData.getColorOfModelText(), profileData.getSymbolNumber(), profileData.getNumberOfMen(), profileData.getNumberOfWomen(),
                profileData.getNumberOfTeacher(), profileData.getSchoolNumber(),profileData.getStars(),profileData.getLittleBell(),
                profileData.getLittleBellColor(), profileData.getBigBell(), profileData.getBigBellColor(), profileData.getScroll(),
                profileData.getScrollColor(), profileData.getRibbon(), profileData.getRibbonColor(), profileData.getBowtie(),
                profileData.getBowtieColor(), profileData.getCredentials(), profileData.getPhoneNumber(), profileData.getUrlVK(),
                profileData.getDeliveryAddress(), profileData.getCommentsToOrder()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_ORDER;
    }
}