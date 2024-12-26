package org.mattpayne.scraper.xlx_dashboard.schedule;

import org.mattpayne.scraper.xlx_dashboard.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(final ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Page<ScheduleDTO> findAll(final String filter, final Pageable pageable) {
        Page<Schedule> page;
        if (filter != null) {
            Long longFilter = null;
            try {
                longFilter = Long.parseLong(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = scheduleRepository.findAllById(longFilter, pageable);
        } else {
            page = scheduleRepository.findAll(pageable);
        }
        return new PageImpl<>(page.getContent()
                .stream()
                .map(schedule -> mapToDTO(schedule, new ScheduleDTO()))
                .toList(),
                pageable, page.getTotalElements());
    }

    public ScheduleDTO get(final Long id) {
        return scheduleRepository.findById(id)
                .map(schedule -> mapToDTO(schedule, new ScheduleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ScheduleDTO scheduleDTO) {
        final Schedule schedule = new Schedule();
        mapToEntity(scheduleDTO, schedule);
        return scheduleRepository.save(schedule).getId();
    }

    public void update(final Long id, final ScheduleDTO scheduleDTO) {
        final Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(scheduleDTO, schedule);
        scheduleRepository.save(schedule);
    }

    public void delete(final Long id) {
        scheduleRepository.deleteById(id);
    }

    private ScheduleDTO mapToDTO(final Schedule schedule, final ScheduleDTO scheduleDTO) {
        scheduleDTO.setId(schedule.getId());
        scheduleDTO.setUrl(schedule.getUrl());
        scheduleDTO.setCron(schedule.getCron());
        return scheduleDTO;
    }

    private Schedule mapToEntity(final ScheduleDTO scheduleDTO, final Schedule schedule) {
        schedule.setUrl(scheduleDTO.getUrl());
        schedule.setCron(scheduleDTO.getCron());
        return schedule;
    }

    public boolean urlExists(final String url) {
        return scheduleRepository.existsByUrlIgnoreCase(url);
    }

}
