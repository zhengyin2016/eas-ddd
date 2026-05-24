package com.eas.pm.southbound.adapter.repository;

import com.eas.pm.domain.project.Project;
import com.eas.pm.domain.project.ProjectId;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Project MyBatis Mapper
 */
@Mapper
public interface ProjectMapper {

    @Insert("INSERT INTO project (id, name, customer_id, contract_id, pm_id, status, budget, start_date, end_date, version) " +
            "VALUES (#{id}, #{name}, #{customerId}, #{contractId}, #{pmId}, #{status}, #{budget}, #{startDate}, #{endDate}, #{version})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Project project);

    @Update("UPDATE project SET name = #{name}, customer_id = #{customerId}, contract_id = #{contractId}, " +
            "pm_id = #{pmId}, status = #{status}, budget = #{budget}, start_date = #{startDate}, end_date = #{endDate}, version = version + 1 " +
            "WHERE id = #{id} AND version = #{version}")
    int update(Project project);

    @Select("SELECT * FROM project WHERE id = #{id}")
    @Results(id = "projectResultMap", value = {
        @Result(property = "id.value", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "customerId", column = "customer_id"),
        @Result(property = "contractId", column = "contract_id"),
        @Result(property = "pmId", column = "pm_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "budget", column = "budget"),
        @Result(property = "startDate", column = "start_date"),
        @Result(property = "endDate", column = "end_date"),
        @Result(property = "version", column = "version")
    })
    ProjectDO findById(String id);

    @Select("SELECT * FROM project")
    List<ProjectDO> findAll();

    @Delete("DELETE FROM project WHERE id = #{id}")
    int deleteById(String id);
}
