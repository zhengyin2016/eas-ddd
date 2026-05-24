package com.eas.hr.northbound.appservice;

import com.eas.hr.domain.employee.Employee;
import com.eas.hr.domain.employee.EmployeeId;
import com.eas.hr.domain.employee.Skill;
import com.eas.hr.domain.employee.SkillLevel;
import com.eas.hr.domain.talent.*;
import com.eas.hr.message.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 储备人才应用服务
 */
@Service
public class TalentAppService {

    private final TalentRepository talentRepository;
    private final com.eas.hr.domain.employee.EmployeeRepository employeeRepository;

    public TalentAppService(TalentRepository talentRepository,
                           com.eas.hr.domain.employee.EmployeeRepository employeeRepository) {
        this.talentRepository = talentRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * 创建储备人才
     *
     * @param request 创建请求
     * @return 储备人才响应
     */
    @Transactional
    public TalentResponse createTalent(CreateTalentRequest request) {
        ContactInfo contactInfo = request.getWechat() != null
                ? ContactInfo.of(request.getPhone(), request.getEmail(), request.getWechat())
                : ContactInfo.of(request.getPhone(), request.getEmail());

        List<Skill> skills = request.getSkills() != null
                ? request.getSkills().stream()
                    .map(s -> {
                        SkillLevel level = SkillLevel.valueOf(s.getLevel());
                        if (s.getCertifiedDate() != null) {
                            return Skill.certified(s.getName(), level, LocalDate.parse(s.getCertifiedDate()));
                        }
                        return Skill.of(s.getName(), level);
                    })
                    .collect(Collectors.toList())
                : List.of();

        Talent talent = Talent.create(
                request.getName(),
                request.getSource(),
                contactInfo,
                skills
        );

        if (request.getNotes() != null) {
            talent.addNotes(request.getNotes());
        }

        talent = talentRepository.save(talent);

        return toResponse(talent);
    }

    /**
     * 更新储备人才状态
     *
     * @param talentId 储备人才ID
     * @param newStatus 新状态
     */
    @Transactional
    public void updateStatus(String talentId, TalentStatus newStatus) {
        Talent talent = getTalentEntity(talentId);
        talent.updateStatus(newStatus);
        talentRepository.save(talent);
    }

    /**
     * 将储备人才转化为员工
     *
     * @param talentId 储备人才ID
     * @param request  创建员工请求
     * @return 员工响应
     */
    @Transactional
    public EmployeeResponse convertToEmployee(String talentId, CreateEmployeeRequest request) {
        Talent talent = getTalentEntity(talentId);

        // 创建员工
        Employee employee = Employee.create(
                talent.getName(),
                request.getGender(),
                request.getIdCard(),
                request.getPhone(),
                request.getEmail(),
                com.eas.hr.domain.employee.DepartmentId.of(request.getDepartmentId()),
                com.eas.hr.domain.employee.PositionId.of(request.getPositionId())
        );

        // 添加储备人才的技能
        for (Skill skill : talent.getSkills()) {
            employee.addSkill(skill);
        }

        employee = employeeRepository.save(employee);

        // 更新储备人才状态
        talent.convertToEmployee(employee.getId());
        talentRepository.save(talent);

        return toEmployeeResponse(employee);
    }

    /**
     * 获取储备人才信息
     *
     * @param id 储备人才ID
     * @return 储备人才响应
     */
    public TalentResponse getTalent(String id) {
        Talent talent = getTalentEntity(id);
        return toResponse(talent);
    }

    /**
     * 查询待转化的储备人才
     *
     * @return 储备人才列表
     */
    public List<TalentResponse> findApprovedForConversion() {
        List<Talent> talents = talentRepository.findApprovedForConversion();
        return talents.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Helper methods

    private Talent getTalentEntity(String id) {
        TalentId talentId = TalentId.of(id);
        Talent talent = talentRepository.findById(talentId);
        if (talent == null) {
            throw new IllegalArgumentException("Talent not found: " + id);
        }
        return talent;
    }

    private TalentResponse toResponse(Talent talent) {
        TalentResponse response = new TalentResponse();
        response.setId(talent.getId().value());
        response.setName(talent.getName());
        response.setSource(talent.getSource());
        response.setPhone(talent.getContactInfo().phone());
        response.setEmail(talent.getContactInfo().email());
        response.setWechat(talent.getContactInfo().wechat());
        response.setStatus(talent.getStatus());
        response.setSkills(talent.getSkills().stream()
                .map(s -> new SkillResponse(s.name(), s.level(), s.certifiedDate()))
                .collect(Collectors.toList()));
        response.setNotes(talent.getNotes());
        response.setCreatedAt(talent.getCreatedAt());
        response.setUpdatedAt(talent.getUpdatedAt());
        return response;
    }

    private EmployeeResponse toEmployeeResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId().value());
        response.setName(employee.getName());
        response.setGender(employee.getGender());
        response.setPhone(employee.getPhone());
        response.setEmail(employee.getEmail());
        response.setStatus(employee.getStatus());
        response.setHireDate(employee.getHireDate());
        response.setDepartmentId(employee.getDepartmentId().value());
        response.setPositionId(employee.getPositionId().value());
        response.setSkills(employee.getSkills().stream()
                .map(s -> new SkillResponse(s.name(), s.level(), s.certifiedDate()))
                .collect(Collectors.toList()));
        response.setCreatedAt(employee.getCreatedAt());
        response.setUpdatedAt(employee.getUpdatedAt());
        return response;
    }
}
