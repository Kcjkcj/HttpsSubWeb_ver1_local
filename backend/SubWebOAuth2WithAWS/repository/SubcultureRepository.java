package com.kcj.SubWebOAuth2WithAWS.repository;

import com.kcj.SubWebOAuth2WithAWS.entity.Subculture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcultureRepository extends CrudRepository<Subculture, Integer> {
    Subculture findByTitle(String title); //findBy 뒤에 속성명 DB랑 같게 //제목이 겹치는 경우는 없음
    List<Subculture> findByGenre(String genre); //장르는 겹칠 수 있으니 list로 반환
    Subculture findById(int id);
}




