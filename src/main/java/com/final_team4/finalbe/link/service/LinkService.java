package com.final_team4.finalbe.link.service;

import com.final_team4.finalbe.content.mapper.ContentMapper;
import com.final_team4.finalbe.link.domain.LinkTarget;
import com.final_team4.finalbe.link.dto.LinkResponseDto;
import com.final_team4.finalbe.link.mapper.LinkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LinkService {

    LinkMapper linkMapper;

    @Transactional
    public LinkResponseDto resolveLink(String jobId, String ip){
        LinkTarget target = linkMapper.findByJobId(jobId);
        if(target == null){
            throw new IllegalArgumentException("존재하지 않는 jobId입니다 : " + jobId);
        }

        if(target.getProductId() !=null) {
            linkMapper.insertClick(target.getProductId(), ip);
        }
        return LinkResponseDto.of(target.getLink()) ;
    }



}
