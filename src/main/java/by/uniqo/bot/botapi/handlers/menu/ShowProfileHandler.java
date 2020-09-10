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
        return new SendMessage(message.getChatId(), String.format("%s%n -------------------%nОбщее количество лент" +
                        " %s%nЦвет ленты: %d%nНомер макета: %s%nЦвет надписи: %d%n" + "%s%nНомер символа: %s%nКоличество парней: %s%n" +
                        "%s%nКоличество девушек: %s%nКлассные руководители(ль)%s%nНазвание учебного заведения: "  +
                        "%s%nЦвет маленького колокольчика %s%nЦвет большого колокольчика%s%n" +
                        "Пригласительный свиток: %s%nЦвет пригласительного свитка %s%nЦвет банта:" +
                        " %s%nЦвет бабочки: %s%nФИО заказчика: %s%nНомер телефона: %s%nСсылка на профиль в ВК %s%n" +
                        "Адрес доставки: %s%nКомментарии к заказу: %s%n",
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