package com.kcj.SubWebOAuth2WithAWS.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;

    @Column(name = "comment_body") //댓글 내용
    private String commentBody;

    @Column(name = "account_id") //누가 이 댓글을 썼는가
    private int accountId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id",insertable = false, updatable = false,referencedColumnName = "account_id")
    private Account writeUser; //이래야 UI 상에서 메시지를 보낸 유저 이름을 쉽게 넘겨줄 수 있음
    //서버에서 보낼때는 sendUser로 이름까지 쉽게, 클라이언트에서 받을 때는 sendId로 쉽게 테이블에 저장


    @Column(name = "post_id") //누가 이 댓글을 썼는가
    private int postId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id",insertable = false, updatable = false,referencedColumnName = "post_id") //어떤 게시글의 댓글인가 d//manytoone
    private Post post;

    @Column(name = "create_dt")
    @JsonFormat(pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private Date createDt;
}
