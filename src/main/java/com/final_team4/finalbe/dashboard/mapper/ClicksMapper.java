package com.final_team4.finalbe.dashboard.mapper;


import com.final_team4.finalbe.dashboard.dto.DailyClicksDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ClicksMapper {

    long countClicksByProductId(@Param("productId") Long productId);

    long countAllClicks();

    List<DailyClicksDto> findDailyClicks(@Param("userId") Long userId,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);

}
