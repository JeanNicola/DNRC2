package gov.mt.wris.repositories;

public interface CustomLegalLandDescriptionRepository {
    public Long validateLegalLandDescription(
        String description320,
        String description160,
        String description80,
        String description40,
        Long governmentLot,
        Long township,
        String townshipDirection,
        Long range,
        String rangeDirection,
        Long section,
        Long countyId);
}
