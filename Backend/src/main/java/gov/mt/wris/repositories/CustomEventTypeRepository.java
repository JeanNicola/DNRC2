package gov.mt.wris.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gov.mt.wris.dtos.SortDirection;
import gov.mt.wris.models.ApplicationTypeXref;
import gov.mt.wris.models.EventType;

public interface CustomEventTypeRepository {
    public Page<EventType> getEventTypes(Pageable pageable, String sortColumn, SortDirection sortDirection, String code, String description, String dueDays);
}