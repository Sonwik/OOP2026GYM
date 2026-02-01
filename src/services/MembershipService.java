package services;
import entities.edu.aitu.oop3.entities.MembershipTransaction;
import repositories.MembershipTransactionRepository;
import services.pricing.DiscountKind;
import services.pricing.DiscountPolicyFactory;

import java.math.BigDecimal;
import java.util.List;

import entities.edu.aitu.oop3.entities.Member;
import entities.edu.aitu.oop3.entities.MembershipType;
import exceptions.NotFoundException;
import repositories.MemberRepository;
import repositories.MembershipTypeRepository;

import java.time.LocalDate;

public class MembershipService {
    private final MemberRepository memberRepo;
    private final MembershipTypeRepository typeRepo;
    private final MembershipTransactionRepository txRepo;

    public MembershipService(MemberRepository memberRepo, MembershipTypeRepository typeRepo) {
        this(memberRepo, typeRepo, null);
    }

    public MembershipService(MemberRepository memberRepo,
                             MembershipTypeRepository typeRepo,
                             MembershipTransactionRepository txRepo) {
        this.memberRepo = memberRepo;
        this.typeRepo = typeRepo;
        this.txRepo = txRepo;
    }

    public LocalDate buyOrExtend(long memberId, long typeId) {
        return buyOrExtendWithReceipt(memberId, typeId, DiscountKind.NONE).getEndDate();
    }

    public MembershipTransaction buyOrExtendWithReceipt(long memberId, long typeId, DiscountKind discountKind) {

        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));

        MembershipType type = typeRepo.findById(typeId)
                .orElseThrow(() -> new NotFoundException("MembershipType not found: " + typeId));

        LocalDate today = LocalDate.now();

        LocalDate baseStart = (member.getMembershipEndDate() != null && member.getMembershipEndDate().isAfter(today))
                ? member.getMembershipEndDate()
                : today;

        LocalDate newEnd = baseStart.plusDays(type.getDurationDays());
        memberRepo.updateMembership(memberId, typeId, newEnd);

        BigDecimal basePrice = type.getPrice();
        BigDecimal finalPrice = DiscountPolicyFactory
                .create(discountKind)
                .apply(member, type, basePrice);

        MembershipTransaction tx = MembershipTransaction.builder()
                .memberId(memberId)
                .typeId(typeId)
                .startDate(baseStart)
                .endDate(newEnd)
                .basePrice(basePrice)
                .finalPrice(finalPrice)
                .build();

        if (txRepo != null) {
            txRepo.save(tx);
        }

        return tx;
    }
    public List<MembershipTransaction> history(long memberId) {
        if (txRepo == null) throw new IllegalStateException("txRepo is not configured");
        return txRepo.findByMemberId(memberId);
    }

}