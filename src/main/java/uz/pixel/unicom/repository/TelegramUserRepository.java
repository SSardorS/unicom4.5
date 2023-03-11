package uz.pixel.unicom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pixel.unicom.entity.TelegramUser;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, UUID> {

    Optional<TelegramUser> findByUserName(String userName);



    Optional<TelegramUser> findByUserNameOrChatId(String username, Long chatId);

    Optional<TelegramUser> findByChatId(Long chatId);

    Optional<TelegramUser> findByUserId(UUID id);



}
