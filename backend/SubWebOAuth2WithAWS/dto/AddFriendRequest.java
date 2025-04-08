package com.kcj.SubWebOAuth2WithAWS.dto;

import com.kcj.SubWebOAuth2WithAWS.entity.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AddFriendRequest {
    private boolean approve;
    private Message message;
}
