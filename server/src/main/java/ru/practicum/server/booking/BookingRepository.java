package ru.practicum.server.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.common.dto.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Все бронирования пользователя
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    // Все бронирования вещей владельца
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    // Бронирования вещи по статусу
    List<Booking> findAllByItemIdAndStatusOrderByStartDesc(Long itemId, BookingStatus status);

    // Проверка перекрывающихся Approved-бронирований
    List<Booking> findAllByItemIdAndStatusAndStartBeforeAndEndAfter(
            Long itemId, BookingStatus status, LocalDateTime end, LocalDateTime start);

    // Последняя завершённая бронь
    Booking findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(
            Long itemId, BookingStatus status, LocalDateTime time);

    // Следующая будущая бронь
    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, BookingStatus status, LocalDateTime time);

    // Аналогичный метод со статусом (может использоваться сервисом)
    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long userId, Long itemId, BookingStatus status, LocalDateTime time
    );
}
