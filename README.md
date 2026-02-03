 Fitness Club Membership & Class Booking (Milestone 1)

 Team
- Zharqyn — core backend (JDBC, services, repositories)
- Assylzhan — demo/seed/docs improvements

 Stack
- Java (IntelliJ IDEA)
- JDBC
- Supabase (PostgreSQL)

 Entities
- Member
- MembershipType
- FitnessClass
- ClassBooking
- BookingStatus (enum)

 Database tables
- public.members
- public.membership_types
- public.classes
- public.class_bookings

 User stories
- Buy/extend membership
- Book a class
- View attendance history

Exceptions
- MembershipExpiredException — membership expired
- ClassFullException — class is full
- BookingAlreadyExistsException — booking already exists
- NotFoundException — entity not found
- 

Contributions
Zharqyn
- Project structure, entities, repositories, JDBC impl, services, console demo

Assylzhan
- Added schema.sql + seed.sql
- Added documentation (`README.md`)
- Improved demo behavior/output (small changes in `Main.java`)

- Milestone 2 (Assignment 4) – Language Features & Design Patterns
 What we added

1) Generics (common repository pattern)
- repositories/core/Repository<T, ID>
- Repositories can extend it, e.g. MemberRepository extends Repository<Member, Long>

2) Singleton
- DbMapping.getOrCreate(db) is used as a Singleton-style cached mapping.
- It initializes mapping once and reuses it during the app runtime.

3) Builder (new object with many fields)
- MembershipTransaction is created using MembershipTransaction.builder()...build()
- This is easier than a huge constructor and keeps code readable.

4) Factory + Lambdas (pricing/discount policy)
- services/pricing/DiscountPolicyFactory.create(kind) returns different policies.
- Each policy is a lambda: (member, type, price) -> ...

5) Demo usage
- Main.java shows:
  - DB connection
  - DbMapping initialization
  - membership extend
  - class booking (+ handling duplicate booking)
  - attendance history
  - (optional) membership transactions history if the table exists

 How to run (quick)
1. Open Supabase SQL Editor
2. Run schema.sql
3. Run seed.sql
4. Run Main.java in IntelliJ


