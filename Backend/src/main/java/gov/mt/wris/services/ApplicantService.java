package gov.mt.wris.services;

import java.util.Optional;

import gov.mt.wris.dtos.ApplicantDto;
import gov.mt.wris.dtos.ApplicantSortColumn;
import gov.mt.wris.dtos.ApplicantsPageDto;
import gov.mt.wris.dtos.SortDirection;

/**
 * @author Cesar.Zamorano
 *
 */
public interface ApplicantService {

	/**
	 * @param applicationId
	 * @param sortColumn
	 * @param sortDirection
	 * @param contactId
	 * @param lastName
	 * @param firstName
	 * @param middleInitial
	 * @param suffix
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public ApplicantsPageDto getApplicants(Long applicationId, Integer pageNumber, Integer pageSize,
			ApplicantSortColumn sortColumn, SortDirection sortDirection);

	/**
	 * @param applicationId
	 * @param contactId
	 * @param applicantDto
	 * @return
	 */
	public Optional<ApplicantDto> changeApplicant(Long applicationId, Long ownerId, ApplicantDto applicantDto);

	/**
	 * @param applicationId
	 * @param contactId
	 */
	public void deleteApplicant(Long applicationId, Long contactId);

	/**
	 * @param applicationId
	 * @param applicantDto
	 * @return
	 */
	public ApplicantDto createApplicant(Long applicationId, ApplicantDto applicantDto);

	/**
	 * @param dto
	 * @return
	 */
	public ApplicantDto toUpperCase(ApplicantDto dto);
}
