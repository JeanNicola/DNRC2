package gov.mt.wris.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PurposeVolumeCalculation {

    BigDecimal volume;

    List<String> messages;

}
