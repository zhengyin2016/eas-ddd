package com.eas.pm.southbound.adapter.repository;

import com.eas.pm.domain.assignment.Assignment;
import com.eas.pm.domain.assignment.AssignmentRole;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Assignment MyBatis Mapper
 */
@Mapper
public interface AssignmentMapper {

    @Insert("INSERT INTO assignment (id, project_id, employee_id, role, allocation, start_date, end_date, released, released_at, version) " +
            "VALUES (#{id}, #{projectId}, #{employeeId}, #{role}, #{allocation}, #{startDate}, #{endDate}, #{released}, #{releasedAt}, #{version})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Assignment assignment);

    @Update("UPDATE assignment SET allocation = #{allocation}, released = #{released}, released_at = #{releasedAt}, version = version + 1 " +
            "WHERE id = #{id} AND version = #{version}")
    int update(Assignment assignment);

    @Select("SELECT * FROM assignment WHERE id = #{id}")
    @Results(id = "assignmentResultMap", value = {
        @Result(property = "id.value", column = "id"),
        @Result(property = "projectId", column = "project_id"),
        @Result(property = "employeeId", column = "employee_id"),
        @Result(property = "role", column = "role"),
        @Result(property = "allocation", column = "allocation"),
        @Result(property = "startDate", column = "start_date"),
        @Result(property = "endDate", column = "end_date"),
        @Result(property = "released", column = "released"),
        @Result(property = "releasedAt", column = "released_at"),
        @Result(property = "version", column = "version")
    })
    AssignmentDO findById(String id);

    @Select("SELECT * FROM assignment WHERE project_id = #{projectId}")
    List<AssignmentDO> findByProjectId(String projectId);

    @Select("SELECT * FROM assignment WHERE employee_id = #{employeeId}")
    List<AssignmentDO> findByEmployeeId(String employeeId);

    @Select("SELECT * FROM assignment WHERE employee_id = #{employeeId} AND released = false " +
            "AND NOT (end_date < #{startDate} OR start_date > #{endDate})")
    List<AssignmentDO> findActiveByEmployeeIdAndDateRange(@Param("employeeId") String employeeId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    @Delete("DELETE FROM assignment WHERE id = #{id}")
    int deleteById(String id);
}
