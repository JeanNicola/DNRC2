package gov.mt.wris.constants;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public final class Constants {
    // Privileges
    public static final String SELECT = "SELECT";
    public static final String INSERT = "INSERT";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
    public static final String EXECUTE= "EXECUTE";

    // Tables and Columns
    public static final String REFERENCE_TABLE = "WRD_REF_CODES";
    public static final String DOMAIN = "RV_DOMAIN";
    public static final String LOW_VALUE = "RV_LOW_VALUE";
    public static final String MEANING = "RV_MEANING";
    public static final String ABBREVIATION = "RV_ABBREVIATION";
    public static final String YES_NO_DOMAIN = "YES_NO";
    public static final String CONTRACT_FOR_DEED_RLE_DOMAIN = "CTRCT_DEED_RLE";
    public static final String RELATED_RIGHT_DOMAIN = "RELATED RIGHT";

    public static final String CASE_ASSIGNMENT_TYPES_TABLE = "WRD_CASE_ASSIGNMENT_TYPES";
    public static final String CASE_ASSIGNMENT_DOMAIN = "CASE_ASSIGNMENT_TYPE_PROGRAMS";
    public static final String ASSIGNMENT_CODE = "ASST_CD";
    public static final String ASSIGNMENT_TYPE = "DESCR";
    public static final String PROGRAM = "PROGRAM";

    public static final String CASE_ASSIGNMENTS_TABLE = "WRD_CASE_ASSIGNMENTS";
    public static final String CASE_ASSIGNMENTS_SEQUENCE = "WRD_CASN_SEQ";
    public static final String CASE_ASSIGNMENT_ID = "ASSN_ID_SEQ";
    public static final String CASE_ASSIGNMENT_BEGIN_DATE = "ASSN_BGN_DT";
    public static final String CASE_ASSIGNMENT_END_DATE = "ASSN_END_DT";

    public static final String CASE_STATUS_TABLE = "WRD_CASE_STATUS";
    public static final String CASE_STATUS_CODE = "STAT_CD";
    public static final String CASE_STATUS_DESCRIPTION = "DESCR";
    public static final String CASE_STATUS_CLOSED = "CLSD";

    public static final String CASE_TYPE_TABLE = "WRD_CASE_TYPES";
    public static final String CASE_TYPE_DOMAIN= "CASE_TYPE_PROGRAMS";
    public static final String CASE_TYPE_CODE = "CATP_CD";
    public static final String CASE_TYPE_DESCR = "DESCR";
    public static final String CASE_TYPE_PROGRAM = "PROGRAM";
    public static final String CASE_TYPE_PROGRAM_NA = "NA";
    public static final String CASE_TYPE_PROGRAM_WC = "WC";
    public static final String CASE_TYPE_CONTESTED_CASE_HEARING = "ADM";
    public static final String CASE_TYPE_SHOW_CAUSE_HEARING = "SCH";
    public static final String CASE_TYPE_ARM_RULE_CASE = "ARMR";

    public static final String WATER_COURT_CASE_TYPES_TABLE = "WRD_WATER_COURT_CASES";
    public static final String WATER_COURT_CASE_TYPES_SEQUENCE = "WRD_CASE_SEQ";
    public static final String CASE_ID = "CASE_ID_SEQ";
    public static final String CASE_NO = "CASE_NO";
    public static final String SIGNIFICANT_CASE = "SIGN_CASE";
    public static final String CASE_COMMENTS = "CASE_COMMENTS";

    public static final String CASE_SCHEDULE_TABLE = "WRD_CASE_SCHEDULE";
    public static final String CASE_SCHEDULE_SEQUENCE = "WRD_CSCH_SEQ";
    public static final String SCHEDULE_ID = "SCH_ID_SEQ";
    public static final String SCHEDULE_STATUS = "SCH_STATUS";
    public static final String SCHEDULE_DATE = "SCH_DATE";
    public static final String SCHEDULE_BEGIN_TIME = "SCH_BGN_TIME";
    public static final String SCHEDULE_COMMENTS = "SCH_COMMENTS";

    public static final String ORDER_ADOPTING_DATE = "ORD_ADOPTING_DT";
    //public static final String CASE_TYPE_EVENT_TYPE_TABLE = "WRD_CASE_TYPE_EVENT_TYPE_XREFS";

    public static final String EVENT_TYPE_TABLE = "WRD_EVENT_TYPES";
    public static final String EVENT_TYPE_CODE = "EVTP_CD";
    public static final String EVENT_TYPE_DESCR = "DESCR";
    public static final String EVENT_TYPE_DUE_DAYS = "RSPNS_DUE_DAYS_NO";

    public static final String COURT_CASE_VERSION_XREF_TABLE = "WRD_CASE_VERSION_XREFS";
    public static final String CASE_TYPE_XREF_TABLE = "WRD_CASE_TYPE_EVENT_TYPE_XREFS";
    public static final String CASES_AND_OBJECTIONS_ADMIN_ROLE = "WRD_CASES_OBJS_ADMIN_ROLE";

    public static final String DISTRICT_COURT_TABLE = "WRD_CASE_DISTRICT_COURT";
    public static final String DISTRICT_COURT_SEQUENCE = "WRD_CDCT_SEQ";
    public static final String DISTRICT_CAUSE_NUMBER = "DIST_CAUSE_NO";
    public static final String DISTRICT_COURT = "DIST_COURT";
    public static final String DISTRICT_JUDGE = "DIST_JUDGE_DNRC_ID";
    public static final String DISTRICT_SUPREME_CAUSE_NUMBER = "DIST_SUPR_CRT_CAUSE_NO";
    public static final String DISTRICT_COURT_STAFF = "DCJD";

    public static final String APPLICATION_TYPE_XREF_TABLE = "WRD_EVENT_APP_TYPE_XREFS";
    public static final String APPLICATION_TYPE_TABLE = "WRD_APPLICATION_TYPES";
    public static final String APPLICATION_TYPE_CODE = "APTP_CD";
    public static final String APPLICATION_TYPE_DESCR = "DESCR";
    public static final String AUTO_COMPLETE_TYPE_CODE = "AUTO_COMPLETE_TYPE";
    public static final String APPLICATION_TYPE_FILING_FEE = "FILING_FEE";
    public static final String APPLICATION_TYPE_FEE_OTHER = "FILING_FEE_OTHER";
    public static final String APPLICATION_TYPE_FEE_DISCOUNT = "FEE_DISCOUNT";
    public static final String APPLICATION_TYPE_FEE_CGWA = "FILING_FEE_CGWA";
    public static final String APPLICATION_TYPE_CASE_REPORT = "CASE_REPORT";
    public static final String APPLICATION_TYPE_OBJECTIONS_ALLOWED = "OBJECTIONS_ALW";

    public static final String DECREE_TYPE_XREF_TABLE = "WRD_DECREE_TYP_EVNT_TYP_XREFS";
    public static final String DECREE_TYPE_TABLE = "WRD_DECREE_TYPES";
    public static final String DECREE_TYPE_CODE = "DCTP_CD";
    public static final String DECREE_TYPE_DESCR = "DESCR";

    public static final String ZIP_CODE_TABLE = "WRD_ZIP_CODES";
    public static final String ZIP_CODE_ID = "ZPCD_ID_SEQ";
    public static final String ZIP_SEQUENCE = "WRD_ZPCD_SEQ";
    public static final String ZIP_CODE = "ZIP_CD";
    public static final String CITY_ID = "CITY_ID_SEQ";
    public static final String CITY_SEQUENCE = "WRD_CITY_SEQ";
    public static final String CITY_TABLE = "WRD_CITIES";
    public static final String CITY_NAME = "NM";
    public static final String STATE_CODE = "STT_CD";
    public static final String STATE_TABLE = "WRD_STATES";
    public static final String STATE_NAME = "NM";

    public static final String WATER_RIGHT_TABLE = "WRD_WATER_RIGHTS";
    public static final String WATER_RIGHT_SEQUENCE = "WRD_WRGT_SEQ";
    public static final String WATER_RIGHT_ID = "WRGT_ID_SEQ";
    public static final String WATER_RIGHT_BASIN = "BOCA_CD";
    public static final String WATER_RIGHT_NUMBER = "WTR_ID";
    public static final String WATER_RIGHT_EXT = "EXT";
    public static final String WATER_RIGHT_CON_DIST_NO = "CON_DIST_NO";
    public static final String WATER_RIGHT_CON_DIST_DATE = "CON_DIST_PRITY_DT";
    public static final String WATER_RESERVATION_ID = "WTSV_ID_SEQ";
    public static final String WATER_RIGHT_SUB_BASIN = "SUB_BOCA_CD";
    public static final String WATER_RIGHT_CREATE_DATE = "DTM_CREATED";
    public static final String DIVIDED_OWNSHP = "DIVIDED_OWNSHP";
    public static final String SEVERED = "SEVERED";
    public static final String ORIGINAL_WATER_RIGHT_ID = "WRGT_ID_SEQ_SECOND";
    public static final String COMPACT_ROLE = "WRD_COMPACT_MANAGER_ROLE";
    public static final List<String> WATER_RIGHT_ALLOWED_CREATION_TYPE = Arrays.asList("STOC", "RSCL", "ITSC");
    public static final String WATER_RIGHT_SEVERED = "SEVERED";
    public static final String WATER_RIGHT_SEVERED_CD = "SEVR";

    public static final String WATER_RIGHT_STATUS_TABLE = "WRD_WATER_RIGHT_STATUSES";
    public static final String WATER_RIGHT_STATUS_CODE = "WRST_CD";
    public static final String WATER_RIGHT_STATUS_DESCR = "DESCR";
    public static final String WATER_RIGHT_STATUS_CODE_TERMINATED = "TERM";
    public static final String LOV_ITEM = "LOV_ITEM";

    public static final String WATER_RIGHT_TYPE_TABLE = "WRD_WATER_RIGHT_TYPES";
    public static final String WATER_RIGHT_TYPE_CODE = "WRTE_CD";
    public static final String WATER_RIGHT_TYPE_DESCR = "DESCR";

    public static final String WATER_RIGHT_TYPE_WTR_RESV = "WRWR";
    public static final String WATER_RIGHT_TYPE_PROGRAM = "PROGRAM";
    public static final String WATER_RIGHT_TYPE_GROUND_WATER_CERT = "GWCT";

    public static final String MAILING_JOB_XREF_TABLE = "WRD_MAILING_LBL_JOB_CSTM_XREFS";
    public static final String MAILING_JOB_XREF_ID = "MLJB_ID_SEQ";
    public static final String MAILING_JOB_XREF_CONTACT_ID = "CUST_ID_SEQ";


    public static final String MAILING_JOB_WATER_RIGHT_XREF_TABLE = "WRD_MAILING_LBL_JB_WTRRT_XREFS";
    public static final String MAILING_JOB_WATER_RIGHT_XREF_ID = "MLJB_ID_SEQ";
    public static final String MAILING_JOB_WATER_RIGHT_XREF_WATER_RIGHT_ID = "WRGT_ID_SEQ";

    public static final String ADDRESS_TABLE = "WRD_ADDRESSES";
    public static final String ADDRESS_ID = "ADDR_ID_SEQ";
    public static final String CUSTOMER_ID = "CUST_ID_SEQ";
    public static final String ADDRESS_SEQUENCE = "WRD_ADDR_SEQ";
    public static final String PRIMARY_MAIL = "PRI_MAIL";
    public static final String ADDRESS_LINE_1 = "ADDR_LN_1";
    public static final String ADDRESS_LINE_2 = "ADDR_LN_2";
    public static final String ADDRESS_LINE_3 = "ADDR_LN_3";
    public static final String PL_FOUR = "PL_4";
    public static final String FOREIGN_ADDRESS = "FRGN_ADDR";
    public static final String FOREIGN_POSTAL = "FRGN_POSTAL";
    public static final String DATE_CREATED = "DTM_CREATED";
    public static final String MODIFIED_REASON = "MOD_REASON";
    public static final String UNRESOLVED_FLAG = "UNRESOLVED_FLAG";

    public static final String APPLICATION_TABLE = "WRD_APPLICATIONS";
    public static final String APPLICATION_ID = "APPL_ID_SEQ";
    public static final String APPLICATION_REGARDING_ID = "APPL_ID_SEQ_REGARDING";
    public static final String APPLICATION_SEQUENCE = "WRD_APPL_SEQ";
    public static final List<String> DISALLOWED_APPLICATION_TYPES = Arrays.asList("607","608", "617", "618", "626", "627", "650", "651");
    public static final String BASIN = "BOCA_CD";
    public static final String BASIN_DESCR = "DESCR";
    public static final String OFFICE_ID = "OFFC_ID_SEQ";
    public static final String PROCESSOR_OFFICE_ID = "OFFC_ID_SEQ_PROCESSOR";
    public static final String PROCESSOR_STAFF_ID = "DNRC_ID_PROCESSOR";
    public static final String FILING_FEE = "FILING_FEE";
    public static final String FEE_STATUS = "FILING_FEE_STATUS";
    public static final String PAYMENT_APPLICATION_FEE_STATUS_DOMAIN = "PAYMENT_APPLICATION_FEE_STATUS";
    public static final String FEE_DISCOUNT = "FILING_FEE_DISCOUNT";
    public static final String FEE_WAIVED = "FILING_FEE_WAIVED";
    public static final String FEE_OTHER = "FILING_FEE_OTHER";
    public static final String FEE_CGWA = "FILING_FEE_CGWA";
    public static final String NON_FILED_WATER_PROJECT = "NON_FILED_WP";

    public static final LocalDateTime FILING_FEE_START_DATE = LocalDateTime.parse("2018-08-19T00:00");

    public static final String PAYMENT_TABLE = "WRD_PAYMENTS";
    public static final String PAYMENT_ID = "PYMT_ID_SEQ";
    public static final String PAYMENT_SEQUENCE = "WRD_PYMT_SEQ";
    public static final String PAYMENT_TRACKING_NO = "TLMS_RECEIPT_NO";
    public static final String PAYMENT_AMOUNT = "PAID_AMT";
    public static final String PAYMENT_DATE = "PAID_DT";
    public static final String PAYMENT_APPLICATION_ORIGIN_DOMAIN = "PAYMENT_APPLICATION_ORIGIN";
    public static final String PAYMENT_ORIGIN = "PYMT_ORGN";

    public static final String CHANGE_DESC = "CHG_DESCR";
    public static final String PAST_USE = "PAST_USE";
    public static final String ADDITIONAL_INFO = "ADDITIONAL_INFORMATION";
    public static final String DISTANCE = "DISTANCE";
    public static final String DIRECTION = "DIRECTION";

    public static final String BASIN_COMPACT_TABLE = "WRD_BASIN_OR_COMPACT_AREAS";
    public static final String BASIN_CODE = "BOCA_CD";
    public static final String BOCA_TYPE = "BOCA_TYP";
    public static final String PARENT_BASIN = "BOCA_CD_SUBASIN_IN";
    public static final String SUBBASIN_TYPE = "SUBN";

    public static final String OWNER_TABLE = "WRD_OWNERS";
    public static final String OWNER_ID = "OWNR_ID_SEQ";
    public static final String OWNER_SEQUENCE = "WRD_OWNR_SEQ";
    public static final String OWNER_CUSTOMER_ID = "CUST_ID_SEQ";
    public static final String OWNER_BEGIN_DATE = "BGN_DT";
    public static final String OWNER_END_DATE = "END_DT";
    public static final String OWNER_ORIGINAL = "ORIG_OWNR";
    public static final String OWNER_CONTRACT_FOR_DEED = "CONTT_FOR_DEED";
    public static final String OWNER_ORIGIN = "ELEM_ORGN";
    public static final String OWNER_ORIGIN_DOMAIN = "OWNER_ORIGINS";
    public static final String OWNER_RECEIVED_MAIL = "RECV_MAIL";

    public static final String REPRESENTATIVE_TABLE = "WRD_REPRESENTATIVES";
    public static final String REPRESENTATIVE_SEQUENCE = "WRD_REPT_SEQ";
    public static final String REPRESENTATIVE_ID = "REPT_ID_SEQ";
    public static final String REPRESENTATIVE_OWNER_ID = "OWNR_ID_SEQ";
    public static final String REPRESENTATIVE_CUSTOMER_ID = "CUST_ID_SEQ";
    public static final String REPRESENTATIVE_OWNER_CUSTOMER_ID = "CUST_ID_SEQ_SEC";
    public static final String REPRESENTATIVE_OBJECTOR_CUSTOMER_ID = "CUST_ID_SEQ_THR";
    public static final String REPRESENTATIVE_BEGIN_DATE = "BGN_DT";
    public static final String REPRESENTATIVE_END_DATE = "END_DT";
    public static final String REPRESENTATIVE_ROLE_TYPE = "RLTP_CD";

    public static final String ROLE_TYPES_TABLE = "WRD_ROLE_TYPES";
    public static final String ROLE_TYPES_CODE = "RLTP_CD";
    public static final String ROLE_TYPES_DESCR = "DESCR";
    public static final String ROLE_TYPES_SEQUENCE = "WRD_RLTP_SEQ";

    public static final String CUSTOMER_TABLE = "WRD_CUSTOMERS";
    public static final String CUSTOMER_SEQUENCE = "WRD_CUST_SEQ";
    public static final String LAST_NAME = "LST_NM_OR_BUSN_NM";
    public static final String FIRST_NAME = "FST_NM";
    public static final String MIDDLE_INITIAL = "MID_INT";
    public static final String SUFFIX = "SUFX";
    public static final String CONTACT_TYPE = "CTTP_CD";
    public static final String CONTACT_STATUS = "SEND_MAIL";

    public static final String EVENT_TABLE = "WRD_EVENT_DATES";
    public static final String EVENT_ID = "EVDT_ID_SEQ";
    public static final String EVENT_SEQUENCE = "WRD_EVDT_SEQ";
    public static final String EVENT_DATE = "DT_OF_EVNT";
    public static final String EVENT_COMMENT = "EVNT_COMT";
    public static final String EVENT_RESPONSE_DUE = "RSPNS_DUE_DT";
    public static final String EVENT_CREATED_BY="CREATED_BY";
    public static final String EVENT_MODIFIED_DATE = "DTM_MOD";
    public static final String EVENT_CREATED_DATE = "DTM_CREATED";
    public static final String EVENT_MODIFIED_BY = "MOD_BY";
    public static final String P_FILING_FEE_START_DATE = "19-AUG-2018";
    public static final String EVENT_DATE_FORMAT_OWNERSHIP_TRANSFER = "dd-MM-yyyy";
    public static final String EVENT_F643 = "F643";
    public static final String EVENT_FRMR = "FRMR";
    public static final String EVENT_F641 = "F641";
    public static final String EVENT_F642 = "F642";

    public static final String ABSTRACT_TABLE = "WRD_ABSTRACTS";
    public static final String ABSTRACT_CODE = "ABST_CD";
    public static final String ABSTRACT_NAME = "NM";
    public static final String ABSTRACT_REPORT = "RPT_MODULE";


    public static final String SUBDIVISION_CODES_TABLE = "WRD_SUBDIVISION_CODES";
	public static final String SUBDIVISION_CODES_CODE = "SBCD_CD";
	public static final String SUBDIVISION_CODES_COUNTY_ID = "COUT_ID_SEQ";
	public static final String SUBDIVISION_CODES_DNRC_NAME = "DNRC_NM";
	public static final String SUBDIVISION_CODES_DOR_NAME = "DOR_NM";

    public static final String SUBDIVISION_XREFS_TABLE = "WRD_POU_SUBDIVISION_XREFS";
    public static final String SUBDIVISION_BLOCK = "BLK";
    public static final String SUBDIVISION_LOT = "LOT";

    public static final String POU_EXAMINATIONS_XREF = "WRD_POU_EXAMINATION_POU_XREFS";
    public static final String POU_EXAMINATIONS_ACRES = "ACRES";
    public static final String AERIAL_ID = "APTO_ID_SEQ";

    public static final String AERIAL_TABLE = "WRD_AERIAL_PHOTOS";
    public static final String AERIAL_SEQUENCE = "WRD_APTO_SEQ";

    public static final String AERIAL_TYPE = "PHT_TYP";
    public static final String AERIAL_NUMBER = "NM_NO";
    public static final String AERIAL_DATE = "APTO_DT_TXT";

    public static final String PHOTO_TYPES = "PHOTO TYPE";

    public static final String POU_SURVEY_TYPE_XREF = "WRD_POU_SURVEY_TYPE_XREFS";
    public static final String POU_SURVEY_ID = "WRSY_ID_SEQ";

	public static final String COUNTIES_TABLE = "WRD_COUNTIES";
	public static final String COUNTIES_ID = "COUT_ID_SEQ";
	public static final String COUNTIES_NAME = "NM";
	public static final String COUNTIES_FIPS_CODE = "FIPS_CD";
	public static final String COUNTIES_STATE_CODE = "STT_CD";
	public static final String COUNTIES_STATE_CODE_ID = "STATE_CO_ID";
	public static final String COUNTIES_MONTANA_CODE= "MT";

	public static final String MASTER_STAFF_INDEXES_TABLE = "WRD_MASTER_STAFF_INDEXES";
	public static final String MASTER_STAFF_INDEXES_DNRC_ID = "DNRC_ID";
	public static final String MASTER_STAFF_INDEXES_DIRECTORY_USER = "C_NO";
	public static final String MASTER_STAFF_INDEXES_LAST_NAME = "LST_NM";
	public static final String MASTER_STAFF_INDEXES_FIRST_NAME = "FST_NM";
	public static final String MASTER_STAFF_INDEXES_MID_INITIAL = "MID_INT";
    public static final String MASTER_STAFF_INDEXES_OFFICE_ID = "OFFC_ID_SEQ";
    public static final String MASTER_STAFF_INDEXES_END_DATE = "END_DT";
    public static final String MASTER_STAFF_INDEXES_POSITION_CODE = "PSTP_CD";

	public static final String STAFF_APPL_XREFS_TABLE = "WRD_STAFF_APPL_XREFS";
	public static final String STAFF_APPL_XREFS_APPL_ID = "APPL_ID_SEQ";
	public static final String STAFF_APPL_XREFS_ID = "SAXR_ID_SEQ";
	public static final String STAFF_APPL_XREFS_SEQUENCE = "WRD_SAXR_SEQ";
	public static final String STAFF_APPL_XREFS_DNRC_ID = "DNRC_ID";
    public static final String STAFF_APPL_XREFS_BEGIN_DATE = "BGN_DT";
    public static final String STAFF_APPL_XREFS_END_DATE = "END_DT";

	public static final String OFFICE_APPL_XREFS_TABLE = "WRD_OFFICE_APPL_XREFS";
	public static final String OFFICE_APPL_XREFS_APPL_ID = "APPL_ID_SEQ";
	public static final String OFFICE_APPL_XREFS_ID = "OAXR_ID_SEQ";
	public static final String OFFICE_APPL_XREFS_SEQUENCE = "WRD_OAXR_SEQ";
	public static final String OFFICE_APPL_XREFS_OFFICE_ID = "OFFC_ID_SEQ";
    public static final String OFFICE_APPL_XREFS_RECEIVED_DATE = "BGN_DT";
    public static final String OFFICE_APPL_XREFS_SENT_DATE = "END_DT";

    public static final String OFFICES_TABLE = "WRD_OFFICES";
    public static final String OFFICES_ID = "OFFC_ID_SEQ";
	public static final String OFFICES_DESCR = "DESCR";
	public static final String OFFICES_NOTES = "NOTES";
	public static final String OFFICES_SEQUENCE = "WRD_OFFC_SEQ";

    public static final String OFFICE_CONTACT_TABLE = "WRD_OFFICE_CUSTOMER_XREFS";
    public static final String OFFICE_DEFAULT_PARTY = "MAIL_JOB_INCLUDE";

    public static final String MAILING_JOB_TABLE = "WRD_MAILING_LABEL_JOBS";
    public static final String MAILING_JOB_ID = "MLJB_ID_SEQ";
    public static final String MAILING_JOB_SEQUENCE = "WRD_MLJB_SEQ";
    public static final String MAILING_DATE_GENERATED = "DT_GENERATED";
    public static final String MAILING_JOB_HEADER = "HEADER";


	public static final String OBJECTIONS_TABLE = "WRD_OBJECTION_INTNT_TO_APPEARS";
    public static final String OBJECTIONS_SEQUENCE = "WRD_OITA_SEQ";
	public static final String OBJECTIONS_ID = "OITA_ID_SEQ";
	public static final String OBJECTIONS_LATE = "LTE_OBJN";
	public static final String OBJECTIONS_STATUS = "STATUS";
	public static final String OBJECTIONS_DATE_RECEIVED = "DT_RECEIVED";
	public static final String OBJECTIONS_CASE_ID = "CASE_ID_SEQ";
	public static final String OBJECTIONS_TYPE = "OBJN_TYP";
    public static final String OBJECTIONS_TYPE_COUNTER_OBJECTION = "COB";
    public static final String OBJECTIONS_TYPE_INTENT_TO_APPEAR = "ITA";
    public static final String OBJECTIONS_TYPE_OBJECTION_TO_RIGHT = "OTW";
    public static final String OBJECTIONS_TYPE_ON_MOTION = "OMO";
    public static final String OBJECTIONS_TYPE_OBJECTION_TO_DECREE = "OTD";
    public static final String OBJECTIONS_TYPE_OBJECTION_TO_APPLICATION = "OTA";
    public static final String OBJECTIONS_TYPE_CERTIFICATION = "CET";
    public static final String OBJECTIONS_LATE_DEFAULT = "N";
    public static final String OBJECTIONS_STATUS_DEFAULT = "OPEN";


	public static final String OBJECTORS_TABLE = "WRD_OBJECTORS";
	public static final String OBJECTORS_OBJECTIONS_ID = "OITA_ID_SEQ";
	public static final String OBJECTORS_CUSTOMER_ID = "CUST_ID_SEQ";
	public static final String OBJECTORS_STATUS = "STATUS";
	public static final String OBJECTORS_END_DATE = "END_DT";


    public static final String ELEMENT_OBJECTION_TABLE = "WRD_ELEMENT_OBJECTIONS";
    public static final String ELEMENT_OBJECTION_SEQUENCE = "WRD_EOBJ_SEQ";
    public static final String ELEMENT_OBJECTION_ID = "EOBJ_ID_SEQ";
    public static final String ELEMENT_OBJECTION_COMMENT = "EOBJ_COMT";

    public static final String OBJECTIONS_TYPE_DOMAIN = "OBJECTION TYPE";
    public static final String OBJECTIONS_STATUS_DOMAIN = "OBJECTION STATUS";

    public static final String CASE_APPLICATION_XREF_TABLE = "WRD_CASE_APPLICATION_XREFS";

	public static final String CORRECT_COMPLETES_TABLE = "WRD_CORRECT_COMPLETES";
	public static final String CORRECT_COMPLETES_ID = "CRCM_ID_SEQ";
	public static final String CORRECT_COMPLETES_TYPE = "CRTP_CD";
    public static final String CORRECT_COMPLETE_DATE = "COR_COMPLETE_DT";

	public static final String CORRECT_TYPES_TABLE = "WRD_CORRECT_TYPES";
	public static final String CORRECT_TYPES_CODE = "CRTP_CD";
	public static final String CORRECT_TYPES_DESCRIPTION = "DESCR";

    public static final String VERSIONS_TABLE = "WRD_VERSIONS";
    public static final String VERSIONS_ID = "VERS_ID_SEQ";
    public static final String DNRC_THRESHOLD_PRIORITY_DATE = "06-08-2008";

    public static final String VERSION_APPLICATION_XREFS_TABLE = "WRD_VERSION_APPLICATION_XREFS";
    public static final String CREATED_BY = "CREATED_BY";
    public static final String DTM_CREATED = "DTM_CREATED";
    public static final String MOD_BY = "MOD_BY";
    public static final String DTM_MOD = "DTM_MOD";

    public static final String PURPOSES_TABLE = "WRD_PURPOSES";
    public static final String PURPOSE_SEQUENCE = "WRD_PURS_SEQ";
    public static final String PURPOSE_ID = "PURS_ID_SEQ";
    public static final String PURT_CODE = "PURT_CD";
    public static final String ELEMENT_ORIGIN = "ELEM_ORGN";
    public static final String VOLUME_AMOUNT = "VOL_AMT";
    public static final String VOLUME_DESCRIPTION = "VOL_DESCR";
    public static final String ANIMAL_UNITS = "ANML_UNT";
    public static final String PURPOSE_CLARIFICATION = "NM";
    public static final String HOUSEHOLD = "FAMILIES";
    public static final String CLIMATIC_AREA_CODE = "CLAR_CD";
    public static final String CROP_ROTATION = "ROTATION_SYS";
    public static final String MODIFIED_IN_VERSION = "MOD_IN_THIS_VER";

    public static final String PURPOSE_TYPES_TABLE = "WRD_PURPOSE_TYPES";
    public static final String PURPOSE_TYPE_DESCRIPTION = "DESCR";
    public static final String PURPOSE_TYPE_REQUIRED = "REQD_POU";
    public static final String PURPOSE_GROUP_CODE = "PURP_GRP_CD";
    public static final String PURPOSE_TYPE_CODE_IRRIGATION = "IR";
    public static final String PURPOSE_TYPE_CODE_MULTIPLE_DOMESTIC = "MD";
    public static final String PURPOSE_TYPE_CODE_DOMESTIC = "DM";
    public static final String PURPOSE_TYPE_CODE_STOCK = "ST";
    public static final String PURPOSE_TYPE_CODE_LAWN_GARDEN = "LG";

    public static final String CLIMATIC_AREAS_TABLE = "WRD_CLIMATIC_AREAS";
    public static final String CLIMATIC_AREA_DESCRIPTION = "DESCR";

    public static final String PURPOSE_IRRIGATION_XREF_TABLE = "WRD_PURP_IRR_XREFS";
    public static final String IRRIGATION_TYPES_TABLE = "WRD_IRRIGATION_TYPES";
    public static final String IRRIGATION_TYPE_CODE = "IRTP_CD";
    public static final String IRRIGATION_TYPE_DESCRIPTION = "DESCR";
    public static final String IRRIGATION_HISTORICAL = "HISTORICAL";

    public static final String PLACE_OF_USES_TABLE = "WRD_PLACE_OF_USES";
    public static final String PLACE_OF_USE_ID = "PUSE_ID_SEQ";
    public static final String PLACE_OF_USE_ACREAGE = "ACREAGE";

    public static final String RETIRED_PLACE_OF_USE_TABLE = "WRD_PLACE_OF_USE_RETIRED";
    public static final String RETIRED_PLACE_OF_USE_ID = "PUSR_ID_SEQ";

    public static final String PERIOD_OF_USES_TABLE = "WRD_PERIOD_OF_USES";
    public static final String PERIOD_OF_USE_SEQUENCE = "WRD_POUS_SEQ";
    public static final String PERIOD_OF_USE_ID = "POUS_ID_SEQ";
    public static final String PERIOD_OF_USE_BEGIN_DATE = "BGN_DT";
    public static final String PERIOD_OF_USE_END_DATE = "END_DT";
    public static final String PERIOD_OF_USE_FLOW_RATE = "FLOW_RATE";

    public static final String PERIOD_OF_USE_LEASE_YEAR = "LEASE_YR";
    public static final String PERIOD_OF_USE_ORIGIN_DOMAIN = "PERIOD_ORIGINS";

    public static final String COMMON_FUNCTIONS = "WRD_COMMON_FUNCTIONS";

    public static final String CARDINAL_DIRECTION = "C_DIRECTION";

    public static final String APPLICATION_VERSION_XREF_TABLE = "WRD_VERSION_APPLICATION_XREFS";
    public static final String VERSION_TABLE = "WRD_VERSIONS";
    public static final String VERSION_ID = "VERS_ID_SEQ";
    public static final String VERSION_WATER_RIGHT_ID = "WRGT_ID_SEQ";
    public static final String VERSION_TYPE_CODE = "VER_TYP";
    public static final String VERSION_TYPE_DOMAIN = "VERSION TYPE";
    public static final String VERSION_SCANNED = "VERS_SCANNED";
    public static final String VERSION_OPER_AUTHORITY = "OPER_AUTHORITY";
    public static final String VERSION_ENFORCEABLE_PRIORITY_DATE = "ENF_PRTY_DT";
    public static final String VERSION_PRIORITY_DATE = "PRTY_DT";
    public static final String VERSION_PRIORITY_DATE_ORIGIN = "PRTY_DT_ELEM_ORGN";
    public static final String VERSION_STANDARDS_APPLIED = "STANDARDS_APLD";
    public static final String VERSION_ADJUDICATION_PROCESS = "ADJ_PROCESS";
    public static final String VERSION_CHANGE_AUTHORIZATION_FLOW_RATE = "CHAU_HIST_FLOW_RATE";
    public static final String VERSION_CHANGE_AUTHORIZATION_FLOW_UNIT = "CHAU_HIST_FLOW_UNIT";
    public static final String VERSION_CHANGE_AUTHORIZATION_DIVERTED_VOLUME = "CHAU_HIST_DIVERTED_VOL";
    public static final String VERSION_CHANGE_AUTHORIZATION_CONSUMPTIVE_VOLUME = "CHAU_HIST_CONSUMPTIVE_VOL";
    public static final String VERSION_DATE_RECEIVED = "DT_RECEIVED";
    public static final String VERSION_LATE_DESIGNATION = "LTE_DSGN";
    public static final String VERSION_FEE_RECEIVED = "FEE_RECEIVED";
    public static final String VERSION_IMPLIED_CLAIM = "IMPLIED_CLM";
    public static final String VERSION_EXEMPT_CLAIM = "EXEMPT_CLM";
    public static final String VERSION_CASE_NUMBER = "HISTORICAL_DOCM_NO";
    public static final String VERSION_FILING_DATE = "HISTORICAL_DOCM_DT_FILE";
    public static final String VERSION_RIGHT_TYPE = "HISTORICAL_RGT_TYP";
    public static final String VERSION_RIGHT_TYPE_ORIGIN = "HISTORICAL_RGT_TYP_ELEM_ORGN";
    public static final String VERSION_DECREE_APPROPRIATOR = "HISTORICAL_APPROPRIATOR";
    public static final String VERSION_SOURCE = "HISTORICAL_SOURCE";
    public static final String VERSION_DECREE_MONTH = "HISTORICAL_DECREED_MONTH";
    public static final String VERSION_DECREE_DAY = "HISTORICAL_DECREED_DAY";
    public static final String VERSION_DECREE_YEAR = "HISTORICAL_DECREED_YEAR";
    public static final String VERSION_MINERS_INCHES = "HISTORICAL_MINERS_INCHES";
    public static final String VERSION_FLOW_DESCRIPTION = "HISTORICAL_FLOW_DESCRIPTION";

    public static final String ELEMENT_ORIGIN_DOMAIN = "ELEMENT ORIGIN";
    public static final String HISTORICAL_RIGHT_TYPE_DOMAIN = "HISTORICAL RIGHT TYPE";
    public static final String ADJUDICATION_PROCESS_DOMAIN = "ADJ PROCESS";

    public static final String MAXIMUM_FLOW_RATE = "MAX_FLW_RT";
    public static final String MAXIMUM_VOLUME = "MAX_VOL";
    public static final String MAXIMUM_ACRES = "MAX_ACRES";
    public static final String FLOW_RATE_UNIT = "FLW_RT_UNT";
    public static final String FLOW_RATE_UNIT_DOMAIN = "FLOW_RATE_UNITS";
    public static final String FLOW_RATE_FULL_UNIT_DOMAIN = "FLOW RATE UNIT";
    public static final String ELECTRONIC_CONTACT = "ELECTRONIC CONTACT";

    public static final String STANDARD_PROCEDURES = "WRD_STANDARDS";
    public static final String VERSION_FLOW_RATE_ORIGIN = "MAX_FLW_RT_ELEM_ORGN";
    public static final String ORIGIN_DOMAIN = "ELEMENT ORIGIN";
    public static final String VERSION_VOLUME_ORIGIN = "MAX_VOL_ELEM_ORGN";
    public static final String VERSION_VOLUME_DESCRIPTION = "MAX_VOL_DESCR";
    public static final String VERSION_ACRES_ORIGIN = "MAX_ACRES_ELEM_ORGN";
    public static final String VERSION_FLOW_RATE_DESCRIPTION = "MAX_FLW_RT_DESCR";
    public static final String VERSION_FLOW_RATE_DESCRIPTION_DOMAIN = "FLOW_RATE_DESCRIPTION_SNIPPET";

    public static final String CUSTOMER_TYPES_TABLE = "WRD_CUSTOMER_TYPES";
    public static final String CUSTOMER_TYPE_CODE = "CTTP_CD";
    public static final String CUSTOMER_TYPE_DESCR = "DESCR";

    public static final String ELECTRONIC_CONTACTS_TABLE = "WRD_ELECTRONIC_CONTACTS";
    public static final String ELECTRONIC_ID = "ECON_ID_SEQ";
    public static final String ELECTRONIC_SEQUENCE = "WRD_ECON_SEQ";
    public static final String ELECTRONIC_VAL = "VAL";
    public static final String ELECTRONIC_TYPE = "ECON_TYP";
    public static final String ELECTRONIC_NOTES = "NOTES";

    public static final String CUSTOMER_STATUS_DECEASED = "***DECEASED***";

    public static final String STATUS_TYPE_XREF_TABLE = "WRD_STAT_TYP_XREFS";

    public static final String CREATED_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAYED_DATE_FORMAT = "MM/dd/yyyy HH:mm";
    public static final String CUSTOMER_STATUS_DOMAIN = "SEND MAIL";
    public static final String CUSTOMER_SUFFIX_DOMAIN = "SUFFIX";

    public static final String NOT_THE_SAMES_TABLE = "WRD_NOT_THE_SAMES";
    public static final String NOT_THE_SAME_XREF_TABLE = "WRD_CUST_NOT_THE_SAME_XREFS";
    public static final String NOT_THE_SAME_ID = "NTSM_ID_SEQ";

    public static final String CUSTM_OWNERSHIP_UPDT_XREFS_TABLE = "WRD_CUSTM_OWNERSHIP_UPDT_XREFS";
    public static final String OWNERSHIP_UPDATE_CUSTOMERS_SEQUENCE = "WRD_COUX_SEQ";
    public static final String OWNR_UPDT_ID = "OWNR_UPDT_ID";
    public static final String ROLE = "ROLE";
    public static final String SELLER_ROLE = "SEL";
    public static final String BUYER_ROLE = "BUY";
    public static final String CUST_XREF_END_DT = "END_DT";
    public static final String STR_DT = "STR_DT";
    public static final String CHAIN_OF_TTL = "CHAIN_OF_TTL";
    public static final String CONTT_FOR_DEED = "CONTT_FOR_DEED";
    public static final String SEND_MAIL = "SEND_MAIL";
    public static final String CUST_XREF_ID_SEQ = "CUST_ID_SEQ";
    public static final String CUST_XREF_CREATED_BY = "CREATED_BY";
    public static final String CUST_XREF_DTM_CREATED = "DTM_CREATED";
    public static final String CUST_XREF_MOD_BY = "MOD_BY";
    public static final String CUST_XREF_DTM_MOD = "DTM_MOD";
    public static final String COUX_ID_SEQ = "COUX_ID_SEQ";
    public static final String DOR_CUST_TYPE = "DOR_CUST_TYPE";

    public static final String WRD_OWNERSHIP_UPDATES_TABLE = "WRD_OWNERSHIP_UPDATES";
    public static final String OWNERSHIP_UPDATE_SEQUENCE = "WRD_OWUP_SEQ";
    public static final String TRN_TYP = "TRN_TYP";
    public static final String OWNERSHIP_UPDATE_DT_RECEIVED = "DT_RECEIVED";
    public static final String OWNERSHIP_UPDATE_DT_PROCESSED = "DT_PROCESSED";
    public static final String OWNERSHIP_UPDATE_DT_TERMINATED = "DT_TERMINATED";
    public static final String OWNERSHIP_UPDATE_CREATED_BY = "CREATED_BY";
    public static final String OWNERSHIP_UPDATE_DTM_CREATED = "DTM_CREATED";
    public static final String OWNERSHIP_UPDATE_MOD_BY = "MOD_BY";
    public static final String OWNERSHIP_UPDATE_DTM_MOD = "DTM_MOD";
    public static final String OWNERSHIP_UPDATE_FEE_DUE = "FEE_DUE";
    public static final String OWNERSHIP_UPDATE_FEE_STATUS = "FEE_STATUS";
    public static final String OWNERSHIP_UPDATE_FEE_DUE_LTR_SENT_DT = "FEE_DUE_LTR_SENT_DT";
    public static final String OWNERSHIP_UPDATE_FEE_LETTER_WR = "FEE_LETTER_WR";
    public static final String OWNERSHIP_UPDATE_NOTES = "NOTES";
    public static final String OWNERSHIP_UPDATE_PENDING_DOR = "PENDING_DOR";
    public static final String RECEIVED_AS_608 = "RECEIVED_AS_608";
    public static final String OFFC_ID_SEQ = "OFFC_ID_SEQ";
    public static final String OFFC_ID_SEQ_PROCESSOR = "OFFC_ID_SEQ_PROCESSOR";
    public static final String DNRC_ID_PROCESSOR = "DNRC_ID_PROCESSOR";
    public static final String OWNERSHIP_TRANSFER_DOMAIN = "OWNERSHIP TRANSFER";
    public static final String DOR_608_TRANSACTION_TYPE = "DOR 608";
    public static final String STANDARD_608_TRANSACTION_TYPE = "608";
    public static final String CD_608_TRANSACTION_TYPE = "CD 608";
    public static final String ADM_TRANSACTION_TYPE = "ADM";
    public static final String DI_641_608_TRANSACTION_TYPE = "641 608";
    public static final String DI_641_COR_TRANSACTION_TYPE = "641 COR";
    public static final String DI_641_OTH_TRANSACTION_TYPE = "641 OTH";
    public static final String EWR_642_608_TRANSACTION_TYPE = "642 608";
    public static final String EWR_642_COR_TRANSACTION_TYPE = "642 COR";
    public static final String EWR_642_OTH_TRANSACTION_TYPE = "642 OTH";
    public static final String SWR_643_608_TRANSACTION_TYPE = "643 608";
    public static final String SWR_643_COR_TRANSACTION_TYPE = "643 COR";
    public static final String EXEMPT_FILING_TRANSACTION_TYPE = "EXFL";
    public static final String IMPLIED_CLAIM_TRANSACTION_TYPE = "IMP";
    public static final String PENDING_CORRECTION_TRANSACTION_TYPE = "PND COR";
    public static final String PENDING_608_TRANSACTION_TYPE = "PND 608";
    public static final String VER_TRANSACTION_TYPE = "VER";
    public static final String WTC_608_TRANSACTION_TYPE = "WTC 608";

    public static final String DNRC_START_FEES_CHARGE_DATE = "09-30-2017";
    public static final String DNRC_FEE_STATUS_FULL = "FULL";

    public static final String WRD_WTR_RGT_OWNSHIP_UPDT_XREFS_TABLE = "WRD_WTR_RGT_OWNSHIP_UPDT_XREFS";
    public static final String WTR_XREF_CREATED_BY = "CREATED_BY";
    public static final String WTR_XREF_DTM_CREATED = "DTM_CREATED";
    public static final String WTR_XREF_MOD_BY = "MOD_BY";
    public static final String WTR_XREF_DTM_MOD = "DTM_MOD";
    public static final String WTR_XREF_SELLER_ROLE = "SEL";
    public static final String WTR_XREF_BUYER_ROLE = "BUY";

    public static final String WRD_APPL_OWNSHIP_UPDT_XREFS_TABLE = "WRD_APPL_OWNSHIP_UPDT_XREFS";
    public static final String APPL_XREF_CREATED_BY = "CREATED_BY";
    public static final String APPL_XREF_DTM_CREATED = "DTM_CREATED";
    public static final String APPL_XREF_MOD_BY = "MOD_BY";
    public static final String APPL_XREF_DTM_MOD = "DTM_MOD";

    public static final String OWNERSHIP_UPDATE_OFFICE_XREFS_TABLE = "WRD_OWNERSHIP_OFFICE_XREFS";
    public static final String OWNERSHIP_UPDATE_OFFICE_XREF_ID = "OOXR_ID_SEQ";
    public static final String OWNERSHIP_UPDATE_OFFICE_XREF_SEQUENCE = "WRD_OOXR_SEQ";
    public static final String OWNERSHIP_UPDATE_OFFICE_ID = "OFFC_ID_SEQ";
    public static final String OWNERSHIP_UPDATE_OFFICE_RECEIVED_DATE = "BGN_DT";
    public static final String OWNERSHIP_UPDATE_OFFICE_SENT_DATE = "END_DT";
    public static final String OWNERSHIP_UPDATE_OFFICE_CREATED_DATE = "DTM_CREATED";

    public static final String OWNERSHIP_UPDATE_STAFF_XREFS_TABLE = "WRD_OWNERSHIP_STAFF_XREFS";
    public static final String OWNERSHIP_UPDATE_STAFF_XREF_ID = "OSXR_ID_SEQ";
    public static final String OWNERSHIP_UPDATE_STAFF_XREF_SEQUENCE = "WRD_OSXR_SEQ";
    public static final String OWNERSHIP_UPDATE_STAFF_ID = "DNRC_ID";
    public static final String OWNERSHIP_UPDATE_STAFF_BEGIN_DATE = "BGN_DT";
    public static final String OWNERSHIP_UPDATE_STAFF_END_DATE = "END_DT";
    public static final String OWNERSHIP_UPDATE_STAFF_CREATED_DATE = "DTM_CREATED";

    public static final String WATER_RIGHT_GEOCODE_XREF_TABLE = "WRD_GEOCODE_WATER_RIGHT_XREFS";
    public static final String WATER_RIGHT_GEOCODE_XREF_ID = "GWRX_ID_SEQ";
    public static final String GEOCODE_ID = "GOCD_ID_SEQ";
    public static final String GEOCODE_BEGIN_DATE = "BGN_DT";
    public static final String GEOCODE_END_DATE = "END_DT";
    public static final String GEOCODE_VALID = "VALID";
    public static final String GEOCODE_COMMENTS = "COMMENTS";
    public static final String GEOCODE_UNRESOLVED = "UNRESOLVED_FLAG";
    public static final String GEOCODE_SEVER = "SEVER_SELL_FLAG";
    public static final String WATER_RIGHT_GEOCODE_SEQUENCE = "WRD_GWRX_SEQ";

    public static final String GEOCODE_TABLE = "WRD_GEOCODES";
    public static final String GEOCODE_STATUS = "GOCD_STATUS";

    public static final String WATER_RIGHT_OFFICE_TABLE = "WRD_OFFICE_WATER_RIGHT_XREFS";
    public static final String WATER_RIGHT_OFFICE_ID = "OWRX_ID_SEQ";
    public static final String WATER_RIGHT_OFFICE_SEQUENCE = "WRD_OWRX_SEQ";
    public static final String WATER_RIGHT_OFFICE_RECEIVED_DATE = "BGN_DT";
    public static final String WATER_RIGHT_OFFICE_SENT_DATE = "END_DT";
    public static final String WATER_RIGHT_OFFICE_CREATED_DATE = "DTM_CREATED";

    public static final String WATER_RIGHT_STAFF_TABLE = "WRD_STAFF_WATER_RIGHT_XREFS";
    public static final String WATER_RIGHT_STAFF_ID = "SWRX_ID_SEQ";
    public static final String WATER_RIGHT_STAFF_SEQUENCE = "WRD_SWRX_SEQ";
    public static final String WATER_RIGHT_STAFF_BEGIN_DATE = "BGN_DT";
    public static final String WATER_RIGHT_STAFF_END_DATE = "END_DT";
    public static final String WATER_RIGHT_STAFF_CREATED_DATE = "DTM_CREATED";

    public static final String DECREE_VERSION_TABLE = "WRD_DECREE_VERSION_XREFS";
    public static final String DECREE_VERSION_MISSED_IN_DECREE = "MISSED_IN_DECREE";

    public static final String DECREE_TABLE = "WRD_DECREES";
    public static final String DECREE_ID = "DECR_ID_SEQ";
    public static final String DECREE_MODIFY_ROLE = "WRD_MODIFY_DECREED_RIGHT";
    public static final String POST_DECREE_MODIFY_VERSION_ROLE = "WRD_MODIFY_POST_REXM_VERSION";
    public static final String SPLIT_DECREE_MODIFY_VERSION_ROLE = "WRD_MODIFY_SPPD_VERSION_ROLE";
    public static final String PRINT_DECREE_REPORT = "WRD_PRINT_DECREE_REPORT";
    public static final String PRINT_ALL_WC_REPORTS = "WRD_WC_PRINT_ALL_REPORTS_ROLE";

    public static final String ELEMENT_TYPE_TABLE = "WRD_ELEMENT_TYPES";
    public static final String ELEMENT_TYPE_CODE = "ETYP_CD";
    public static final String ELEMENT_TYPE_DESCR = "DESCR";

    public static final String SHARED_ELEMENT_TABLE = "WRD_RELATED_RIGTS_SHARD_ELEMTS";
    public static final String SHARED_ELEMENT_RELATED_RIGHT_ID = "RLRT_ID_SEQ";
    public static final String SHARED_ELEMENT_CREATED_BY = "CREATED_BY";
    public static final String SHARED_ELEMENT_CREATED_DATE = "DTM_CREATED";
    public static final String SHARED_ELEMENT_MODIFIED_BY = "MOD_BY";
    public static final String SHARED_ELEMENT_MODIFIED_DATE = "DTM_MOD";

    public static final String POINT_OF_DIVERSION_TABLE = "WRD_POINT_OF_DIVERSIONS";
    public static final String POINT_OF_DIVERSION_ID = "PODV_ID_SEQ";
    public static final String POINT_OF_DIVERSION_SEQUENCE = "WRD_PODV_SEQ";
    public static final String POINT_OF_DIVERSION_NUMBER = "PODV_NO";
    public static final String POINT_OF_DIVERSION_COUNTY_ID = "COUT_ID_SEQ_IN";
    public static final String MAJOR_TYPE = "MAJOR_TYPE";
    public static final String MAJOR_TYPE_DOMAIN = "MAJOR SOURCE TYPE";
    public static final String POINT_OF_DIVERSION_ORIGIN_CODE = "ELEM_ORGN";
    public static final String POINT_OF_DIVERSION_ORIGIN_DOMAIN = "DIVERSION_ORIGINS";
    public static final String POINT_OF_DIVERSION_TYPE_CODE = "POD_TYP";
    public static final String POINT_OF_DIVERSION_TYPE_DOMAIN = "POD TYPE";
    public static final String POINT_OF_DIVERSION_MODIFIED = "MOD_IN_THIS_VER";
    public static final String POINT_OF_DIVERSION_X_COORDINATE = "MT_STATE_PLANE_X";
    public static final String POINT_OF_DIVERSION_Y_COORDINATE = "MT_STATE_PLANE_Y";
    public static final String POINT_OF_DIVERSION_SOURCE_ORIGIN_CODE = "SRC_ELEM_ORGN";
    public static final String POINT_OF_DIVERSION_SOURCE_ORIGIN_DOMAIN = "DIVERSION_ORIGINS";
    public static final String POINT_OF_DIVERSION_UNNAMED = "UNNAMED_TRIBUTARY";
    public static final String POINT_OF_DIVERSION_LOT = "LOT";
    public static final String POINT_OF_DIVERSION_BLOCK = "BLK";
    public static final String POINT_OF_DIVERSION_TRACT = "TRACT";
    public static final String POINT_OF_DIVERSION_WELL_DEPTH = "WELL_DPTH";
    public static final String POINT_OF_DIVERSION_WATER_LEVEL = "STIC_WTR_LVL";
    public static final String POINT_OF_DIVERSION_CASTING_DIAMETER = "CSNG_DIAM";
    public static final String POINT_OF_DIVERSION_FLOWING = "FLOWING";
    public static final String POINT_OF_DIVERSION_PUMP_SIZE = "PUMP_SZ";
    public static final String POINT_OF_DIVERSION_WATER_TEMP = "WTR_TEMPATURE";
    public static final String POINT_OF_DIVERSION_TEST_RATE = "YLD_RT";
    public static final String POINT_OF_DIVERSION_TRANSITORY = "TRANSITORY";
    public static final String POINT_OF_DIVERSION_MODIFIED_ELEM_ORGN = "MOD_ELEM_ORGN";
    public static final String POINT_OF_DIVERSION_PERCENTAGE_OF_REACH = "PERCENTAGE_OF_REACH";
    public static final String POINT_OF_DIVERSION_PODV_ID_AFT = "PODV_ID_SEQ_AFT";
    public static final String POINT_OF_DIVERSION_PRCS_STS = "PRCS_STS";
    public static final String POINT_OF_DIVERSION_PRE_MJR = "PRE_MJR";
    public static final String POINT_OF_DIVERSION_PRE_MRTP = "PRE_MRTP";
    public static final String POINT_OF_DIVERSION_PRE_SOURCE_ID = "PRE_SOUR_ID_SEQ";
    public static final String POINT_OF_DIVERSION_PRE_UT = "PRE_UT";
    public static final String POINT_OF_DIVERSION_REAC_ID_SEQ = "REAC_ID_SEQ";
    public static final String POINT_OF_DIVERSION_SVTP_ID_SEQ = "SVTP_ID_SEQ";
    public static final String POINT_OF_DIVERSION_WRKEY = "WRKEY";

    public static final String DITCH_TABLE = "WRD_DITCHES";
    public static final String DITCH_ID = "DTCH_ID_SEQ";
    public static final String DITCH_SEQUENCE = "WRD_DTCH_SEQ";
    public static final String DITCH_NAME = "DTCH_NM";
    public static final String DITCH_CAPACITY = "CAPACITY";
    public static final String DITCH_DEPTH = "DEPTH";
    public static final String DITCH_WIDTH = "WIDTH";
    public static final String DITCH_LENGTH = "LENGTH";
    public static final String DITCH_SLOPE = "SLOPE";
    public static final String DITCH_VALID = "VLD";

    public static final String DIVERSION_TYPE_TABLE = "WRD_DIVERSION_TYPES";
    public static final String DIVERSION_TYPE_CODE = "DIVE_CD";
    public static final String DIVERSION_TYPE_DESCRIPTION = "DESCR";

    public static final String MEANS_OF_DIVERSION_TABLE = "WRD_MEANS_OF_DIVERSIONS";
    public static final String MEANS_OF_DIVERSION_CODE = "MODV_CD";
    public static final String MEANS_OF_DIVERSION_DESCRIPTION = "DESCR";

    public static final String MINOR_TYPE_TABLE = "WRD_MINOR_TYPES";
    public static final String MINOR_TYPE_CODE = "MRTP_CD";
    public static final String MINOR_TYPE_DESCRIPTION = "DESCR";

    public static final String POINT_OF_DIVERSION_ENFORCEMENT_XREF_TABLE = "WRD_ENF_POD_XREFS";
    public static final String POINT_OF_DIVERSION_ENFORCEMENT_NUMBER = "ENF_NUMBER";
    public static final String POINT_OF_DIVERSION_ENFORCEMENT_COMMENTS = "ENF_COMMENT";

    public static final String ENFORCEMENT_AREA_TABLE = "WRD_ENFORCEMENT_AREAS";
    public static final String ENFORCEMENT_AREA_ID = "ENF_AREA_ID";
    public static final String ENFORCEMENT_AREA_NAME = "ENF_AREA_NAME";

    public static final String SOURCE_TABLE = "WRD_SOURCES";
    public static final String SOURCE_ID = "SOUR_ID_SEQ";
    public static final String SOURCE_SEQUENCE = "WRD_SOUR_SEQ";
    public static final String SOURCE_FORK_NAME = "FORK_NM";

    public static final String SOURCE_NAME_TABLE = "WRD_SOURCE_NAMES";
    public static final String SOURCE_NAME_ID = "SRNM_ID_SEQ";
    public static final String SOURCE_NAME_SEQUENCE = "WRD_SRNM_SEQ";
    public static final String SOURCE_NAME = "SRNM_NM";

    public static final String ALSO_KNOWN_TABLE = "WRD_ALSO_KNOWN_ASES";
    public static final String ALSO_KNOWN_ID = "AKNA_ID_SEQ";
    public static final String ALSO_KNOWN_SEQUENCE = "WRD_AKNA_SEQ";
    public static final String ALSO_KNOWN_NAME = "NM";

    public static final String SYSTEM_VARIABLE_TABLE = "WRD_SYS_VARIABLES";
    public static final String SYSTEM_VARIABLE_ID = "VAR_ID_SEQ";
    public static final String SYSTEM_VARIABLE_NAME = "VAR_NM";
    public static final String SYSTEM_VARIABLE_VALUE = "VALUE";
    public static final String SYSTEM_ENFORCEMENT_EMAILS = "ENFORCEMENT_NOTIFICATION";

    public static final String RELATED_RIGHT_TABLE = "WRD_RELATED_RIGHTS";
    public static final String RELATED_RIGHT_TYPE_DOMAIN = "RELATED RIGHT";
    public static final String RELATED_RIGHT_TYPE_MULTIPLE = "MULT";

    public static final String RLRT_ID_SEQ = "RLRT_ID_SEQ";
    public static final String RLT_TYP = "RLT_TYP";
    public static final String MAX_FLW_RT = "MAX_FLW_RT";
    public static final String FLW_RT_UNT = "FLW_RT_UNT";
    public static final String MAX_ACRES = "MAX_ACRES";
    public static final String MAX_VOLUME = "MAX_VOLUME";
    public static final String WRD_RELATED_RIGHT_VERS_XREFS = "WRD_RELATED_RIGHT_VERS_XREFS";

    public static final String COMPACT_TABLE = "WRD_COMPACTS";
    public static final String COMPACT_NAME = "COMP_NM";
    public static final String COMPACT_ID = "COMP_ID_SEQ";

    public static final String SUBCOMPACT_TABLE = "WRD_SUBCOMPACTS";
    public static final String SUBCOMPACT_NAME = "SBCP_NM";
    public static final String SUBCOMPACT_ID = "SBCP_ID_SEQ";

    public static final String RELATED_RIGHT_ID = "RLRT_ID_SEQ";
    public static final String RELATED_RIGHT_SEQUENCE = "WRD_RLRT_SEQ";
    public static final String RELATED_RIGHT_TYPE = "RLT_TYP";
    public static final String RELATED_RIGHT_CREATED_BY = "CREATED_BY";
    public static final String RELATED_RIGHT_CREATED_DATE = "DTM_CREATED";
    public static final String RELATED_RIGHT_MODIFIED_BY = "MOD_BY";
    public static final String RELATED_RIGHT_MODIFIED_DATE = "DTM_MOD";
    public static final String RELATED_RIGHT_MAX_FLOW_RATE = "MAX_FLW_RT";
    public static final String RELATED_RIGHT_FLOW_RATE_UNIT = "FLW_RT_UNT";
    public static final String RELATED_RIGHT_MAX_ACRES = "MAX_ACRES";
    public static final String RELATED_RIGHT_MAX_VOLUME = "MAX_VOLUME";

    public static final String RELATED_RIGHT_VERSIONS_XREFS_TABLE = "WRD_RELATED_RIGHT_VERS_XREFS";


    public static final String RESERVOIR_TABLE = "WRD_RESERVOIRS";
    public static final String RESERVOIR_SEQUENCE = "WRD_RESV_SEQ";
    public static final String RESERVOIR_ID = "RESV_ID_SEQ";
    public static final String RESERVOIR_NAME = "NM";
    public static final String RESERVOIR_TYPE = "RESERVOIR_TYP";
    public static final String RESERVOIR_TYPE_DOMAIN = "RESERVOIR TYPE";
    public static final String RESERVOIR_CURRENT_CAPACITY = "INT_CAP";
    public static final String RESERVOIR_ENLARGED_CAPACITY = "CAP";
    public static final String RESERVOIR_DEPTH = "MAX_DPTH";
    public static final String RESERVOIR_HEIGHT = "DAM_HT";
    public static final String RESERVOIR_SURFACE_AREA = "SRF_AREA";
    public static final String RESERVOIR_ELEVATION = "ELV";
    public static final String RESERVOIR_ORIGIN = "ELEM_ORGN";
    public static final String RESERVOIR_ORIGIN_DOMAIN = "RESERVOIR_ORIGIN";
    public static final String RESERVOIR_CHANGED = "MOD_IN_THIS_VER";

    public static final String LEGAL_LAND_DESCRIPTION_TABLE = "WRD_LEGAL_LAND_DESCRIPTIONS";
    public static final String LEGAL_LAND_DESCRIPTION_ID = "LLDS_ID_SEQ";
    public static final String LEGAL_LAND_DESCRIPTION_GOVERNMENT_LOT = "GOVT_LOT";
    public static final String LEGAL_LAND_DESCRIPTION_320 = "LLDS_320_160";
    public static final String LEGAL_LAND_DESCRIPTION_160 = "LLDS_160_40";
    public static final String LEGAL_LAND_DESCRIPTION_80 = "LLDS_80_10";
    public static final String LEGAL_LAND_DESCRIPTION_40 = "LLDS_40_2_5";
    public static final String ALIQUOT_PARTS_DOMAIN = "ALIQUOT PARTS";
    public static final String RANGE_DIRECTION_DOMAIN = "RANGE TYPE";
    public static final String TOWNSHIP_DIRECTION_DOMAIN = "TOWNSHIP TYPE";

    public static final String TRS_LOCATION_TABLE = "WRD_TRSES";
    public static final String TRS_LOCATION_ID = "TRSS_ID_SEQ";
    public static final String TRS_LOCATION_SECTION = "SCTN";
    public static final String TRS_LOCATION_TOWNSHIP = "TNSP";
    public static final String TRS_LOCATION_RANGE = "TRSS_RNG";
    public static final String TRS_LOCATION_TOWNSHIP_DIRECTION = "TNSP_DIR";
    public static final String TRS_LOCATION_RANGE_DIRECTION = "RNG_DIR";

    public static final String REMARK_CODES_TABLE = "WRD_FORMATTED_REMARK_LIBRARIES";
    public static final String REMARK_CODE = "FRLB_CD";
    public static final String REMARK_CATEGORY_CODE = "RMCG_CD";
    public static final String REMARK_CODE_STATUS = "STS";
    public static final String REMARK_TYPE = "REMARK_TYPE";

    public static final String REMARK_CATEGORIES_TABLE = "WRD_REMARK_CATEGORIES";
    public static final String CATEGORY_DESCRIPTION = "DESCR";
    public static final String REMARK_STATUS_DOMAIN = "FORMATTED REMARK STATUS";
    public static final String REMARK_TYPE_DOMAIN = "REMARK TYPE";

    public static final String REMARK_WATER_RIGHT_TYPE_XREF_TABLE = "WRD_FRMT_RMK_WTR_RGT_TYP_XREFS";
    public static final String REMARK_LEGACY_CD = "LEGACY_CD";
    public static final String REMARK_PROGRAM = "PROGRAM";

    public static final String VERSION_REMARKS_TABLE = "WRD_FORMATTED_REMK_VERSN_XREFS";
    public static final String REMARKS_ID = "FRVX_ID_SEQ";
    public static final String VERSION_REMARKS_SEQUENCE = "WRD_FRVX_SEQ";
    public static final String VERSION_REMARK_CODE = "FRLB_CD";
    public static final String VERSION_REMARK_TYPE_CODE = "RPTP_CD";
    public static final String VERSION_REMARK_DATE = "RMK_DT";
    public static final String VERSION_REMARK_END_DATE = "END_DT";
    public static final String VERSION_REMARK_TYPE_INDICATOR = "TYP_IND";

    public static final String REMARK_TYPE_TABLE = "WRD_REPORT_TYPES";
    public static final String REMARK_TYPE_CODE = "RPTP_CD";

    public static final String REMARK_ELEMENTS_TABLE = "WRD_DATA_ELEMENTS";
    public static final String REMARK_ELEMENTS_ID = "DTEM_ID_SEQ";
    public static final String REMARK_ELEMENTS_SEQUENCE = "WRD_DTEM_SEQ";
    public static final String REMARK_ELEMENTS_VALUE = "VAL";

    public static final String REMARK_VARIABLE_TABLE = "WRD_VARIABLES";
    public static final String REMARK_VARIABLE_ID = "VARB_ID_SEQ";
    public static final String REMARK_VARIABLE_NUMBER = "VARB_SEQ";
    public static final String REMARK_VARIABLE_PRECEDING_TEXT = "PRECEDING_STIC";
    public static final String REMARK_VARIABLE_TRAILING_TEXT = "TRAILING_STIC";
    public static final String REMARK_VARIABLE_TYPE = "VARB_DATA_TYP";
    public static final String REMARK_VARIABLE_LENGTH = "VARB_LNGTH";
    public static final String REMARK_VARIABLE_TABLE_NAME = "TBL_NM";
    public static final String REMARK_VARIABLE_COLUMN_NAME = "COLUMN_NM";

    public static final String VERSION_REMARK_MEASUREMENT_TABLE = "WRD_CONDITION_REPORTINGS";
    public static final String VERSION_REMARK_MEASUREMENT_ID = "CNRP_ID_SEQ";
    public static final String VERSION_REMARK_MEASUREMENT_SEQUENCE = "WRD_CNRP_SEQ";
    public static final String VERSION_REMARK_MEASUREMENT_YEAR = "DT_OF_MEAS";
    public static final String VERSION_REMARK_MEASUREMENT_AMOUNT = "AMT";
    public static final String VERSION_REMARK_MEASUREMENT_UNIT = "MEAS_UNT";
    public static final String VERSION_REMARK_MEASUREMENT_VOLUME = "MAX_VOL";

    public static final String REPORT_TYPE_TABLE = "WRD_REPORT_TYPES";
    public static final String REPORT_TYPE_CODE = "RPTP_CD";
    public static final String REPORT_TYPE_DESCRIPTION = "DESCR";

    public static final String CLOSURE_ID = "CLSU_ID_SEQ";

    public static final String LEASE_YEAR_VALUE_DOMAIN = "LEASE YEAR VALUE";

    public static final String VERSION_COMPACT_TABLE = "WRD_AFFECTED_WATER_RIGHTS";
    public static final String VERSION_COMPACT_ID = "AFWR_ID_SEQ";
    public static final String VERSION_COMPACT_SEQUENCE = "WRD_AFWR_SEQ";
    public static final String VERSION_COMPACT_EXEMPT = "EXMPT_IND";
    public static final String VERSION_COMPACT_AFFECTS = "AFFCTS_ALLOC_IND";
    public static final String VERSION_COMPACT_TRANSBASIN = "TRANSBASIN_IND";

    public static final String WRD_POU_RET_SUBDIVISION_XREFS_TABLE = "WRD_POU_RET_SUBDIVISION_XREFS";
    public static final String WRD_POU_RET_SUB_BLK = "BLK";
    public static final String WRD_POU_RET_SUB_LOT = "LOT";

    public static final String EXAMINATIONS_TABLE = "WRD_EXAMINATIONS";
    public static final String EXAMINATION_SEQUENCE = "WRD_EXAM_SEQ";
    public static final String EXAMINATION_ID = "EXAM_ID_SEQ";
    public static final String EXAMINATION_BEGIN_DATE = "BGN_DT";
    public static final String EXAMINATION_END_DATE = "END_DT";
    public static final String EXAMINATION_EXAMINER = "DNRC_ID_EXAMINER";
    public static final String EXAMINATION_ERROR_CHECK_FLAG = "ERROR_CHECK_FLAG";

    public static final String DATA_SOURCE_TYPE = "DATA SOURCE TYPE";

    public static final String POU_EXAMINATIONS_TABLE = "WRD_POU_EXAMINATIONS";
    public static final String POU_EXAMINATION_ID = "PEXM_ID_SEQ";
    public static final String POU_EXAMINATION_SEQUENCE = "WRD_PEXM_SEQ";
    public static final String POU_EXAMINATION_INVESTIGATION_DATE = "FLD_INVSTG_DT";
    public static final String POU_EXAMINATION_DATA_SRC_TYP = "DATA_SRC_TYP";

    public static final String EXAM_USGS_MAP_XREF_TABLE = "WRD_POU_EXAM_USGS_MAP_XREFS";

    public static final String USGS_ID = "UTMP_ID_SEQ";

    public static final String WATER_SURVEY_POU_EXAM_XREF = "WRD_WRS_POU_EXAM_XREFS";

    public static final String FULL_ACCESS_ROLE = "WRD_FULL_ACCESS_ROLE";
    public static final String PROGRAM_MGR_ROLE = "WRD_PROGRAM_MGR_ROLE";
    public static final String DEVELOPER_ROLE = "WRD_DEVELOPER_ROLE";

    public static final String USGS_TABLE = "WRD_USGS_TOPO_MAPS";
    public static final String USGS_SEQUENCE = "WRD_UTMP_SEQ";

    public static final String USGS_NAME = "NM";

    public static final String USGS_DATE = "MAP_DT";
    public static final String WATER_RESOURCE_SURVEYS_TABLE = "WRD_WATER_RESOURCE_SURVEYS";

    public static final String WATER_SURVEY_SEQUENCE = "WRD_WRSY_SEQ";

    public static final String WATER_SURVEY_YR = "YR";

    public static final String CASE_DISTRICT_COURT_TABLE = "WRD_CASE_DISTRICT_COURT";
    public static final String DISTRICT_SEQUENCE = "WRD_CDCT_SEQ";

    public static final String CASE_LODGING_TABLE = "WRD_CASE_LODGING";
    public static final String CASE_LODGING_ID = "LDG_ID_SEQ";
    public static final String CASE_LODGING_SEQUENCE = "WRD_CLDG_SEQ";

    public static final String CASE_VEHICLE_TABLE = "WRD_CASE_VEHICLE";
    public static final String CASE_VEHICLE_ID = "VEH_ID_SEQ";
    public static final String CASE_VEHICLE_SEQUENCE = "WRD_CVEH_SEQ";

    public static final String CASE_SUMMARY_TABLE = "WRD_CASE_SUMMARY";
    public static final String CASE_SUMMARY_ID = "SMRY_ID_SEQ";
    public static final String CASE_SUMMARY_SEQUENCE = "WRD_CSUM_SEQ";

    public static final String DISTRICT_ID = "DIST_ID_SEQ";

    private Constants() {
    }
}
