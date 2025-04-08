package com.kcj.SubWebOAuth2WithAWS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name="role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int role_id;

    @Column(name = "role_name")
    private String role_name;

    @ManyToOne(fetch = FetchType.LAZY) //여러개의 역할이 하나의 account_id에 속함
    @JsonIgnore
    //반환할 때 account안에 Set(roles)안에 다시 account정보가 와서 무한 반복이 됨/ 그래서 클라이언트가 이 정보를 받지 않도록 함
    @JoinColumn(name = "account_id") //외래키를 나타내는 JoinColumn
    private Account account; //외래키는 해당 클래스로 선언해줘야 함..
}
