package com.kcj.SubWebOAuth2WithAWS.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="subculture")
public class Subculture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subculture_id")
    private int subcultureId;

    @Column(name = "title")
    private String title;

    @Column(name = "genre")
    private String genre;

    @Column(name = "image_path") //메인 화면에서 해당 작품의 이미지를 볼때.. read only??
    private String imagePath;
    //타입을 MultipartFile로 저장하는 것은 옳지 않다 HTTP 요청의 본문에서 직접 파일을 읽어오는데 사용하기 때문에

    @Column(name = "create_dt")
    @JsonFormat(pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private Date createDt;

    /*
    @OneToMany(mappedBy = "subculture",fetch = FetchType.EAGER) //mappedBy 하기위해서 post entity에 subculture이 있어야 함
    private List<Post> posts; //서브 컬쳐 게시판에 속한 게시글들
    */ //게시판 자체를 메인에 띄울때 이 정보가 필요가 없음
     
}
