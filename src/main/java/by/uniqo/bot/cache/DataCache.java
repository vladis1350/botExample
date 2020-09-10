package by.uniqo.bot.cache;

import by.uniqo.bot.botapi.handlers.BotState;
import by.uniqo.bot.botapi.handlers.fillingOrder.UserProfileData;

public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    UserProfileData getUserProfileData(int userId);

    void saveUserProfileData(int userId, UserProfileData userProfileData);
}
