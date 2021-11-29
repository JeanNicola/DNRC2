package gov.mt.wris.models;

import gov.mt.wris.constants.Constants;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = Constants.DECREE_TYPE_TABLE)
@Getter
@Setter
public class DecreeType {

    @Id
    @Column(name = Constants.DECREE_TYPE_CODE)
    public String code;

    @Column(name = Constants.DECREE_TYPE_DESCR)
    public String description;

    @OneToMany(mappedBy = "decreeType")
    public List<Decree> decree;
}
