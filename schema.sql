create table if not exists public.membership_types (
    id bigserial primary key,
    name text not null unique,
    duration_days int not null check (duration_days > 0),
    price numeric(10,2) not null check (price >= 0)
    );

create table if not exists public.members (
    id bigserial primary key,
    full_name text not null,
    membership_type_id bigint references public.membership_types(id),
    membership_end_date date
    );

create table if not exists public.classes (
    id bigserial primary key,
    title text not null,
    start_time timestamptz not null,
    capacity int not null check (capacity > 0)
    );

create table if not exists public.class_bookings (
    id bigserial primary key,
    member_id bigint not null references public.members(id),
    class_id bigint not null references public.classes(id),
    status text not null default 'BOOKED',
    booked_at timestamptz not null default now(),
    unique (member_id, class_id)
    ); 
create table if not exists public.membership_transactions (
  id bigserial primary key,
  member_id bigint not null references public.members(id),
  type_id bigint not null references public.membership_types(id),
  start_date date not null,
  end_date date not null,
  base_price numeric(10,2) not null,
  final_price numeric(10,2) not null,
  created_at timestamptz not null default now()
);

create index if not exists idx_membership_tx_member_id
  on public.membership_transactions(member_id);

create index if not exists idx_class_bookings_member_id on public.class_bookings(member_id);

create index if not exists idx_class_bookings_class_id on public.class_bookings(class_id);
