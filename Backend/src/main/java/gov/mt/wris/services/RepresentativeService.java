package gov.mt.wris.services;

import java.math.BigDecimal;
import java.util.Optional;

import gov.mt.wris.dtos.RepresentativeDto;
import gov.mt.wris.dtos.RepresentativeSortColumn;
import gov.mt.wris.dtos.RepresentativesPageDto;
import gov.mt.wris.dtos.SortDirection;

/**
 * @author Cesar.Zamorano
 *
 */
public interface RepresentativeService {

	/**
	 * @param applicationId
	 * @param ownerId
	 * @param pageNumber
	 * @param pageSize
	 * @param sortColumn
	 * @param sortDirection
	 * @return
	 */
	public RepresentativesPageDto getRepresentatives(BigDecimal applicationId, BigDecimal ownerId, BigDecimal customerId, Integer pageNumber,
			Integer pageSize, RepresentativeSortColumn sortColumn, SortDirection sortDirection);

	/**
	 * @param applicationId
	 * @param ownerId
	 * @param representativeId
	 * @param RepresentativeDto
	 * @return
	 */
	public Optional<RepresentativeDto> changeRepresentative(BigDecimal applicationId, BigDecimal ownerId, BigDecimal customerId, BigDecimal representativeId,
			RepresentativeDto RepresentativeDto);

	/**
	 * @param applicationId
	 * @param ownerId
	 * @param representativeId
	 */
	public void deleteRepresentative(BigDecimal applicationId, BigDecimal ownerId, BigDecimal customerId, BigDecimal representativeId);

	/**
	 * @param applicationId
	 * @param ownerId 
	 * @param RepresentativeDto
	 * @return
	 */
	public RepresentativeDto createRepresentative(BigDecimal applicationId, BigDecimal ownerId, BigDecimal customerId, RepresentativeDto RepresentativeDto);

	/**
	 * @param dto
	 * @return
	 */
	public RepresentativeDto toUpperCase(RepresentativeDto dto);

	public RepresentativesPageDto getObjectorRepresentatives(Long applicationId, Long objectionId, Long customerId, Integer pageNumber,
			Integer pageSize, RepresentativeSortColumn sortColumn, SortDirection sortDirection);
}
