package com.eas.hr.domain.talent;

import com.eas.common.ddd.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 储备人才资源库接口
 */
public interface TalentRepository extends Repository<Talent, TalentId> {

    /**
     * 根据状态查找储备人才
     *
     * @param status 状态
     * @return 储备人才列表
     */
    List<Talent> findByStatus(TalentStatus status);

    /**
     * 根据来源查找储备人才
     *
     * @param source 来源
     * @return 储备人才列表
     */
    List<Talent> findBySource(TalentSource source);

    /**
     * 查找可转化的储备人才
     *
     * @return 已通过审批的储备人才列表
     */
    List<Talent> findApprovedForConversion();
}
