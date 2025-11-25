package com.final_team4.finalbe.user.mapper;

import com.final_team4.finalbe.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findById(@Param("id") Long id);

    User findByEmail(@Param("email") String email);

    User findByAvailableEmail(@Param("email") String email);

    User findAvailableById(@Param("id") Long id);
    int insert(User user);
    // insert 실행 후 user 객체를 반환하지 않고 DB를 건드린 행 개수를 반환하기 때문에 int로 함
}
