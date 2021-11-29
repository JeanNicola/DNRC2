package gov.mt.wris;

import gov.mt.wris.controllers.ReferencesController;
import gov.mt.wris.dtos.AllReferencesDto;
import gov.mt.wris.dtos.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class ReferencesIntegrationTests  extends BaseTestCase {

    @Autowired
    private MockMvc mockMVC;

    @Autowired
    ReferencesController referencesController;

    @Test
    public void testGetContactStatusReferencesSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/contact-status")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(2);
        assertThat(page.getResults().get(0).getValue().equals("DEC"));
        assertThat(page.getResults().get(0).getDescription().equals("DECEASED"));
        assertThat(page.getResults().get(0).getValue().equals("LOC"));
        assertThat(page.getResults().get(0).getDescription().equals("UNKNOWN LOCATION"));

    }

    @Test
    public void testGetContactTypeReferencesSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/contact-type")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(17);
        assertThat(page.getResults().get(0).getValue().equals("ATTY"));
        assertThat(page.getResults().get(0).getDescription().equals("ATTORNEY"));
        assertThat(page.getResults().get(10).getValue().equals("NEWS"));
        assertThat(page.getResults().get(10).getDescription().equals("NEWSPAPER"));

    }

    @Test
    public void testGetContactSuffixReferencesSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/contact-suffix")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(2);
        assertThat(page.getResults().get(0).getValue().equals("JR"));
        assertThat(page.getResults().get(0).getDescription().equals("JUNIOR"));
        assertThat(page.getResults().get(1).getValue().equals("SR"));
        assertThat(page.getResults().get(1).getDescription().equals("SENIOR"));

    }

    @Test
    public void testGetOwnershipTransfersSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/ownership-transfer")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(20);
        assertThat(page.getResults().get(0).getValue().equals("641 COR"));
        assertThat(page.getResults().get(0).getDescription().equals("641 CORRECTION"));
        assertThat(page.getResults().get(19).getValue().equals("WTC 608"));
        assertThat(page.getResults().get(19).getDescription().equals("WATER COURT ORDER UPDATE"));

    }

    @Test
    public void testGetContractForDeedRleSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/contract-for-deed-rle")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(4);
        assertThat(page.getResults().get(0).getValue()).isEqualTo("N");
        assertThat(page.getResults().get(0).getDescription()).isEqualTo("NO");
        assertThat(page.getResults().get(1).getValue()).isEqualTo("P");
        assertThat(page.getResults().get(1).getDescription()).isEqualTo("PAID");
        assertThat(page.getResults().get(2).getValue()).isEqualTo("R");
        assertThat(page.getResults().get(2).getDescription()).isEqualTo("REMAINING LIFE ESTATE");
        assertThat(page.getResults().get(3).getValue()).isEqualTo("Y");
        assertThat(page.getResults().get(3).getDescription()).isEqualTo("YES");

    }

    @Test
    public void testGetRelatedRightTypesSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/related-right-types")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(4);
        assertThat(page.getResults().get(0).getValue().equals("ASSO"));
        assertThat(page.getResults().get(0).getDescription().equals("ASSOCIATED"));
        assertThat(page.getResults().get(1).getValue().equals("DUPL"));
        assertThat(page.getResults().get(1).getDescription().equals("DUPLICATED RIGHTS"));

    }

    @Test
    public void testGetRelatedElementTypesSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/related-element-types")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(21);
        assertThat(page.getResults().get(0).getValue().equals("ABDN"));
        assertThat(page.getResults().get(0).getDescription().equals("ABANDONMENT"));
        assertThat(page.getResults().get(20).getValue().equals("WR"));
        assertThat(page.getResults().get(20).getDescription().equals("WATER RIGHT"));

    }

    @Test
    public void testGetClimaticAreasSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/climatic-areas")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(5);
        assertThat(page.getResults().get(0).getValue().equals("1"));
        assertThat(page.getResults().get(0).getDescription().equals("1 - HIGH"));
        assertThat(page.getResults().get(4).getValue().equals("5"));
        assertThat(page.getResults().get(4).getDescription().equals("5 - LOW"));

    }

    @Test
    public void testGetPurposeTypesSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/purpose-types")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(51);
        assertThat(page.getResults().get(0).getValue().equals("AS"));
        assertThat(page.getResults().get(0).getDescription().equals("AGRICULTURAL SPRAYING"));
        assertThat(page.getResults().get(50).getValue().equals("WW"));
        assertThat(page.getResults().get(50).getDescription().equals("WILDLIFE/WATERFOWL"));

    }

    @Test
    public void testGetIrrigationTypesSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/irrigation-types")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults()).hasSize(17);
        assertThat(page.getResults().get(0).getValue().equals("B"));
        assertThat(page.getResults().get(0).getDescription().equals("BORDER DIKE"));
        assertThat(page.getResults().get(16).getValue().equals("D"));
        assertThat(page.getResults().get(16).getDescription().equals("WATER SPREADING"));

    }

    @Test
    public void testGetCaseTypesSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;
        Integer supported;

        supported = 0; /* return all case type values */
        result = mockMVC.perform(get("/api/v1/references/case-type-values")
                .header("Authorization", "Bearer " + token)
                .param("supported", String.valueOf(supported))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        AllReferencesDto testA = convertTo(result, AllReferencesDto.class);
        assertThat(testA.getResults()).hasSize(23);
        assertThat(testA.getResults().get(0).getValue().equals("AWC"));
        assertThat(testA.getResults().get(0).getDescription().equals("ADMINISTRATIVE"));
        assertThat(testA.getResults().get(22).getValue().equals("WCC"));
        assertThat(testA.getResults().get(22).getDescription().equals("WATER COURT CASE"));

        supported = 1; /* return supported case type values only */
        result = mockMVC.perform(get("/api/v1/references/case-type-values")
                .header("Authorization", "Bearer " + token)
                .param("supported", String.valueOf(supported))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        AllReferencesDto testB = convertTo(result, AllReferencesDto.class);
        assertThat(testB.getResults()).hasSize(15);
        assertThat(testB.getResults().get(0).getValue().equals("ARMR"));
        assertThat(testB.getResults().get(0).getDescription().equals("ARM RULE CASE"));
        assertThat(testB.getResults().get(14).getValue().equals("WCC"));
        assertThat(testB.getResults().get(14).getDescription().equals("WATER COURT CASE"));

    }

    @Test
    public void testGetObjectionStatus() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        result = mockMVC.perform(get("/api/v1/references/objection-status")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AllReferencesDto page = convertTo(result, AllReferencesDto.class);
        assertThat(page.getResults().get(0).getValue().equals("CLSD"));
        assertThat(page.getResults().get(0).getDescription().equals("CLOSED"));
        assertThat(page.getResults().get(4).getValue().equals("WDRN"));
        assertThat(page.getResults().get(4).getDescription().equals("WITHDRAWN"));

    }

    @Test
    public void testGetObjectionTypes() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;
        Integer supported = 0;

        result = mockMVC.perform(get("/api/v1/references/objection-types")
                .header("Authorization", "Bearer " + token)
                .param("supported", supported.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        AllReferencesDto test1 = convertTo(result, AllReferencesDto.class);
        assertThat(test1.getResults().get(0).getValue().equals("CET"));
        assertThat(test1.getResults().get(0).getDescription().equals("CERTIFICATION"));
        assertThat(test1.getResults().get(8).getValue().equals("OMO"));
        assertThat(test1.getResults().get(8).getDescription().equals("ON MOTION"));

        supported = 1;
        result = mockMVC.perform(get("/api/v1/references/objection-types")
                .header("Authorization", "Bearer " + token)
                .param("supported", supported.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        AllReferencesDto test2 = convertTo(result, AllReferencesDto.class);
        assertThat(test2.getResults().get(0).getValue().equals("COB"));
        assertThat(test2.getResults().get(0).getDescription().equals("COUNTER OBJECTION"));
        assertThat(test2.getResults().get(5).getValue().equals("OMO"));
        assertThat(test2.getResults().get(5).getDescription().equals("ON MOTION"));

    }

}
