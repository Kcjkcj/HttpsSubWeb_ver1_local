package com.kcj.SubWebOAuth2WithAWS.repository;

import com.kcj.SubWebOAuth2WithAWS.entity.Friend_list;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FriendRepository extends CrudRepository<Friend_list,Integer> {
    List<Friend_list> findByAccountId(int id); //나의 친구 리스트 불러오기
    boolean existsByAccountId(int id);
    boolean existsByAccountIdAndFriendAccountId(int accountId, int friendAccountId);
    @Transactional
    void deleteByAccountIdAndFriendAccountId(int accountId, int friendAccountId);
}
