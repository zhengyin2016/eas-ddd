package com.eas.hr.southbound.adapter.repository;

import com.eas.hr.domain.employee.Skill;
import com.eas.hr.domain.talent.*;
import com.eas.hr.southbound.port.repository.TalentRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 储备人才资源库适配器实现
 */
@Component
public class TalentRepositoryImpl implements TalentRepositoryPort {

    private final TalentMapper talentMapper;

    public TalentRepositoryImpl(TalentMapper talentMapper) {
        this.talentMapper = talentMapper;
    }

    @Override
    @Transactional
    public Talent save(Talent aggregate) {
        boolean isNew = talentMapper.findById(aggregate.getId().value()) == null;

        if (isNew) {
            talentMapper.insert(aggregate);
        } else {
            talentMapper.update(aggregate);
        }

        // 保存技能
        saveSkills(aggregate);

        return aggregate;
    }

    @Override
    public Talent findById(TalentId id) {
        TalentMapper.TalentDO talentDO = talentMapper.findById(id.value());
        if (talentDO == null) {
            return null;
        }
        return toTalent(talentDO);
    }

    @Override
    public void deleteById(TalentId id) {
        talentMapper.deleteSkills(id.value());
        // talentMapper.delete(id.value());
    }

    @Override
    public List<Talent> findByStatus(TalentStatus status) {
        List<TalentMapper.TalentDO> talentDOs = talentMapper.findByStatus(status.name());
        return talentDOs.stream()
                .map(this::toTalent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Talent> findBySource(TalentSource source) {
        List<TalentMapper.TalentDO> talentDOs = talentMapper.findBySource(source.name());
        return talentDOs.stream()
                .map(this::toTalent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Talent> findApprovedForConversion() {
        List<TalentMapper.TalentDO> talentDOs = talentMapper.findApprovedForConversion();
        return talentDOs.stream()
                .map(this::toTalent)
                .collect(Collectors.toList());
    }

    private void saveSkills(Talent talent) {
        talentMapper.deleteSkills(talent.getId().value());

        for (Skill skill : talent.getSkills()) {
            talentMapper.insertSkill(
                    talent.getId().value(),
                    skill.name(),
                    skill.level().name(),
                    skill.certifiedDate()
            );
        }
    }

    private Talent toTalent(TalentMapper.TalentDO talentDO) {
        List<Skill> skills = talentMapper.findSkillsByTalentId(talentDO.getId());

        ContactInfo contactInfo = talentDO.getWechat() != null
                ? ContactInfo.of(talentDO.getPhone(), talentDO.getEmail(), talentDO.getWechat())
                : ContactInfo.of(talentDO.getPhone(), talentDO.getEmail());

        return Talent.restore(
                TalentId.of(talentDO.getId()),
                talentDO.getName(),
                talentDO.getSource(),
                contactInfo,
                skills,
                talentDO.getStatus(),
                null, // convertedEmployeeId
                talentDO.getCreatedAt(),
                talentDO.getUpdatedAt(),
                talentDO.getNotes()
        );
    }
}
