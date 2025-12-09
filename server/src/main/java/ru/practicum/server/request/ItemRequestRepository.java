package ru.practicum.server.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    @Query("select r from ItemRequest r where r.requester.id <> :userId order by r.created desc")
    List<ItemRequest> findAllOthers(Long userId, Pageable pageable);
}

