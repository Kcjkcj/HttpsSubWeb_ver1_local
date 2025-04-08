package com.kcj.SubWebOAuth2WithAWS.repository;

import com.kcj.SubWebOAuth2WithAWS.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message,Integer> {
    List<Message> findBySendId(int id);
    List<Message> findByReceiveId(int id); //받은 메시지 - 기본
    List<Message> findByReceiveIdAndNotice(int id, boolean isNotice); //공지사항
    List<Message> findByReceiveIdAndRequest(int id, boolean isRequest); //요청 메시지
    Void deleteById(int id); //나에게 온 친구 요청을 처리하고 나면 삭제
    boolean existsById(int id);
}
