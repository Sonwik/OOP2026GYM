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
