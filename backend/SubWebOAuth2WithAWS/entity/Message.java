package com.kcj.SubWebOAuth2WithAWS.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int messageId;

    @Column(name = "send_id")
    private int sendId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "send_id",insertable = false, updatable = false,referencedColumnName = "account_id")
    private Account sendUser; //이래야 UI 상에서 메시지를 보낸 유저 이름을 쉽게 넘겨줄 수 있음
    //서버에서 보낼때는 sendUser로 이름까지 쉽게, 클라이언트에서 받을 때는 sendId로 쉽게 테이블에 저장
    
    @Column(name = "receive_id")
    private int receiveId;

    @Column(name = "is_notice")
    //JsonProperty.Access.READ_ONLY하면 프론트에서의 요청이 무시됨.. 역직렬화(클라이언트 -> 서버) 무시
    private boolean notice;

    @Column(name = "is_request")
    private boolean request; //보내진 메시지 중 요청관련 메시지(친구요청,

    @Column(name = "message_body")
    private String messageBody;

    @Column(name = "create_dt")
    @JsonFormat(pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private Date createDt;


}
