CREATE MATERIALIZED VIEW accommodations_per_host AS
select h.id         AS host_id,
       count(a.id)  AS num_accommodations
from host h
     LEFT JOIN
     accommodation a ON h.id = a.host_id
GROUP BY h.id;

CREATE MATERIALIZED VIEW hosts_per_country AS
SELECT c.id         AS country_id,
       COUNT(h.id)  AS num_hosts
FROM country c
         LEFT JOIN host h ON c.id = h.country_id
GROUP BY c.id;