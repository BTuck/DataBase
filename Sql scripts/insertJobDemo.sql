﻿INSERT INTO public.demo_jobs (select_query, table_name,script,frequency) VALUES('SELECT serial, type FROM equipment.device','etl_test','INSERT INTO public.script_test (job_name, timestamp) VALUES(''job 1'',CURRENT_TIMESTAMP)', 1),
('SELECT r.name, c.name, d.type, d.serial FROM equipment.device d JOIN customers.customer c ON d.customer_id=c.id JOIN customers.region r ON c.region_id=r.id ORDER BY r.name,c.name', 'etl_billing','INSERT INTO public.script_test (job_name, timestamp) VALUES(''job 2'',CURRENT_TIMESTAMP)',1);