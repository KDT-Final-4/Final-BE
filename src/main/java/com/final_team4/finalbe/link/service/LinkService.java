package com.final_team4.finalbe.link.service;

import com.final_team4.finalbe._core.exception.InvalidJobIdException;
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

    private final LinkMapper linkMapper;

    @Transactional
    public LinkResponseDto resolveLink(String jobId, String ip){
        LinkTarget target = linkMapper.findByJobId(jobId);
        if(target == null){
            throw new InvalidJobIdException("존재하지 않는 jobId입니다.");
        }

        if(target.getProductId() !=null) {
            linkMapper.insertClick(target.getProductId(), ip);
        }
        return LinkResponseDto.of(target.getLink()) ;
    }



}
