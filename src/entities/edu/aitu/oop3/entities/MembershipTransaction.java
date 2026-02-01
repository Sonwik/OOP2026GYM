package entities.edu.aitu.oop3.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class MembershipTransaction {
    private Long id;
    private Long memberId;
    private Long typeId;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal basePrice;
    private BigDecimal finalPrice;

    private OffsetDateTime createdAt;

    private MembershipTransaction() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MembershipTransaction tx = new MembershipTransaction();

        public Builder memberId(Long v) { tx.memberId = v; return this; }
        public Builder typeId(Long v) { tx.typeId = v; return this; }
        public Builder startDate(LocalDate v) { tx.startDate = v; return this; }
        public Builder endDate(LocalDate v) { tx.endDate = v; return this; }
        public Builder basePrice(BigDecimal v) { tx.basePrice = v; return this; }
        public Builder finalPrice(BigDecimal v) { tx.finalPrice = v; return this; }

        public MembershipTransaction build() {
            if (tx.memberId == null || tx.typeId == null) {
                throw new IllegalStateException("memberId/typeId required");
            }
            if (tx.startDate == null || tx.endDate == null) {
                throw new IllegalStateException("startDate/endDate required");
            }
            if (tx.basePrice == null || tx.finalPrice == null) {
                throw new IllegalStateException("basePrice/finalPrice required");
            }
            return tx;
        }
    }

    // getters/setters для repo
    public Long getId() { return id; }
    public Long getMemberId() { return memberId; }
    public Long getTypeId() { return typeId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getBasePrice() { return basePrice; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
