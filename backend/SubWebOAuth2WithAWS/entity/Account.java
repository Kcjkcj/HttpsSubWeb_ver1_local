package com.kcj.SubWebOAuth2WithAWS.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity //자바 클래스를 JPA 엔터티로 지정 DB 테이블과 매핑
@Getter @Setter
@Table(name="account") //클러스터, 보조 인덱스 설정 가능
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private int accountId;

    @Column(name = "name")
    private String accountName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //직렬화 무시
    //직렬화(서버에서 클라이언트로), 역직렬화(클라이언트에서 서버로)
    //UI 어플리케이션은 백엔드 어플리케이션에서 password를 필요로하지 않음
    //DB에서 온 데이터의 값이 null로 대체 되도록
    @Column(name = "pwd")
    private String accountPwd;

    @Column(name = "email")
    private String email;

    @JsonFormat(pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    @Column(name = "create_dt")
    private Date createDt;

    @OneToMany(mappedBy = "account",fetch = FetchType.EAGER) //Lazy를 쓸지는 고민
    //하나의 account_id에 여러개의 role
    @JsonIgnore //클라이언트 측에서는 이 정보를 받지 않음 //그런데 어차피 role 정보는 JWT에 있기 때문에 받을 필요도 없음
    private Set<Role> roles = new HashSet<>();

}
