package com.kcj.SubWebOAuth2WithAWS.controller;

import com.kcj.SubWebOAuth2WithAWS.entity.Subculture;
import com.kcj.SubWebOAuth2WithAWS.repository.SubcultureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SubcultureController {
    //DB에 등록된 장르들을 반환

    private final SubcultureRepository subcultureRepository; //final로 지정하지 않으면 안됨
    @PostMapping("/api/subculture")
    public ResponseEntity<String> createSubculture (
            @RequestParam("title")String title,
            @RequestParam("genre")String genre,
            @RequestParam("image")MultipartFile image) //@RequestBody로 받을 때 XML이나 JSON형태를 기대함
    { //서브컬쳐 작품 등록
        try{
            String uploadDir = "/app/genre/"+title; //절대 경로로 지정하는것이 더 안정적임
            File dir = new File(uploadDir);
            if(!dir.exists()){
                dir.mkdirs();
            }

            String originalFilename = image.getOriginalFilename();
            String extention = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString()+extention; //파일명 겹치는것 방지하면서 확장자만 가져옴

            String imagePath = uploadDir + "/" + newFilename;
            File dest = new File(imagePath); //해당 경로에 대응하는 File 객체
            try {
                image.transferTo(dest); //실제 파일 데이터를 지정한 위치에 기록
            }catch (IOException e)
            {
                System.err.println(("파일 저장 중 오류:"+e.getMessage()));
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 이미지가 저장되지 않았습니다.");
            }
            String encodedPath = "/genre/" +
                    URLEncoder.encode(title, StandardCharsets.UTF_8.toString()).replace("+", "%20") +
                    "/" + newFilename; //URL에 한국어가 있는 경우 percent Encoding를 사용해서 문제가 없도록 함
            Subculture subculture = new Subculture();
            subculture.setTitle(title);
            subculture.setGenre(genre);
            subculture.setImagePath(encodedPath); //HTTP URL에 맞는 상대 경로 설정
            subculture.setCreateDt(new Date(System.currentTimeMillis()));

            Subculture savedSubculture = subcultureRepository.save(subculture);
            if(savedSubculture.getSubcultureId()>0)
            {
                return ResponseEntity.status(HttpStatus.CREATED).
                        body("성공적으로 등록되었습니다.");
            }
            else
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                        body("등록에 실패하였습니다.");
            }
        }
        catch (Exception ex)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("예외가 발생하였습니다 :"+ex.getMessage());

        }
    }

    @GetMapping("/api/main")
    public List<Subculture> getSubcultures() //메인페이지에 보이는 서브컬쳐 게시판
    {
       List<Subculture> result = new ArrayList<>();
       subcultureRepository.findAll().forEach(result::add);
       return result;
        //return (List<Subculture>) subcultureRepository.findAll(); 강제 캐스팅을 하면 자료형이 안맞는 오류가 생길 수도 있음
    }
    
    
}
