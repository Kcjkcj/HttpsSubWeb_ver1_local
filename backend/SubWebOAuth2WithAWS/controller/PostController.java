package com.kcj.SubWebOAuth2WithAWS.controller;

import com.kcj.SubWebOAuth2WithAWS.config.CustomUserDetails;
import com.kcj.SubWebOAuth2WithAWS.entity.Post;
import com.kcj.SubWebOAuth2WithAWS.entity.Subculture;
import com.kcj.SubWebOAuth2WithAWS.repository.PostRepository;
import com.kcj.SubWebOAuth2WithAWS.repository.SubcultureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PostController {
    //게시글 리스트 반환
    private final SubcultureRepository subcultureRepository;
    private final PostRepository postRepository;


    //제목, 글 내용, 이미지 내용 -> @RequestParam(""), 공지여부 - 어차피 일반 유저에게는 드러나지 않음
    /*
    @PostMapping("/postImage")
    public ResponseEntity<?> ChangeImagePath( //postBody에 이미지 경로를 넣기 위한 작업
            @RequestParam("subcultureId")int id,
            @RequestParam("title")String title,
            @RequestParam("image")MultipartFile image)//이미지 리스트가 온다면 Array로 받는게 일반적
    {
        if(image.isEmpty())
        {
            return null;
        }
        try {
            Subculture whichSubculture = subcultureRepository.findById(id);
            String SubcultureTitle = whichSubculture.getTitle();

            String randomValue = UUID.randomUUID().toString();

            String uploadDir = "C:/SubWeb_ver1/SubWeb/genre/"+SubcultureTitle+"/post/"+title+randomValue; //절대 경로로 지정하는것이 더 안정적임
            //기존 서브컬쳐 게시판의 폴더에 생성되며, 해당 게시글의 폴더 + 제목 + 시간에는 게시글에 들어간 이미지가 저장됨
            //시간까지 함께 저장하려면 다른 방법이 필요..
            java.io.File dir = new File(uploadDir);
            if(!dir.exists()){
                dir.mkdirs();
            }

            String originalFilename = image.getOriginalFilename();
            String extention = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + extention; //파일명 겹치는것 방지하면서 확장자만 가져옴

            String imagePath = uploadDir + "/" + newFilename;
            File dest = new File(imagePath); //해당 경로에 대응하는 File 객체
            try {
                image.transferTo(dest); //실제 파일 데이터를 지정한 위치에 기록
            } catch (IOException e) {
                System.err.println(("파일 저장 중 오류:" + e.getMessage()));
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 이미지가 저장되지 않았습니다.");
            }
            String encodedPath = "/genre/" + URLEncoder.encode(title+randomValue, StandardCharsets.UTF_8.toString()) +
                    "/" + newFilename; //URL에 한국어가 있는 경우 percent Encoding를 사용해서 문제가 없도록 함
            Map<String,String> response = new HashMap<>();
            response.put("imageUrl",encodedPath);
            return ResponseEntity.ok(response); //이렇게 변환된 이미지의 url을 다시 postbody에 넣어줌
            //
        }catch (UnsupportedEncodingException ex)
        {
            System.err.println("게시글 저장 중 문제가 발생하였습니다."+ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("예외가 발생하였습니다 :"+ex.getMessage());
        }

    }
   */
    //제목, 글 내용, 이미지 내용 -> @RequestParam(""), 공지여부 - 어차피 일반 유저에게는 드러나지 않음
    @PostMapping("/api/post")
    public ResponseEntity<?> createPost( //게시글 저장 메소드
            @RequestParam("subcultureId")int id,
            @RequestParam("title")String title,
            @RequestParam("postBody")String postBody, //Velog 게시글 모델을 참조하여 글을 작성할 때 이미지와 함께 저장되는 것을 모방
            Authentication authentication) //Form Data로 전달 - postman에서도
    {
       try{
           Subculture subculture = new Subculture();
           subculture = subcultureRepository.findById(id);
           Date currentTime = new Date(System.currentTimeMillis());
           Post Newpost = new Post();
           Newpost.setTitle(title);
           Newpost.setPostBody(postBody);
           Newpost.setSubculture(subculture);
           Newpost.setCreateDt(currentTime);
           if(authentication.isAuthenticated())
           {
               String roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).
                       collect(Collectors.joining(","));
               if(roles.contains("ADMIN"))
                   Newpost.setNotice(true);
               else
                   Newpost.setNotice(false);
           }
           CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
           Newpost.setAccount_id(userDetails.getId());
           Post savedPost = postRepository.save(Newpost);
           if(savedPost.getPostId() >0)
           {
               return ResponseEntity.status(HttpStatus.CREATED).
                       body("성공적으로 생성되었습니다.");
           }
           else
           {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                       body("게시글 생성에 실패하였습니다.");
           }

       }catch (Exception ex)
       {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                   body("예외가 발생하였습니다 :"+ex.getMessage());
       }

    }

}
