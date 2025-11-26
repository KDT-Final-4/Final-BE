package com.final_team4.finalbe.content.mapper;

import com.final_team4.finalbe.content.domain.Content;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContentMapper {
        void insert(Content content);
}
