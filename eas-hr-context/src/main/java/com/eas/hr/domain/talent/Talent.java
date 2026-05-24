package com.eas.hr.domain.talent;

import com.eas.common.ddd.AggregateRoot;
import com.eas.hr.domain.employee.EmployeeId;
import com.eas.hr.domain.employee.Skill;
import com.eas.hr.domain.event.TalentConvertedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 储备人才聚合根
 */
public class Talent extends AggregateRoot<TalentId> {

    private String name;
    private TalentSource source;
    private ContactInfo contactInfo;
    private List<Skill> skills;
    private TalentStatus status;
    private EmployeeId convertedEmployeeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;

    private Talent(TalentId id, String name, TalentSource source, ContactInfo contactInfo) {
        super(id);
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.contactInfo = Objects.requireNonNull(contactInfo, "Contact info cannot be null");
        this.status = TalentStatus.NEW;
        this.skills = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Talent create(String name, TalentSource source, ContactInfo contactInfo) {
        return new Talent(TalentId.generate(), name, source, contactInfo);
    }

    public static Talent create(String name, TalentSource source, ContactInfo contactInfo, List<Skill> skills) {
        Talent talent = new Talent(TalentId.generate(), name, source, contactInfo);
        talent.skills = new ArrayList<>(skills);
        return talent;
    }

    public static Talent restore(TalentId id, String name, TalentSource source, ContactInfo contactInfo,
                                 List<Skill> skills, TalentStatus status, EmployeeId convertedEmployeeId,
                                 LocalDateTime createdAt, LocalDateTime updatedAt, String notes) {
        Talent talent = new Talent(id, name, source, contactInfo);
        talent.skills = new ArrayList<>(skills);
        talent.status = status;
        talent.convertedEmployeeId = convertedEmployeeId;
        talent.createdAt = createdAt;
        talent.updatedAt = updatedAt;
        talent.notes = notes;
        return talent;
    }

    public void updateStatus(TalentStatus newStatus) {
        if (this.status == TalentStatus.CONVERTED) {
            throw new IllegalStateException("Cannot update status of converted talent");
        }
        if (!isValidStatusTransition(this.status, newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid status transition from %s to %s", this.status, newStatus));
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void convertToEmployee(EmployeeId employeeId) {
        Objects.requireNonNull(employeeId, "Employee ID cannot be null");
        if (this.status != TalentStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED talent can be converted to employee");
        }
        this.status = TalentStatus.CONVERTED;
        this.convertedEmployeeId = employeeId;
        this.updatedAt = LocalDateTime.now();
        addDomainEvent(new TalentConvertedEvent(getId().value(), employeeId.value(), name));
    }

    public void updateContactInfo(ContactInfo newContactInfo) {
        this.contactInfo = Objects.requireNonNull(newContactInfo, "Contact info cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void addNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    public void addSkill(Skill skill) {
        Objects.requireNonNull(skill, "Skill cannot be null");
        skills.removeIf(s -> s.name().equals(skill.name()));
        skills.add(skill);
        this.updatedAt = LocalDateTime.now();
    }

    private boolean isValidStatusTransition(TalentStatus from, TalentStatus to) {
        return switch (from) {
            case NEW -> to == TalentStatus.CONTACTING || to == TalentStatus.INTERVIEWED || to == TalentStatus.REJECTED;
            case CONTACTING -> to == TalentStatus.INTERVIEWED || to == TalentStatus.REJECTED;
            case INTERVIEWED -> to == TalentStatus.APPROVED || to == TalentStatus.REJECTED;
            case APPROVED -> to == TalentStatus.CONVERTED || to == TalentStatus.REJECTED;
            case CONVERTED, REJECTED -> false;
        };
    }

    // Getters

    public String getName() {
        return name;
    }

    public TalentSource getSource() {
        return source;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public List<Skill> getSkills() {
        return new ArrayList<>(skills);
    }

    public TalentStatus getStatus() {
        return status;
    }

    public EmployeeId getConvertedEmployeeId() {
        return convertedEmployeeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isConverted() {
        return status == TalentStatus.CONVERTED;
    }
}
