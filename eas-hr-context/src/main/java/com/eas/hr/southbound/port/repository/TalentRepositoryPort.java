package com.eas.hr.southbound.port.repository;

import com.eas.hr.domain.talent.Talent;
import com.eas.hr.domain.talent.TalentId;
import com.eas.hr.domain.talent.TalentSource;
import com.eas.hr.domain.talent.TalentStatus;

import java.util.List;

/**
 * 储备人才资源库端口
 */
public interface TalentRepositoryPort extends com.eas.hr.domain.talent.TalentRepository {

    /**
     * 根据来源查找储备人才
     *
     * @param source 来源
     * @return 储备人才列表
     */
    List<Talent> findBySource(TalentSource source);
}
