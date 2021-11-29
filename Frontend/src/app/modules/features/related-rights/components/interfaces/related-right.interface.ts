export interface RelatedRightCreationDto {
  relationshipType: string;
  waterRights: {
    waterRightId: string;
    versionId: string;
  }[];
}
