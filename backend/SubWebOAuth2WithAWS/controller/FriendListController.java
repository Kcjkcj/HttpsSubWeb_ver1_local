package com.kcj.SubWebOAuth2WithAWS.controller;

import com.kcj.SubWebOAuth2WithAWS.config.CustomUserDetails;
import com.kcj.SubWebOAuth2WithAWS.config.SecurityService;
import com.kcj.SubWebOAuth2WithAWS.dto.AddFriendRequest;
import com.kcj.SubWebOAuth2WithAWS.entity.Account;
import com.kcj.SubWebOAuth2WithAWS.entity.Friend_list;
import com.kcj.SubWebOAuth2WithAWS.entity.Message;
import com.kcj.SubWebOAuth2WithAWS.repository.AccountRepository;
import com.kcj.SubWebOAuth2WithAWS.repository.FriendRepository;
import com.kcj.SubWebOAuth2WithAWS.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FriendListController {
    private final FriendRepository friendRepository;
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    private final SecurityService securityService;

    @PostMapping("/api/profile/request")
    public ResponseEntity<String> addfriend(@RequestBody AddFriendRequest addFriendRequest,
                                            @AuthenticationPrincipal UserDetails userDetails, Authentication authentication) { //HTTP에서는 Body에 하나의 JSON 객체만 수용 가능
        int id = ((CustomUserDetails) userDetails).getId();
        boolean is_approve = addFriendRequest.isApprove();
        Message message = addFriendRequest.getMessage();
        if (is_approve == true && securityService.hasAccessToResource(id, authentication)) //로그인 된 유저의 id에 대한 것만 보기 위함
        { //승인 한 경우
            try {
                int messageId = message.getMessageId();
                messageRepository.deleteById(messageId); //요청 메시지를 삭제
                boolean is_exist = messageRepository.existsById(messageId);
                if (!is_exist) { //친구 신청 메시지를 삭제하고

                    try {
                        if(friendRepository.existsByAccountIdAndFriendAccountId(id,message.getSendId()) &&
                                message.getMessageBody().contains("친구 요청")) {
                            messageRepository.deleteById(message.getMessageId()); //? 이거 왜 있지
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                                    body("이미 친구가 되어 있습니다.");
                        }

                        Friend_list friendList1 = new Friend_list(); //친구 요청을 받으면 나와 해당 유저가 서로 친구가 추가가 되어야 함
                        friendList1.setAccountId(id);
                        friendList1.setFriendAccountId(message.getSendId()); //친구 요청을 건넨 id
                        friendList1.setCreateDt(new Date(System.currentTimeMillis()));
                        Friend_list savedfriendList1 = friendRepository.save(friendList1);

                        Friend_list friendList2 = new Friend_list();
                        friendList2.setAccountId(message.getSendId());
                        friendList2.setFriendAccountId(id);
                        friendList2.setCreateDt(new Date(System.currentTimeMillis()));
                        Friend_list savedfriendList2 = friendRepository.save(friendList2);

                        if ((savedfriendList1.getFriendListId() > 0) && (savedfriendList2.getFriendListId() > 0)) {
                            return ResponseEntity.status(HttpStatus.OK).
                                    body("성공적으로 친구를 추가하였습니다.");

                        } else
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                                    body("메시지 전송에 실패하였습니다.");


                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                                body("서버에 문제가 발생하였습니다." + e.getMessage());
                    }
                }
                else
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                            body("메시지 삭제에 실패하였습니다.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                        body("서버에 문제가 발생하였습니다." + e.getMessage());
            }
        }
        else if (is_approve == false && securityService.hasAccessToResource(id, authentication)) { //거절 한 경우
            try {
                int messageId = message.getMessageId();
                messageRepository.deleteById(messageId);
                return ResponseEntity.status(HttpStatus.OK).
                        body("성공적으로 거절하였습니다.");

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                        body("서버에 문제가 발생하였습니다." + e.getMessage());
            }
        }
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    }

    //로그인 한 유저의 id accountRepository
    //id값에 대응하는 유저 리스트 fileRepository
    //유저 리스트 반환

    @PostMapping("/api/profile/myFriends")
    public ResponseEntity<String> deletefriend(@RequestBody Account account,
                                            @AuthenticationPrincipal UserDetails userDetails, Authentication authentication) { //HTTP에서는 Body에 하나의 JSON 객체만 수용 가능
        int id = ((CustomUserDetails) userDetails).getId();
        if (securityService.hasAccessToResource(id, authentication)) //로그인 된 유저의 id에 대한 것만 보기 위함
        { //친구 삭제 시에 상대방도 같이 지워지도록 구현하는지는 고민
            try {
                int FID = account.getAccountId(); //친구의 accountId를 보냄
                if(friendRepository.existsByAccountIdAndFriendAccountId(id,FID))
                    try {
                        friendRepository.deleteByAccountIdAndFriendAccountId(id,FID);
                    }catch (Exception e)
                    {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                                body("서버에 문제가 발생하였습니다." + e.getMessage());
                    }
                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).
                            body("해당하는 친구가 존재하지 않습니다.");

                boolean is_exist = friendRepository.existsByAccountIdAndFriendAccountId(id,FID);
                if (!is_exist) {
                    return ResponseEntity.status(HttpStatus.OK).
                            body("성공적으로 친구를 삭제하였습니다.");
                }
                else
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                            body("친구 삭제에 실패하였습니다.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                        body("서버에 문제가 발생하였습니다." + e.getMessage());
            }

        }
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/api/profile/myFriends")
    public List<Account> getMyFriends(@AuthenticationPrincipal UserDetails userDetails,
                                      Authentication authentication)
    {
        int id = ((CustomUserDetails)userDetails).getId();
        if(securityService.hasAccessToResource(id,authentication))
        {
            List<Account> friendList = new ArrayList<>();
            List<Friend_list> myFriends = friendRepository.findByAccountId(id);
            myFriends.forEach(myFriend ->
                    friendList.add(accountRepository.findByAccountId(myFriend.getFriendAccountId())
                            .orElse(new Account())));
            return friendList;
        }
        else
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }


}
