package com.kcj.SubWebOAuth2WithAWS.repository;

import com.kcj.SubWebOAuth2WithAWS.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository <Role,Integer> {
    //Set<Role> findByAccount_AccountId(int id); //외래키를 사용하는 경우 참조하는 것의 엔티티와 속성을 적어주면 됨
}
