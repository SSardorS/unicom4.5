package uz.pixel.unicom.entity;


import lombok.*;
import uz.pixel.unicom.entity.template.AbsUUIDEntity;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "unicom_telegram_user")
//@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueUserNameAndTelegramId", columnNames = { "userName", "chatId" })})
public class TelegramUser extends AbsUUIDEntity {

    @Column(unique = true)
    private String userName;
    private String fullName;

    @Column(unique = true, nullable = false)
    private Long chatId;

    private boolean active;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    public TelegramUser(String userName, String fullName, Long chatId, boolean active) {
        this.userName = userName;
        this.fullName = fullName;
        this.chatId = chatId;
        this.active = active;
    }
}
