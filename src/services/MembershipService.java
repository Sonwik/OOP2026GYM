package services;

import entities.edu.aitu.oop3.entities.Member;
import entities.edu.aitu.oop3.entities.MembershipType;
import exceptions.NotFoundException;
import repositories.MemberRepository;
import repositories.MembershipTypeRepository;

import java.time.LocalDate;

public class MembershipService {
    private final MemberRepository memberRepo;
    private final MembershipTypeRepository typeRepo;

    public MembershipService(MemberRepository memberRepo, MembershipTypeRepository typeRepo) {
        this.memberRepo = memberRepo;
        this.typeRepo = typeRepo;
    }

    public LocalDate buyOrExtend(long memberId, long typeId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));

        MembershipType type = typeRepo.findById(typeId)
                .orElseThrow(() -> new NotFoundException("MembershipType not found: " + typeId));

        LocalDate today = LocalDate.now();

        LocalDate base = (member.getMembershipEndDate() != null && member.getMembershipEndDate().isAfter(today))
                ? member.getMembershipEndDate()
                : today;

        LocalDate newEnd = base.plusDays(type.getDurationDays());

        memberRepo.updateMembership(memberId, typeId, newEnd);
        return newEnd;
    }
}