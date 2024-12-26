package org.mattpayne.scraper.xlx_dashboard.schedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Page<Schedule> findAllById(Long id, Pageable pageable);

    boolean existsByUrlIgnoreCase(String url);

}
