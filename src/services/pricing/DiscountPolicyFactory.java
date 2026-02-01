package services.pricing;

import java.math.BigDecimal;

public final class DiscountPolicyFactory {

    private DiscountPolicyFactory() {}

    public static DiscountPolicy create(DiscountKind kind) {
        return switch (kind) {
            case NONE -> (m, t, p) -> p;
            case STUDENT_10 -> (m, t, p) -> p.multiply(BigDecimal.valueOf(0.90));
            case VIP_15 -> (m, t, p) -> p.multiply(BigDecimal.valueOf(0.85));
        };
    }
}
