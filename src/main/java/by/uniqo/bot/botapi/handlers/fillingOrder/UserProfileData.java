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

    @Override
    public String toString() {
        return String.format("%s%nОбщая сумма лент %s%nЦвет лент %s%nНомер макета %s%nЦвет текста макета" +
                        " %s%nНомер символа на ленте %s%nКол-во парней %s%nКол-во девушек %s%nКол-во преподавателей" +
                        " %s%nНомер школы %s%nСтразы " +
                        "%s%nМаленький колокольчик  %s%nБольшой колокольчик" +
                        "  %s%nПригласительный свиток  %s%nБант  %s%nБабочка " +
                        " %s%nФИО %s%nТелефонный номер %s%nСсылка в вк" +
                        " %s%nАдрес доставки %s%nКомментарии к заказу %s",
                "Данные по вашему заказу", getTotalNumber(), getTapesColor(), getModelNumber(),
                getColorOfModelText(), getSymbolNumber(), getNumberOfMen(), getNumberOfWomen(),
                getNumberOfTeacher(), getSchoolNumber(),getStars(),getLittleBell(),
                getBigBell(), getScroll(), getRibbon(), getBowtie(), getCredentials(), getPhoneNumber(),
                getUrlVK(), getDeliveryAddress(), getCommentsToOrder());
    }
}
