insert into public.membership_types (name, duration_days, price)
values
    ('Monthly',   30, 15000),
    ('Quarterly', 90, 39000),
    ('Annual',   365, 135000),
    ('Student',   30, 12000)
    on conflict (name) do nothing;

insert into public.members (full_name, membership_type_id, membership_end_date)
values
    ('Айжан Садыкова',   1, current_date + 30),
    ('Данияр Ахметов',   2, current_date + 90),
    ('Алина Ким',        4, current_date + 30),
    ('Руслан Тлеубаев',  3, current_date + 365),
    ('Мадина Нуржанова', 1, current_date + 30),
    ('Ернар Бекетов',    4, current_date + 30),
    ('София Иванова',    2, current_date + 90),
    ('Тимур Сейдахмет',  1, current_date + 30),
    ('Виктория Орлова',  3, current_date + 365),
    ('Никита Павлов',    1, current_date + 30);

insert into public.classes (title, start_time, capacity)
values
    ('Yoga',       now() + interval '1 day', 10),
    ('Boxing',     now() + interval '2 day', 12),
    ('Crossfit',   now() + interval '3 day', 14),
    ('Pilates',    now() + interval '1 day 3 hour', 10),
    ('HIIT',       now() + interval '2 day 2 hour', 16),
    ('Stretching', now() + interval '4 day', 18);

insert into public.class_bookings (member_id, class_id, status)
values
    (1, 1, 'BOOKED'),
    (2, 2, 'BOOKED'),
    (3, 4, 'BOOKED')

    on conflict (member_id, class_id) do nothing;insert into public.membership_transactions (member_id, type_id, start_date, end_date, base_price, final_price)
select m.id, t.id, current_date, current_date + t.duration_days, t.price, t.price
from public.members m
         join public.membership_types t on t.name = 'Monthly'
where m.full_name = 'Айжан Садыкова'
    limit 1;  
