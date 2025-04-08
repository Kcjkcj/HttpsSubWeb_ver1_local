package com.kcj.SubWebOAuth2WithAWS.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="friend_list")
public class Friend_list {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_list_id")
    private int friendListId;

    @Column(name = "my_account_id")
    private int accountId; //나의 계정 번호

    @Column(name = "friend_account_id")
    private int friendAccountId; //친구의 계정 번호

    @Column(name = "create_dt")
    @JsonFormat(pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private Date createDt;

}
