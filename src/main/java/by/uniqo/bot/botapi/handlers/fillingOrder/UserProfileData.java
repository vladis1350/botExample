package by.uniqo.bot.botapi.handlers.fillingOrder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Данные анкеты пользователя
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData {
    String credentials;
    String phoneNumber;
    String urlVK;
    String deliveryAddress;
    String commentsToOrder;
    String numberOfTeacher;
    String teacherCredentials;
    String schoolNumber;
    String additionalService;
    String littleBell;
    String bigBell;
    String stars;
    String scroll;
    String ribbon;
    String bowtie;
    int tapesColor;
    int totalNumber;
    int modelNumber;
    int colorOfModelText;
    int symbolNumber;
    int numberOfMen;
    int numberOfWomen;
    int littleBellColor;
    int bigBellColor;
    int scrollColor;
    int ribbonColor;
    int bowtieColor;

}
