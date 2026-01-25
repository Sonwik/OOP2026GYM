package services;

import entities.edu.aitu.oop3.entities.ClassBooking;
import entities.edu.aitu.oop3.entities.FitnessClass;
import entities.edu.aitu.oop3.entities.Member;
import exceptions.ClassFullException;
import exceptions.MembershipExpiredException;
import exceptions.NotFoundException;
import repositories.ClassBookingRepository;
import repositories.FitnessClassRepository;
import repositories.MemberRepository;

import java.time.LocalDate;

public class BookingService {
    private final MemberRepository memberRepo;
    private final FitnessClassRepository classRepo;
    private final ClassBookingRepository bookingRepo;

    public BookingService(MemberRepository memberRepo,
                          FitnessClassRepository classRepo,
                          ClassBookingRepository bookingRepo) {
        this.memberRepo = memberRepo;
        this.classRepo = classRepo;
        this.bookingRepo = bookingRepo;
    }
//
    public ClassBooking bookClass(long memberId, long classId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));

        FitnessClass fc = classRepo.findById(classId)
                .orElseThrow(() -> new NotFoundException("Class not found: " + classId));

        LocalDate today = LocalDate.now();
        if (member.getMembershipEndDate() == null || member.getMembershipEndDate().isBefore(today)) {
            throw new MembershipExpiredException("Membership expired for member: " + memberId);
        }

        int booked = bookingRepo.countActiveBookingsForClass(classId);
        if (booked >= fc.getCapacity()) {
            throw new ClassFullException("Class is full. capacity=" + fc.getCapacity());
        }
        return bookingRepo.create(new ClassBooking(memberId, classId));
    }
}