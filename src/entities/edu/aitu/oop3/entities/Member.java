package entities.edu.aitu.oop3.entities;
import java.time.LocalDate;
public class Member {
    private Long id;
    private String fullName;
    private Long membershipTypeId;
    private LocalDate membershipEndDate;

    public Member(Long id, String fullName, Long membershipTypeId, LocalDate membershipEndDate) {
        this.id = id;
        this.fullName = fullName;
        this.membershipTypeId = membershipTypeId;
        this.membershipEndDate = membershipEndDate;
    }

    public Member(String fullName) {
        this(null, fullName, null, null);
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public Long getMembershipTypeId() { return membershipTypeId; }
    public LocalDate getMembershipEndDate() { return membershipEndDate; }

    public void setId(Long id) { this.id = id; }
    public void setMembershipTypeId(Long membershipTypeId) { this.membershipTypeId = membershipTypeId; }
    public void setMembershipEndDate(LocalDate membershipEndDate) { this.membershipEndDate = membershipEndDate; }

}
