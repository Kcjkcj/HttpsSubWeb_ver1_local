package com.kcj.SubWebOAuth2WithAWS.controller;

import com.kcj.SubWebOAuth2WithAWS.config.CustomUserDetails;
import com.kcj.SubWebOAuth2WithAWS.entity.Comment;
import com.kcj.SubWebOAuth2WithAWS.entity.Post;
import com.kcj.SubWebOAuth2WithAWS.repository.CommentRepository;
import com.kcj.SubWebOAuth2WithAWS.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @GetMapping(value = "/api/board",params = "id") //이런식으로 같은 API에서 다른 Get 요청이 가능
    public List<Post> getPosts(@RequestParam int id)
    {
        return postRepository.findBySubculture_SubcultureId(id);
    }

    @GetMapping(value = "/api/board", params = "title") //검색 쪽은 다른 방법으로 해야함
    public List<Post> getPostsBytitle(@RequestParam String title)
    {
        return postRepository.findByTitle(title);
    } //클러스터형 인덱스와 보조 인덱스를 어떻게 사용할 수 있을지 고민

    @GetMapping(value = "/api/board/content")
    public List<Comment> getCommentsByPostId(@RequestParam int post_id) //URL의 변수명과 맞춰줘야 함
    {
        /*
        Post content = postRepository.findById(post_id);
        return content.getComment();
         */

        return commentRepository.findByPost_PostId(post_id);
    }

    @PostMapping(value = "/api/board/content") //HttpMethod.** 으로 RequestMatcher에서 설정가능
    public ResponseEntity<String> writeComment(@RequestBody Comment comment, Authentication authentication)
    {
        try {
            //Authentication 객체를 쓰는 시점에서 isAuthenticated을 여기서 검사할 필요가 없음
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            comment.setAccountId(userDetails.getId());
            comment.setCreateDt(new Date(System.currentTimeMillis()));
            Comment savedComment = commentRepository.save(comment);

            if (savedComment.getCommentId() > 0)
                return ResponseEntity.status(HttpStatus.CREATED).
                        body("댓글 작성에 성공하였습니다.");
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                        body("댓글 작성에 실패하였습니다.");
            
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("서버에 문제가 발생하였습니다." +e.getMessage());
        }

    }

}
