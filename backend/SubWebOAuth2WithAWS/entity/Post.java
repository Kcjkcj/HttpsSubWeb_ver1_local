package com.kcj.SubWebOAuth2WithAWS.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    @Column(name = "title")
    private String title;

    @Column(name = "post_body") 
    private String postBody;

    @Column(name = "account_id") //누가 쓴 게시글인가
    private int account_id; //굳이 ManyToOne 설정할 필요없이 id만 가져오면 됨
    //SQL문에서 Join할때 꼬이는데?

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id",insertable = false, updatable = false,referencedColumnName = "account_id")
    private Account writeUser; //이래야 UI 상에서 메시지를 보낸 유저 이름을 쉽게 넘겨줄 수 있음
    //서버에서 보낼때는 sendUser로 이름까지 쉽게, 클라이언트에서 받을 때는 sendId로 쉽게 테이블에 저장
    //이게 유저가 탈퇴하면 account_id가 null이 되어서 데이터를 받을 때 문제가 생기네

    @Column(name = "is_notice")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) //역직렬화 무시 즉 서버에서 값을 주는 것은 가능
    //직렬화(서버에서 클라이언트로), 역직렬화(클라이언트에서 서버로)
    //클라이언트 측에서는 수정하기 어렵지만 서버 측에서는 변경 가능
    private Boolean notice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subculture_id") //어떤 장르의 게시판에 속하는가
    @JsonIgnore
    private Subculture subculture; //게시글을 대표하는 서브컬쳐
     //SQL문에서 Join할때 꼬이는데?

    @Column(name = "create_dt")
    @JsonFormat(pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private Date createDt;
    
    //파일 리스트가 필요할 것 같음 //onetomay 이 경우는 이게 필요함 file을 여러개 들고와야 하니까

    //댓글 리스트 필요 //onetomany
    @OneToMany(mappedBy = "post",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comment;
    //이걸로 그냥 해당 게시글 들어가면 댓글 리스트 나오게 하면 되리라 생각하는데
    //이렇게 하나 commentRepository 사용해서 불러오나 SQL상에 당연한거긴 한데..
}
