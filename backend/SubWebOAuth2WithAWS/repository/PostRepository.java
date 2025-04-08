package com.kcj.SubWebOAuth2WithAWS.repository;

import com.kcj.SubWebOAuth2WithAWS.entity.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post,Integer> {
    List<Post> findByTitle(String title); //제목은 중첩 될 수도 있으니 list로 반환 (다른 게시판의 글이 불려와서는 안됨)
    Post findById(int id); //id는 고유한 값이니 상관없음
    List<Post> findBySubculture_SubcultureId(int id); //게시판에 속하는 글을 불러올 때
}
