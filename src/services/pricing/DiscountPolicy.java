package services.pricing;

import entities.edu.aitu.oop3.entities.Member;
import entities.edu.aitu.oop3.entities.MembershipType;

import java.math.BigDecimal;

@FunctionalInterface
public interface DiscountPolicy {
    BigDecimal apply(Member member, MembershipType type, BigDecimal basePrice);
}
