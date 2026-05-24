package com.eas.hr.southbound.adapter.repository;

import com.eas.hr.domain.employee.Employee;
import com.eas.hr.domain.employee.EmployeeId;
import com.eas.hr.domain.employee.EmployeeStatus;
import com.eas.hr.domain.employee.Gender;
import com.eas.hr.domain.employee.Skill;
import com.eas.hr.domain.employee.SkillLevel;
import com.eas.hr.domain.employee.DepartmentId;
import com.eas.hr.domain.employee.PositionId;
import com.eas.hr.domain.employee.StatusChange;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工MyBatis Mapper接口
 * <p>
 * 这是菱形对称架构中的南向网关 - 适配器(Adapter)。
 * </p>
 */
@Mapper
public interface EmployeeMapper {

    /**
     * 插入员工
     *
     * @param employee 员工实体
     */
    @Insert("INSERT INTO hr_employee (id, name, gender, id_card, phone, email, status, hire_date, " +
            "department_id, position_id, created_at, updated_at) " +
            "VALUES (#{id.value}, #{name}, #{gender}, #{idCard}, #{phone}, #{email}, #{status}, #{hireDate}, " +
            "#{departmentId.value}, #{positionId.value}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = false)
    void insert(Employee employee);

    /**
     * 更新员工
     *
     * @param employee 员工实体
     */
    @Update("UPDATE hr_employee SET name = #{name}, gender = #{gender}, phone = #{phone}, email = #{email}, " +
            "status = #{status}, department_id = #{departmentId.value}, position_id = #{positionId.value}, " +
            "updated_at = #{updatedAt} WHERE id = #{id.value}")
    void update(Employee employee);

    /**
     * 根据ID查找员工
     *
     * @param id 员工ID
     * @return 员工实体
     */
    @Select("SELECT id, name, gender, id_card, phone, email, status, hire_date, " +
            "department_id, position_id, created_at, updated_at FROM hr_employee WHERE id = #{value}")
    @ResultMap("employeeResultMap")
    EmployeeDO findById(String id);

    /**
     * 根据身份证号查找员工
     *
     * @param idCard 身份证号
     * @return 员工实体
     */
    @Select("SELECT id, name, gender, id_card, phone, email, status, hire_date, " +
            "department_id, position_id, created_at, updated_at FROM hr_employee WHERE id_card = #{value}")
    @ResultMap("employeeResultMap")
    EmployeeDO findByIdCard(String idCard);

    /**
     * 根据手机号查找员工
     *
     * @param phone 手机号
     * @return 员工实体
     */
    @Select("SELECT id, name, gender, id_card, phone, email, status, hire_date, " +
            "department_id, position_id, created_at, updated_at FROM hr_employee WHERE phone = #{value}")
    @ResultMap("employeeResultMap")
    EmployeeDO findByPhone(String phone);

    /**
     * 根据部门ID查找员工
     *
     * @param departmentId 部门ID
     * @return 员工实体列表
     */
    @Select("SELECT id, name, gender, id_card, phone, email, status, hire_date, " +
            "department_id, position_id, created_at, updated_at FROM hr_employee " +
            "WHERE department_id = #{value} AND status != 'RESIGNED'")
    @ResultMap("employeeResultMap")
    List<EmployeeDO> findByDepartmentId(String departmentId);

    /**
     * 根据状态查找员工
     *
     * @param status 员工状态
     * @return 员工实体列表
     */
    @Select("SELECT id, name, gender, id_card, phone, email, status, hire_date, " +
            "department_id, position_id, created_at, updated_at FROM hr_employee WHERE status = #{value}")
    @ResultMap("employeeResultMap")
    List<EmployeeDO> findByStatus(String status);

    /**
     * 查询可用员工
     *
     * @param departmentId 部门ID（可选）
     * @param positionId   岗位ID（可选）
     * @param skillName    技能名称（可选）
     * @return 员工实体列表
     */
    @Select("<script>" +
            "SELECT DISTINCT e.id, e.name, e.gender, e.id_card, e.phone, e.email, e.status, e.hire_date, " +
            "e.department_id, e.position_id, e.created_at, e.updated_at " +
            "FROM hr_employee e " +
            "WHERE e.status = 'REGULAR' " +
            "<if test='departmentId != null'>AND e.department_id = #{departmentId}</if> " +
            "<if test='positionId != null'>AND e.position_id = #{positionId}</if> " +
            "<if test='skillName != null'>" +
            "AND e.id IN (SELECT employee_id FROM hr_employee_skill WHERE skill_name = #{skillName})" +
            "</if>" +
            "</script>")
    @ResultMap("employeeResultMap")
    List<EmployeeDO> findAvailableEmployees(@Param("departmentId") String departmentId,
                                            @Param("positionId") String positionId,
                                            @Param("skillName") String skillName);

    /**
     * 插入员工技能
     *
     * @param employeeId 员工ID
     * @param skillName  技能名称
     * @param level      技能等级
     * @param certifiedDate 认证日期
     */
    @Insert("INSERT INTO hr_employee_skill (employee_id, skill_name, skill_level, certified_date) " +
            "VALUES (#{employeeId}, #{skillName}, #{level}, #{certifiedDate})")
    void insertSkill(@Param("employeeId") String employeeId,
                     @Param("skillName") String skillName,
                     @Param("level") String level,
                     @Param("certifiedDate") LocalDate certifiedDate);

    /**
     * 删除员工技能
     *
     * @param employeeId 员工ID
     * @param skillName  技能名称
     */
    @Delete("DELETE FROM hr_employee_skill WHERE employee_id = #{employeeId} AND skill_name = #{skillName}")
    void deleteSkill(@Param("employeeId") String employeeId, @Param("skillName") String skillName);

    /**
     * 查询员工技能
     *
     * @param employeeId 员工ID
     * @return 技能列表
     */
    @Select("SELECT skill_name, skill_level, certified_date FROM hr_employee_skill WHERE employee_id = #{value}")
    @Results({
            @Result(property = "name", column = "skill_name"),
            @Result(property = "level", column = "skill_level", javaType = SkillLevel.class),
            @Result(property = "certifiedDate", column = "certified_date")
    })
    List<Skill> findSkillsByEmployeeId(String employeeId);

    /**
     * 员工数据对象（用于MyBatis映射）
     */
    class EmployeeDO {
        private String id;
        private String name;
        private Gender gender;
        private String idCard;
        private String phone;
        private String email;
        private EmployeeStatus status;
        private LocalDate hireDate;
        private String departmentId;
        private String positionId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Gender getGender() { return gender; }
        public void setGender(Gender gender) { this.gender = gender; }
        public String getIdCard() { return idCard; }
        public void setIdCard(String idCard) { this.idCard = idCard; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public EmployeeStatus getStatus() { return status; }
        public void setStatus(EmployeeStatus status) { this.status = status; }
        public LocalDate getHireDate() { return hireDate; }
        public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
        public String getDepartmentId() { return departmentId; }
        public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
        public String getPositionId() { return positionId; }
        public void setPositionId(String positionId) { this.positionId = positionId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
