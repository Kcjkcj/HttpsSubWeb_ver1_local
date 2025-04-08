package com.kcj.SubWebOAuth2WithAWS.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name="file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int fileId;

    @Column(name = "file_path") //파일 경로
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private int fileSize;

    @Column(name = "file_type") //파일 종류, only write?
    private String fileType;

    @Column(name = "post_id") //어떤 게시글에 포함되는가 //manytoone
    private int postId;

    @Column(name = "create_dt")
    @JsonFormat(pattern = "yyyy년 MM월 dd일", timezone = "Asia/Seoul")
    private Date createDt;
}
