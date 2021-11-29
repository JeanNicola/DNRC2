package gov.mt.wris;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gov.mt.wris.dtos.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import gov.mt.wris.controllers.CustomersController;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
public class CustomersIntegrationTests  extends BaseTestCase {

    @Autowired
    private MockMvc mockMVC;

    @Autowired
    CustomersController customersController;

    @Test
    @Rollback
    public void testSearchCustomersSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactID;

        contactID = "6802";
        result = mockMVC.perform(get("/api/v1/customers?pageSize=50")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactID)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerPageDto page = convertTo(result, CustomerPageDto.class);
        assertThat(page.getResults()).hasSize(1);
        assertThat(page.getResults().get(0).getName()).isEqualTo("O'CONNELL FARMS LIMITED PARTNERSHIP");

    }

    @Test
    @Rollback
    public void testSearchCustomersContactsSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String sort = "?sortDirection=ASC&sortColumn=CONTACTID";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/contacts" + sort)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        CustomerContactSearchPageDto page = convertTo(result, CustomerContactSearchPageDto.class);
        assertThat(page.getResults()).hasSize(25);

    }

    @Test
    @Rollback
    public void testSearchCustomersContactsByContactIdSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactId;
        contactId = "97771";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/contacts")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        CustomerContactSearchPageDto page = convertTo(result, CustomerContactSearchPageDto.class);
        assertThat(page.getResults()).hasSize(1);
        assertThat(page.getResults().get(0).getLastName().equals("O'CONNELL FARMS LIMITED PARTNERSHIP"));

    }

    public void testSearchCustomersContactsStatusAndSuffixSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactId;

        contactId = "5304";
        result = mockMVC.perform(get("/api/v1/customers/contacts")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactSearchPageDto page = convertTo(result, CustomerContactSearchPageDto.class);
        assertThat(page.getResults()).hasSize(1);
        assertThat(page.getResults().get(0).getContactTypeValue().equals("INDIVIDUAL"));
        assertThat(page.getResults().get(0).getContactStatusValue().equals("DECEASED"));
        assertThat(page.getResults().get(0).getSuffixValue().equals("SENIOR"));

    }

    @Test
    @Rollback
    public void testSearchCustomersContactsByNameSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String lastName = "SMITH";
        String firstName = "HOWARD";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/contacts")
                .header("Authorization", "Bearer " + token)
                .param("lastName", lastName)
                .param("firstName", firstName)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        CustomerContactSearchPageDto page = convertTo(result, CustomerContactSearchPageDto.class);
        assertThat(page.getResults()).hasSize(7);
        assertThat(page.getResults().get(0).getLastName().equals("SMITH"));

    }

    @Test
    @Rollback
    public void testSearchCustomersContactsLikeByNameSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String lastName = "SM%";
        String firstName = "HOWARD";
        String sort = "?sortDirection=ASC&sortColumn=NAME";

        long start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/contacts")
                .header("Authorization", "Bearer " + token)
                .param("lastName", lastName)
                .param("firstName", firstName)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");

        CustomerContactSearchPageDto page = convertTo(result, CustomerContactSearchPageDto.class);
        assertThat(page.getResults().get(0).getName().equals("SMITH, HOWARD A"));
        assertThat(page.getResults().get(1).getName().equals("SMITH, HOWARD D"));
        assertThat(page.getResults().get(2).getName().equals("SMITH, HOWARD E"));
        assertThat(page.getResults().get(3).getName().equals("SMITH, HOWARD J"));
        assertThat(page.getResults().get(4).getName().equals("SMITH, HOWARD L"));

    }

    @Test
    @Rollback
    public void testSearchCustomersContactsPrimaryAddressOrder() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String lastName = "SM%";
        String firstName = "HOWARD%";
        String sort = "?sortDirection=ASC&sortColumn=ADDRESS";

        result = mockMVC.perform(get("/api/v1/customers/contacts" + sort)
                .header("Authorization", "Bearer " + token)
                .param("lastName", lastName)
                .param("firstName", firstName)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactSearchPageDto page = convertTo(result, CustomerContactSearchPageDto.class);
        assertThat(page.getResults().get(0).getAddresses().get(0).getPrimaryMail().equals("Y"));
        assertThat(page.getResults().get(0).getAddresses().get(0).getAddressLine1().equals("***DECEASED***"));
        assertThat(page.getResults().get(0).getAddresses().get(0).getAddressId().equals(47235));

    }

    @Test
    @Rollback
    public void testSearchNotTheSameSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactId = "46636";

        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/not-the-same")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        NotTheSameSearchPageDto page = convertTo(result, NotTheSameSearchPageDto.class);
        assertThat(page.getResults()).hasSize(2);
        assertThat(page.getResults().get(0).getNotthesameId().equals(43));
        assertThat(page.getResults().get(0).getContactId().equals(280861));
        assertThat(page.getResults().get(0).getLastName().equals("SMITH"));
        assertThat(page.getResults().get(1).getNotthesameId().equals(43));
        assertThat(page.getResults().get(1).getContactId().equals(46635));
        assertThat(page.getResults().get(1).getLastName().equals("SMITH"));

    }

    @Test
    @Rollback
    public void testSearchNotTheSameSort() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactId = "46636";

        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/not-the-same/?sortDirection=ASC&notTheSameSortColumn=NAME")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        NotTheSameSearchPageDto page1 = convertTo(result, NotTheSameSearchPageDto.class);
        assertThat(page1.getResults().get(0).getName().equals("SMITH, THOMAS L"));
        assertThat(page1.getResults().get(1).getName().equals("SMITH, THOMAS L JR"));

        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/not-the-same/?sortDirection=DESC&notTheSameSortColumn=NAME")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        NotTheSameSearchPageDto page2 = convertTo(result, NotTheSameSearchPageDto.class);
        assertThat(page2.getResults().get(0).getName().equals("SMITH, THOMAS L JR"));
        assertThat(page2.getResults().get(1).getName().equals("SMITH, THOMAS L"));

        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/not-the-same/?sortDirection=ASC&notTheSameSortColumn=CONTACTID")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        NotTheSameSearchPageDto page3 = convertTo(result, NotTheSameSearchPageDto.class);
        assertThat(page3.getResults().get(0).getContactId().equals(280861));
        assertThat(page3.getResults().get(0).getContactId().equals(46635));

        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/not-the-same/?sortDirection=DESC&notTheSameSortColumn=CONTACTID")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        NotTheSameSearchPageDto page4 = convertTo(result, NotTheSameSearchPageDto.class);
        assertThat(page4.getResults().get(0).getContactId().equals(46635));
        assertThat(page4.getResults().get(0).getContactId().equals(280861));

    }

    @Test
    @Rollback
    public void testSearchNotTheSameNoRowsFound() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactId = "20202020";

        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/not-the-same")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        NotTheSameSearchPageDto page = convertTo(result, NotTheSameSearchPageDto.class);
        assertThat(page.getResults()).hasSize(0);

    }

    @Test
    //@Rollback
    public void testGetCustomersContactSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactId;

        contactId = "6802";
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + contactId)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactDto contact = convertTo(result, CustomerContactDto.class);
        assertThat(contact.getLastName().equals("O'CONNELL FARMS LIMITED PARTNERSHIP"));

    }

    @Test
    //@Rollback
    public void testGetCustomersContactCityNameStateNameAndZipCodeSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactId;

        //contactId = "216326";
        contactId = "46636";
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + contactId)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactDto contact = convertTo(result, CustomerContactDto.class);
        assertThat(contact.getAddresses().get(0).getCityName().equals("SUPERIOR"));
        assertThat(contact.getAddresses().get(0).getStateName().equals("MONTANA"));
        assertThat(contact.getAddresses().get(0).getZipCode().equals("59872"));

    }

    @Test
    //@Rollback
    public void testGetCustomersContactFail() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactId;

        contactId = "101010101010";
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + contactId)
                .content("{}")
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();
        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Contact with id " + contactId + " does not exist");

    }

    @Test
    @Rollback
    public void testSearchCustomersContactsByContactTypeNoResults() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactType = "BSN";

        result = mockMVC.perform(get("/api/v1/customers/contacts")
                .header("Authorization", "Bearer " + token)
                .param("contactType", contactType)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactSearchPageDto page = convertTo(result, CustomerContactSearchPageDto.class);
        assertThat(page.getResults()).hasSize(0);

    }

    @Test
    @Rollback
    public void testSearchCustomersContactsByContactTypeBusiness() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactType = "BUSN";

        result = mockMVC.perform(get("/api/v1/customers/contacts")
                .header("Authorization", "Bearer " + token)
                .param("contactType", contactType)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactSearchPageDto page = convertTo(result, CustomerContactSearchPageDto.class);
        assertThat(page.getResults()).hasSize(25);
        assertThat(page.getResults().get(0).getLastName().equals("4-J FFARMS"));
        assertThat(page.getResults().get(10).getLastName().equals("Z Y BROWN RANCH INC"));

    }

    @Test
    @Rollback
    public void testSearchCustomersContactsByContactStatusUnknown() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String contactStatus = "LOC";
        String sort = "?sortDirection=ASC&sortColumn=CONTACTID";

        result = mockMVC.perform(get("/api/v1/customers/contacts" + sort)
                .header("Authorization", "Bearer " + token)
                .param("contactStatus", contactStatus)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactSearchPageDto page = convertTo(result, CustomerContactSearchPageDto.class);
        assertThat(page.getResults().get(0).getContactStatus().equals("LOC"));
        assertThat(page.getResults().get(0).getLastName().equals("AABERG"));
        assertThat(page.getResults().get(1).getContactStatus().equals("LOC"));
        assertThat(page.getResults().get(1).getLastName().equals("ZELLER, THELMA H TRUST"));

    }

    @Test
    @Rollback
    public void testCreateCustomersContactsSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;

        CustomerContactCreationDto ccd = new CustomerContactCreationDto();
        ccd.setFirstName("Rod");
        ccd.setLastName("Johnsons");
        ccd.setContactType("BUSN");
        ccd.setMiddleInitial("M");

        List<AddressCreationDto> addresses = new ArrayList<>();
        for(int i=1; i<4; i++) {
            AddressCreationDto a = new AddressCreationDto();
            a.setAddressLine1(String.format("999%s International Pkwy", i));
            a.setZipCodeId(Long.valueOf(1685));
            a.setCityId(Long.valueOf(1240));
            a.setPrimaryMail((i==1?"Y":"N"));

            addresses.add(a);
        }
        ccd.setAddresses(addresses);

        result = mockMVC.perform(post("/api/v1/customers/contacts")
                .content(getJson(ccd))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        CustomerContactDto nccd = convertTo(result, CustomerContactDto.class);
        assertThat(nccd.getLastName().equals("Johnson"));
        assertThat(nccd.getAddresses().get(0).getPrimaryMail().equals("Y"));
        assertThat(nccd.getAddresses().get(1).getPrimaryMail().equals("N"));
        assertThat(nccd.getAddresses().get(2).getPrimaryMail().equals("N"));

    }

    @Test
    @Rollback
    public void testCreateCustomersContactsFailMissingContactType() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        CustomerContactCreationDto ccd = new CustomerContactCreationDto();
        String firstName = getRandomString();
        ccd.setFirstName(firstName);
        ccd.setLastName("Johnsons");
        //ccd.setContactType("BUSN");
        ccd.setMiddleInitial("M");

        List<AddressCreationDto> addresses = new ArrayList<>();
        for(int i=1; i<4; i++) {
            AddressCreationDto a = new AddressCreationDto();
            a.setAddressLine1(String.format("999%s International Pkwy", i));
            a.setZipCodeId(Long.valueOf(13474));
            a.setCityId(Long.valueOf(9724));
            a.setPrimaryMail((i==1?"Y":"N"));

            addresses.add(a);
        }
        ccd.setAddresses(addresses);

        result = mockMVC.perform(post("/api/v1/customers/contacts")
                .content(getJson(ccd))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Contact Type is missing");

    }

    @Test
    @Rollback
    public void testCreateCustomersContactsFailMissingLastName() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        CustomerContactCreationDto ccd = new CustomerContactCreationDto();
        String firstName = getRandomString();
        ccd.setFirstName(firstName);
        //ccd.setLastName("Johnsons");
        ccd.setContactType("BUSN");
        ccd.setMiddleInitial("M");

        List<AddressCreationDto> addresses = new ArrayList<>();
        for(int i=1; i<4; i++) {
            AddressCreationDto a = new AddressCreationDto();
            a.setAddressLine1(String.format("999%s International Pkwy", i));
            a.setZipCodeId(Long.valueOf(13474));
            a.setCityId(Long.valueOf(9724));
            a.setPrimaryMail((i==1?"Y":"N"));

            addresses.add(a);
        }
        ccd.setAddresses(addresses);

        result = mockMVC.perform(post("/api/v1/customers/contacts")
                .content(getJson(ccd))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Last Name is missing");

    }

    @Test
    @Rollback
    public void testCreateCustomersContactsFailNoAddresses() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        CustomerContactCreationDto ccd = new CustomerContactCreationDto();
        String firstName = getRandomString();
        ccd.setFirstName(firstName);
        ccd.setLastName("Johnsons");
        ccd.setContactType("BUSN");
        ccd.setMiddleInitial("M");

        result = mockMVC.perform(post("/api/v1/customers/contacts")
                .content(getJson(ccd))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Addresses must be greater than or equal to 1");

    }

    @Test
    @Rollback
    public void testCreateCustomersContactsFailMinimumOneAddresses() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        CustomerContactCreationDto ccd = new CustomerContactCreationDto();
        String firstName = getRandomString();
        ccd.setFirstName(firstName);
        ccd.setLastName("Johnsons");
        ccd.setContactType("BUSN");
        ccd.setMiddleInitial("M");

        List<AddressCreationDto> addresses = new ArrayList<>();
        ccd.setAddresses(addresses);

        result = mockMVC.perform(post("/api/v1/customers/contacts")
                .content(getJson(ccd))
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Addresses must be greater than or equal to 1");

    }

    @Test
    @Rollback
    public void testChangeCustomersContactsSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;

        CustomerContactCreationDto ccd = new CustomerContactCreationDto();
        String firstName = getRandomString();
        ccd.setFirstName(firstName);
        ccd.setLastName("Johnson");
        ccd.setContactType("BUSN");
        ccd.setMiddleInitial("M");
        String contactId = "427721";

        result = mockMVC.perform(put("/api/v1/customers/contacts/" + contactId)
                .content(getJson(ccd))
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        result = mockMVC.perform(get("/api/v1/customers/contacts/" + contactId)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactDto contact = convertTo(result, CustomerContactDto.class);
        assertThat(contact.getFirstName().equals(firstName));

    }

    @Test
    @Rollback
    public void testChangeCustomersContactsAddressNotesSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Long customerId = Long.valueOf(97771);
        String newModReason = getRandomString();

        result = mockMVC.perform(get("/api/v1/customers/contacts/" + customerId)
                .header("Authorization", "Bearer " + token)
                .param("contactId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactDto currentContact = convertTo(result, CustomerContactDto.class);
        assertThat(currentContact.getContactId().equals(customerId));

        CustomerContactUpdateDto updatedContact = new CustomerContactUpdateDto();
        updatedContact.setContactStatus(currentContact.getContactStatus());
        updatedContact.setContactType(currentContact.getContactType());
        updatedContact.setFirstName(currentContact.getFirstName());
        updatedContact.setLastName(currentContact.getLastName());
        updatedContact.setMiddleInitial(currentContact.getMiddleInitial());
        updatedContact.setSuffix(currentContact.getSuffix());
        List<AddressUpdateDto> upda = new ArrayList<>();
        currentContact.getAddresses().forEach((a) -> {
            AddressUpdateDto dto = new AddressUpdateDto();
            dto.setAddressId(a.getAddressId());
            dto.setCustomerId(a.getCustomerId());
            dto.setAddressLine1(a.getAddressLine1());
            dto.setAddressLine2(a.getAddressLine2());
            dto.setAddressLine3(a.getAddressLine3());
            dto.setPrimaryMail(a.getPrimaryMail());
            dto.setZipCodeId(a.getZipCodeId());
            dto.foreignAddress(a.getForeignAddress());
            dto.setForeignPostal(a.getForeignPostal());
            dto.setModReason(a.getModReason());
            dto.setForeignAddress(a.getForeignAddress());
            dto.setModReason(newModReason);
            upda.add(dto);
        });
        updatedContact.setAddresses(upda);

        result = mockMVC.perform(put("/api/v1/customers/contacts/" + customerId)
                .content(getJson(updatedContact))
                .header("Authorization", "Bearer " + token)
                .param("contactId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        result = mockMVC.perform(get("/api/v1/customers/contacts/" + customerId)
                .header("Authorization", "Bearer " + token)
                .param("contactId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        CustomerContactDto newUpdatedContact = convertTo(result, CustomerContactDto.class);
        assertThat(newUpdatedContact.getAddresses().get(0).getModReason().equals(newModReason));
        assertThat(newUpdatedContact.getAddresses().get(0).getPrimaryMail().equals(
                currentContact.getAddresses().get(0).getPrimaryMail())
        );

    }

    @Test
    @Rollback
    public void testChangeCustomersContactsFailMissingContactType() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        CustomerContactCreationDto ccd = new CustomerContactCreationDto();
        String firstName = getRandomString();
        ccd.setFirstName(firstName);
        ccd.setLastName("Johnson");
        //ccd.setContactType("BUSN");
        ccd.setMiddleInitial("M");
        String contactId = "427721";

        result = mockMVC.perform(put("/api/v1/customers/contacts/" + contactId)
                .content(getJson(ccd))
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Contact Type is missing");

    }

    @Test
    @Rollback
    public void testChangeCustomersContactsFailMissingLastName() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        CustomerContactCreationDto ccd = new CustomerContactCreationDto();
        String firstName = getRandomString();
        ccd.setFirstName(firstName);
        //ccd.setLastName("Johnson");
        ccd.setContactType("BUSN");
        ccd.setMiddleInitial("M");
        String contactId = "427721";

        result = mockMVC.perform(put("/api/v1/customers/contacts/" + contactId)
                .content(getJson(ccd))
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Last Name is missing");

    }

    @Test
    @Rollback
    public void testSearchElectronicContactsByCustomerIdSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String customerId = "97771";

        result = mockMVC.perform(get("/api/v1/customers/" + customerId + "/electronic-contacts")
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        ElectronicContactsSearchPageDto page = convertTo(result, ElectronicContactsSearchPageDto.class);
        assertThat(page.getResults()).hasSize(3);
        assertThat(page.getResults().get(0).getElectronicId().equals(7931));
        assertThat(page.getResults().get(0).getElectronicType().equals("HPHN"));
        assertThat(page.getResults().get(0).getElectronicValue().equals("406-895-2562"));
        assertThat(page.getResults().get(0).getElectronicId().equals(7932));
        assertThat(page.getResults().get(0).getElectronicType().equals("CPHN"));
        assertThat(page.getResults().get(0).getElectronicValue().equals("406-765-7014"));

    }

    @Test
    @Rollback
    public void testSearchElectronicContactsSort() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(282550);
        String ASC = "ASC";
        String DESC = "DESC";
        String ELECTRONICID = "ELECTRONICID";
        String TYPE = "TYPE";
        String VALUE = "VALUE";
        String NOTES = "NOTES";

        String URL01 = "/api/v1/customers/"
                + customerId
                + "/electronic-contacts?sortDirection="
                + ASC
                + "&sortColumn="
                + ELECTRONICID;
        result = mockMVC.perform(get(URL01)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        ElectronicContactsSearchPageDto page01 = convertTo(result, ElectronicContactsSearchPageDto.class);
        assertThat(page01.getResults().get(0).getElectronicId().equals(777));
        assertThat(page01.getResults().get(1).getElectronicId().equals(14631));
        assertThat(page01.getResults().get(2).getElectronicId().equals(15503));
        assertThat(page01.getResults().get(3).getElectronicId().equals(17899));
        assertThat(page01.getResults().get(4).getElectronicId().equals(17993));


        String URL02 = "/api/v1/customers/"
                + customerId
                + "/electronic-contacts?sortDirection="
                + DESC
                + "&sortColumn="
                + ELECTRONICID;
        result = mockMVC.perform(get(URL02)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        ElectronicContactsSearchPageDto page02 = convertTo(result, ElectronicContactsSearchPageDto.class);
        assertThat(page02.getResults().get(0).getElectronicId().equals(24462));
        assertThat(page02.getResults().get(1).getElectronicId().equals(23990));
        assertThat(page02.getResults().get(2).getElectronicId().equals(22960));
        assertThat(page02.getResults().get(3).getElectronicId().equals(282550));
        assertThat(page02.getResults().get(4).getElectronicId().equals(282550));


        String URL03 = "/api/v1/customers/"
                + customerId
                + "/electronic-contacts?sortDirection="
                + ASC
                + "&sortColumn="
                + TYPE;
        result = mockMVC.perform(get(URL03)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        ElectronicContactsSearchPageDto page03 = convertTo(result, ElectronicContactsSearchPageDto.class);
        assertThat(page03.getResults().get(0).getElectronicId().equals(777));
        assertThat(page03.getResults().get(0).getElectronicType().equals("WPHN"));
        assertThat(page03.getResults().get(1).getElectronicId().equals(14631));
        assertThat(page03.getResults().get(1).getElectronicType().equals("WPHN"));
        assertThat(page03.getResults().get(2).getElectronicId().equals(15503));
        assertThat(page03.getResults().get(2).getElectronicType().equals("WPHN"));
        assertThat(page03.getResults().get(3).getElectronicId().equals(17899));
        assertThat(page03.getResults().get(3).getElectronicType().equals("WPHN"));
        assertThat(page03.getResults().get(4).getElectronicId().equals(17993));
        assertThat(page03.getResults().get(4).getElectronicType().equals("WPHN"));


        String URL04 = "/api/v1/customers/"
                + customerId
                + "/electronic-contacts?sortDirection="
                + ASC
                + "&sortColumn="
                + VALUE;
        result = mockMVC.perform(get(URL04)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        ElectronicContactsSearchPageDto page04 = convertTo(result, ElectronicContactsSearchPageDto.class);
        assertThat(page04.getResults().get(0).getElectronicId().equals(777));
        assertThat(page04.getResults().get(0).getElectronicValue().equals("406-444-0451"));
        assertThat(page04.getResults().get(1).getElectronicId().equals(14631));
        assertThat(page04.getResults().get(1).getElectronicValue().equals("406-444-0451"));
        assertThat(page04.getResults().get(2).getElectronicId().equals(15503));
        assertThat(page04.getResults().get(2).getElectronicValue().equals("406-444-6071"));
        assertThat(page04.getResults().get(3).getElectronicId().equals(17899));
        assertThat(page04.getResults().get(3).getElectronicValue().equals("406-444-6093"));
        assertThat(page04.getResults().get(4).getElectronicId().equals(17993));
        assertThat(page04.getResults().get(4).getElectronicValue().equals("406-444-6097"));


        String URL05 = "/api/v1/customers/"
                + customerId
                + "/electronic-contacts?sortDirection="
                + ASC
                + "&sortColumn="
                + NOTES;
        result = mockMVC.perform(get(URL05)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        ElectronicContactsSearchPageDto page05 = convertTo(result, ElectronicContactsSearchPageDto.class);
        assertThat(page05.getResults().get(0).getElectronicId().equals(777));
        assertThat(page05.getResults().get(0).getElectronicValue().equals("406-444-0451"));
        assertThat(page05.getResults().get(1).getElectronicId().equals(14631));
        assertThat(page05.getResults().get(1).getElectronicValue().equals("406-444-0451"));
        assertThat(page05.getResults().get(2).getElectronicId().equals(15503));
        assertThat(page05.getResults().get(2).getElectronicValue().equals("406-444-6071"));
        assertThat(page05.getResults().get(3).getElectronicId().equals(17899));
        assertThat(page05.getResults().get(3).getElectronicValue().equals("406-444-6093"));
        assertThat(page05.getResults().get(4).getElectronicId().equals(17993));
        assertThat(page05.getResults().get(4).getElectronicValue().equals("406-444-6097"));


    }

    @Test
    @Rollback
    public void testSearchElectronicContactsByCustomerIdNoResults() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String customerId = "01010102021";

        result = mockMVC.perform(get("/api/v1/customers/" + customerId + "/electronic-contacts")
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        ElectronicContactsSearchPageDto page = convertTo(result, ElectronicContactsSearchPageDto.class);
        assertThat(page.getResults()).hasSize(0);

    }

    @Test
    @Rollback
    public void testGetElectronicContactsByElectronicIdSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String electronicId = "7931";
        String customerId = "97771";

        result = mockMVC.perform(get("/api/v1/customers/" + customerId + "/electronic-contacts/" + electronicId)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId)
                .param("electronicId", electronicId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        ElectronicContactsDto dto = convertTo(result, ElectronicContactsDto.class);
        assertThat(dto.getCustomerId().equals(customerId));
        assertThat(dto.getElectronicId().equals(electronicId));
        assertThat(dto.getElectronicType().equals("HPHN"));
        assertThat(dto.getElectronicValue().equals("406-895-2562"));

    }

    @Test
    @Rollback
    public void testGetElectronicContactsFail() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        String electronicId = "10102021";
        String customerId = "10102021";

        result = mockMVC.perform(get("/api/v1/customers/" + customerId + "/electronic-contacts/" + electronicId)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId)
                .param("electronicId", electronicId)
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();

        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Electronic Contact with id " + electronicId + " does not exist");

    }

    @Test
    @Rollback
    public void testChangeElectronicContactsFailMissingType() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(97771);
        Long electronicId = Long.valueOf(7931);
        String type = "WPHN";
        String value = "905-244-1029";
        String notes = getRandomString();

        ElectronicContactsUpdateDto eccd = new ElectronicContactsUpdateDto();
        String firstName = getRandomString();
        eccd.setCustomerId(customerId);
        //eccd.setElectronicType(type);
        eccd.setElectronicValue(value);
        eccd.setElectronicNotes(notes);

        result = mockMVC.perform(put("/api/v1/customers/"+ customerId + "/electronic-contacts/" + electronicId)
                .content(getJson(eccd))
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .param("electronicId", electronicId.toString())
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Electronic Type is missing");

    }

    @Test
    @Rollback
    public void testChangeElectronicContactsMissingValueFail() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(97771);
        Long electronicId = Long.valueOf(7931);
        String type = "WPHN";
        String value = "905-244-1029";
        String notes = getRandomString();

        ElectronicContactsUpdateDto ecud = new ElectronicContactsUpdateDto();
        String firstName = getRandomString();
        ecud.setCustomerId(customerId);
        ecud.setElectronicType(type);
        ecud.setElectronicNotes(notes);

        result = mockMVC.perform(put("/api/v1/customers/"+ customerId + "/electronic-contacts/" + electronicId)
                .content(getJson(ecud))
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .param("electronicId", electronicId.toString())
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        message = convertTo(result, Message.class);
        assertThat(message.getUserMessage()).isEqualTo("Missing or incorrect values were provided: Electronic Value is missing");

    }

    @Test
    @Rollback
    public void testCreateElectronicContactsSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(97771);
        String type = "WPHN";
        String value = "905-244-1030";
        String notes = getRandomString();

        ElectronicContactsUpdateDto eccd = new ElectronicContactsUpdateDto();
        eccd.setCustomerId(customerId);
        eccd.setElectronicType(type);
        eccd.setElectronicValue(value);
        eccd.setElectronicNotes(notes);

        result = mockMVC.perform(post("/api/v1/customers/"+ customerId + "/electronic-contacts")
                .content(getJson(eccd))
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

        ElectronicContactsDto cdto = convertTo(result, ElectronicContactsDto.class);

        result = mockMVC.perform(get("/api/v1/customers/" + cdto.getCustomerId() + "/electronic-contacts/" + cdto.getElectronicId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", cdto.getCustomerId().toString())
                .param("electronicId", cdto.getElectronicId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        ElectronicContactsDto gdto = convertTo(result, ElectronicContactsDto.class);
        assertThat(gdto.getCustomerId().equals(customerId));
        assertThat(gdto.getElectronicId().equals(cdto.getElectronicId()));
        assertThat(gdto.getElectronicType().equals(type));
        assertThat(gdto.getElectronicValue().equals(value));
        assertThat(gdto.getElectronicNotes().equals(notes));

        result = mockMVC.perform(delete("/api/v1/customers/" + cdto.getCustomerId() + "/electronic-contacts/" + cdto.getElectronicId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", cdto.getCustomerId().toString())
                .param("electronicId", cdto.getElectronicId().toString())
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

    }

    @Test
    @Rollback
    public void testDeleteElectronicContactsSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(97771);
        String type = "WPHN";
        String value = "905-244-1030";
        String notes = getRandomString();

        ElectronicContactsUpdateDto eccd = new ElectronicContactsUpdateDto();
        eccd.setCustomerId(customerId);
        eccd.setElectronicType(type);
        eccd.setElectronicValue(value);
        eccd.setElectronicNotes(notes);

        result = mockMVC.perform(post("/api/v1/customers/"+ customerId + "/electronic-contacts")
                .content(getJson(eccd))
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

        ElectronicContactsDto cdto = convertTo(result, ElectronicContactsDto.class);

        result = mockMVC.perform(delete("/api/v1/customers/" + cdto.getCustomerId() + "/electronic-contacts/" + cdto.getElectronicId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", cdto.getCustomerId().toString())
                .param("electronicId", cdto.getElectronicId().toString())
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

        result = mockMVC.perform(get("/api/v1/customers/" + cdto.getCustomerId() + "/electronic-contacts/" + cdto.getElectronicId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", cdto.getCustomerId().toString())
                .param("electronicId", cdto.getElectronicId().toString())
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @Test
    @Rollback
    public void testDeleteElectronicContactsFailNotFound() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        String customerId = "97771";
        String electronicId = "109999";

        result = mockMVC.perform(delete("/api/v1/customers/" + customerId + "/electronic-contacts/" + electronicId)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId)
                .param("electronicId", electronicId)
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();

    }

    @Test
    @Rollback
    public void testSearchAddressSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(97771);
        /* customer with address that has missing created_by row in master staff indexes
        customerId = Long.valueOf(261812);
        */

        result = mockMVC.perform(get("/api/v1/customers/contacts/" + customerId + "/addresses")
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressSearchPageDto page = convertTo(result, AddressSearchPageDto.class);
        assertThat(page.getResults()).hasSize(25);
        assertThat(page.getResults().get(0).getAddressId().equals(9738));
        assertThat(page.getResults().get(0).getZipCode().equals("59254"));
        assertThat(page.getResults().get(0).getCityName().equals("PLENTYWOOD"));
        assertThat(page.getResults().get(0).getPrimaryMail().equals("Y"));
        assertThat(page.getResults().get(0).getPrimaryMailValue().equals("YES"));
        assertThat(page.getResults().get(10).getAddressId().equals(588027));
        assertThat(page.getResults().get(10).getZipCode().equals("43022"));
        assertThat(page.getResults().get(10).getCityName().equals("GAMBIER"));
        assertThat(page.getResults().get(10).getPrimaryMail().equals("N"));
        assertThat(page.getResults().get(0).getPrimaryMailValue().equals("NO"));

    }

    @Test
    @Rollback
    public void testSearchAddressSort() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(282550);

        String ASC = "ASC";
        String DESC = "DESC";
        String ADDRESSID = "ADDRESSID";
        String CUSTOMERID = "CUSTOMERID";
        String CITY = "CITY";
        String STATE = "STATE";
        String ZIP = "ZIP";
        String ADDRESSLINE1 = "ADDRESSLINE1";
        String ADDRESSLINE2 = "ADDRESSLINE2";
        String ADDRESSLINE3 = "ADDRESSLINE3";

        /* ADDRESSID */
        String URL01 = "/api/v1/customers/contacts/"
                + customerId
                + "/addresses?sortDirection="
                + ASC
                + "&sortColumn="
                + ADDRESSID;

        result = mockMVC.perform(get(URL01)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressSearchPageDto page01 = convertTo(result, AddressSearchPageDto.class);
        assertThat(page01.getResults().get(0).getAddressId().equals(359160));
        assertThat(page01.getResults().get(0).getPrimaryMail().equals("Y"));
        assertThat(page01.getResults().get(1).getAddressId().equals(7506));
        assertThat(page01.getResults().get(1).getPrimaryMail().equals("N"));
        assertThat(page01.getResults().get(2).getAddressId().equals(7507));
        assertThat(page01.getResults().get(2).getPrimaryMail().equals("N"));
        assertThat(page01.getResults().get(3).getAddressId().equals(7508));
        assertThat(page01.getResults().get(3).getPrimaryMail().equals("N"));
        assertThat(page01.getResults().get(4).getAddressId().equals(7509));
        assertThat(page01.getResults().get(4).getPrimaryMail().equals("N"));
        assertThat(page01.getResults().get(5).getAddressId().equals(7510));
        assertThat(page01.getResults().get(5).getPrimaryMail().equals("N"));

        /* CITY */
        String URL02 = "/api/v1/customers/contacts/"
                + customerId
                + "/addresses?sortDirection="
                + ASC
                + "&sortColumn="
                + CITY;

        result = mockMVC.perform(get(URL02)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressSearchPageDto page02 = convertTo(result, AddressSearchPageDto.class);
        assertThat(page02.getResults().get(0).getAddressId().equals(359160));
        assertThat(page02.getResults().get(0).getPrimaryMail().equals("Y"));
        assertThat(page02.getResults().get(1).getAddressId().equals(7505));
        assertThat(page02.getResults().get(1).getPrimaryMail().equals("N"));
        assertThat(page02.getResults().get(1).getCityName().equals("BILLINGS"));
        assertThat(page02.getResults().get(2).getAddressId().equals(7506));
        assertThat(page02.getResults().get(2).getPrimaryMail().equals("N"));
        assertThat(page02.getResults().get(2).getCityName().equals("BOZEMAN"));
        assertThat(page02.getResults().get(3).getAddressId().equals(7507));
        assertThat(page02.getResults().get(3).getPrimaryMail().equals("N"));
        assertThat(page02.getResults().get(3).getCityName().equals("BUTTE"));
        assertThat(page02.getResults().get(4).getAddressId().equals(277186));
        assertThat(page02.getResults().get(4).getPrimaryMail().equals("N"));
        assertThat(page02.getResults().get(4).getCityName().equals("CORVALLIS"));
        assertThat(page02.getResults().get(5).getAddressId().equals(282550));
        assertThat(page02.getResults().get(5).getPrimaryMail().equals("N"));
        assertThat(page02.getResults().get(5).getCityName().equals("GLENDIVE"));

        /* ZIP */
        String URL03 = "/api/v1/customers/contacts/"
                + customerId
                + "/addresses?sortDirection="
                + ASC
                + "&sortColumn="
                + ZIP;

        result = mockMVC.perform(get(URL03)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressSearchPageDto page03 = convertTo(result, AddressSearchPageDto.class);
        assertThat(page03.getResults().get(0).getAddressId().equals(359160));
        assertThat(page03.getResults().get(0).getPrimaryMail().equals("Y"));
        assertThat(page03.getResults().get(1).getPrimaryMail().equals("N"));
        assertThat(page03.getResults().get(1).getZipCode().equals("59104"));
        assertThat(page03.getResults().get(2).getPrimaryMail().equals("N"));
        assertThat(page03.getResults().get(2).getZipCode().equals("59201"));
        assertThat(page03.getResults().get(3).getPrimaryMail().equals("N"));
        assertThat(page03.getResults().get(3).getZipCode().equals("59301"));
        assertThat(page03.getResults().get(4).getPrimaryMail().equals("N"));
        assertThat(page03.getResults().get(4).getZipCode().equals("59330"));
        assertThat(page03.getResults().get(5).getPrimaryMail().equals("N"));
        assertThat(page03.getResults().get(5).getZipCode().equals("59403"));

        /* ADDRESSLINE1 */
        String URL04 = "/api/v1/customers/contacts/"
                + customerId
                + "/addresses?sortDirection="
                + ASC
                + "&sortColumn="
                + ADDRESSLINE1;

        result = mockMVC.perform(get(URL03)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressSearchPageDto page04 = convertTo(result, AddressSearchPageDto.class);
        assertThat(page04.getResults().get(0).getAddressId().equals(359160));
        assertThat(page04.getResults().get(0).getPrimaryMail().equals("Y"));
        assertThat(page04.getResults().get(1).getPrimaryMail().equals("N"));
        assertThat(page04.getResults().get(1).getAddressLine1().equals("PO BOX 20437"));
        assertThat(page04.getResults().get(2).getPrimaryMail().equals("N"));
        assertThat(page04.getResults().get(2).getAddressLine1().equals("HIGHWAY DIVISION"));
        assertThat(page04.getResults().get(3).getPrimaryMail().equals("N"));
        assertThat(page04.getResults().get(3).getAddressLine1().equals("PO BOX 460"));
        assertThat(page04.getResults().get(4).getPrimaryMail().equals("N"));
        assertThat(page04.getResults().get(4).getAddressLine1().equals("PO BOX 890"));
        assertThat(page04.getResults().get(5).getPrimaryMail().equals("N"));
        assertThat(page04.getResults().get(5).getAddressLine1().equals("PO BOX 1359"));

    }

    @Test
    @Rollback
    public void testGetAddressSuccess() throws Exception {

        String token = getAccessToken();

        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(1719);
        Long addressId = Long.valueOf(1704);

        result = mockMVC.perform(get("/api/v1/customers/contacts/" + customerId + "/addresses/" + addressId)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .param("addressId", addressId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressDto dto = convertTo(result, AddressDto.class);
        assertThat(dto.getCustomerId().equals(customerId));
        assertThat(dto.getAddressId().equals(addressId));

    }

    @Test
    @Rollback
    public void testCreateAddressSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        Long customerId = Long.valueOf(97771);
        String addr1 = getRandomString();
        String addr2 = getRandomString();
        String addr3 = getRandomString();

        AddressCreationDto acd = new AddressCreationDto();
        acd.setAddressLine1(addr1);
        acd.setAddressLine2(addr2);
        acd.setAddressLine3(addr3);
        acd.setZipCodeId(Long.valueOf(13474));
        acd.setCityId(Long.valueOf(9724));
        acd.setPrimaryMail("N");

        result = mockMVC.perform(post("/api/v1/customers/contacts/"+ customerId + "/addresses")
                .content(getJson(acd))
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

        AddressDto adto1 = convertTo(result, AddressDto.class);

        result = mockMVC.perform(get("/api/v1/customers/contacts/" + adto1.getCustomerId() + "/addresses/" + adto1.getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto1.getCustomerId().toString())
                .param("addressId", adto1.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressDto adto2 = convertTo(result, AddressDto.class);
        assertThat(adto2.getPrimaryMail().equals("Y"));
        assertThat(adto2.getCustomerId().equals(customerId));
        assertThat(adto2.getAddressLine1().equals(addr1));
        assertThat(adto2.getAddressLine2().equals(addr2));
        assertThat(adto2.getAddressLine3().equals(addr3));

        result = mockMVC.perform(delete("/api/v1/customers/contacts/" + adto2.getCustomerId() + "/addresses/" + adto2.getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto2.getCustomerId().toString())
                .param("addressId", adto2.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

    }

    @Test
    @Rollback
    public void testChangeAddressSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        Long customerId = Long.valueOf(97771);
        String addr1ORIG = getRandomString();
        String addr2ORIG = getRandomString();
        String addr3ORIG = getRandomString();
        String addr1NEW = getRandomString();
        String addr2NEW = getRandomString();
        String addr3NEW = getRandomString();

        AddressCreationDto acd = new AddressCreationDto();
        acd.setAddressLine1(addr1ORIG);
        acd.setAddressLine2(addr2ORIG);
        acd.setAddressLine3(addr3ORIG);
        acd.setZipCodeId(Long.valueOf(13474));
        acd.setCityId(Long.valueOf(9724));
        acd.setForeignAddress("N");
        acd.setUnresolvedFlag("Y");
        acd.setPrimaryMail("Y");

        result = mockMVC.perform(post("/api/v1/customers/contacts/"+ customerId + "/addresses")
                .content(getJson(acd))
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

        AddressDto adto1 = convertTo(result, AddressDto.class);

        result = mockMVC.perform(get("/api/v1/customers/contacts/" + adto1.getCustomerId() + "/addresses/" + adto1.getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto1.getCustomerId().toString())
                .param("addressId", adto1.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressDto adto2 = convertTo(result, AddressDto.class);
        assertThat(adto2.getPrimaryMail().equals("Y"));
        assertThat(adto2.getCustomerId().equals(customerId));
        assertThat(adto2.getAddressLine1().equals(addr1ORIG));
        assertThat(adto2.getAddressLine2().equals(addr2ORIG));
        assertThat(adto2.getAddressLine3().equals(addr3ORIG));

        AddressUpdateDto adto3 = new AddressUpdateDto();
        adto3.setAddressId(adto2.getAddressId());
        adto3.setCustomerId(adto2.getCustomerId());
        adto3.setAddressLine1(addr1NEW);
        adto3.setAddressLine2(addr2NEW);
        adto3.setAddressLine3(addr3NEW);
        adto3.setZipCodeId(Long.valueOf(13520));
        adto3.setCityId(Long.valueOf(9763));
        adto3.setPrimaryMail("N");

        result = mockMVC.perform(put("/api/v1/customers/contacts/"+ adto2.getCustomerId() + "/addresses/" + adto2.getAddressId())
                .content(getJson(adto3))
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto2.getCustomerId().toString())
                .param("addressId", adto2.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        result = mockMVC.perform(get("/api/v1/customers/contacts/" + adto2.getCustomerId() + "/addresses/" + adto2.getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto2.getCustomerId().toString())
                .param("addressId", adto2.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressDto adto4 = convertTo(result, AddressDto.class);
        assertThat(adto4.getPrimaryMail().equals("Y"));
        assertThat(adto4.getCustomerId().equals(customerId));
        assertThat(adto4.getAddressLine1().equals(addr1NEW));
        assertThat(adto4.getAddressLine2().equals(addr2NEW));
        assertThat(adto4.getAddressLine3().equals(addr3NEW));
        assertThat(adto4.getZipCodeId().equals(Long.valueOf(13520)));
        assertThat(adto4.getCityId().equals(Long.valueOf(9763)));


        result = mockMVC.perform(delete("/api/v1/customers/contacts/" + adto4.getCustomerId() + "/addresses/" + adto4.getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto4.getCustomerId().toString())
                .param("addressId", adto4.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

    }

    @Test
    @Rollback
    public void testChangeAddressModReasonSuccess() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;

        /* Add an address to an existing customer */
        Long customerId = Long.valueOf(97771);
        String addr1_line_ORIG = getRandomString();
        String mod_reason_ORIG = getRandomString();

        AddressCreationDto acd = new AddressCreationDto();
        acd.setAddressLine1(addr1_line_ORIG);
        acd.setZipCodeId(Long.valueOf(13474));
        acd.setCityId(Long.valueOf(9724));
        acd.setForeignAddress("N");
        acd.setUnresolvedFlag("Y");
        acd.setModReason(mod_reason_ORIG);
        acd.setPrimaryMail("N");

        result = mockMVC.perform(post("/api/v1/customers/contacts/"+ customerId + "/addresses")
                .content(getJson(acd))
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

        AddressDto adto1 = convertTo(result, AddressDto.class);

        /* Retrieve address we just added */
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + adto1.getCustomerId() + "/addresses/" + adto1.getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto1.getCustomerId().toString())
                .param("addressId", adto1.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        /* Validate the address has what we entered */
        AddressDto adto2 = convertTo(result, AddressDto.class);
        assertThat(adto2.getPrimaryMail().equals("N"));
        assertThat(adto2.getCustomerId().equals(customerId));
        assertThat(adto2.getAddressLine1().equals(addr1_line_ORIG));
        assertThat(adto2.getModReason().equals(mod_reason_ORIG));


        /* Update the address with a multi-line mod reason */
        AddressUpdateDto adto3 = new AddressUpdateDto();
        adto3.setAddressId(adto2.getAddressId());
        adto3.setCustomerId(adto2.getCustomerId());
        adto3.setAddressLine1(adto2.getAddressLine1());
        adto3.setZipCodeId(adto2.getZipCodeId());
        adto3.setCityId(Long.valueOf(adto2.getCityId()));
        adto3.setPrimaryMail(adto2.getPrimaryMail());
        // adding multi-line note to mod reason...
        adto3.setModReason(adto2.getModReason() + "\n" + getRandomString() + "\n" + getRandomString());

        /* Put the update */
        result = mockMVC.perform(put("/api/v1/customers/contacts/"+ adto2.getCustomerId() + "/addresses/" + adto2.getAddressId())
                .content(getJson(adto3))
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto2.getCustomerId().toString())
                .param("addressId", adto2.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        /* Retrieve the address we just updated */
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + adto2.getCustomerId() + "/addresses/" + adto2.getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto2.getCustomerId().toString())
                .param("addressId", adto2.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        /* Validate the multi-line mod reason change */
        AddressDto adto4 = convertTo(result, AddressDto.class);
        assertThat(adto4.getPrimaryMail().equals(adto3.getPrimaryMail()));
        assertThat(adto4.getAddressId().equals(adto3.getAddressId()));
        assertThat(adto4.getCustomerId().equals(customerId));
        assertThat(adto4.getAddressLine1().equals(adto3.getAddressLine1()));
        // check that our new multi-line note is in mod reason...
        assertThat(adto4.getModReason().equals(adto3.getModReason()));

        /* Delete the test record */
        result = mockMVC.perform(delete("/api/v1/customers/contacts/" + adto2.getCustomerId() + "/addresses/" + adto2.getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto4.getCustomerId().toString())
                .param("addressId", adto4.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isNoContent())
                .andReturn();

    }

    @Test
    @Rollback
    public void testChangePrimaryAddressOnAddressCreate() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(97771);


        // Perform search and get id for address currently primary
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + customerId + "/addresses")
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressSearchPageDto page = convertTo(result, AddressSearchPageDto.class);
        assertThat(page.getResults().get(0).getPrimaryMail().equals("Y"));
        Long oldPrimaryAddress = page.getResults().get(0).getAddressId();

        // Create new address and make it primary
        AddressCreationDto acd = new AddressCreationDto();
        acd.setCustomerId(customerId);
        acd.setAddressLine1(getRandomString());
        acd.setZipCodeId(Long.valueOf(13474));
        acd.setCityId(Long.valueOf(9724));
        acd.setForeignAddress("N");
        acd.setUnresolvedFlag("N");
        acd.setPrimaryMail("Y");

        result = mockMVC.perform(post("/api/v1/customers/contacts/"+ customerId + "/addresses")
                .content(getJson(acd))
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

        AddressDto adto1 = convertTo(result, AddressDto.class);

        // Is new address primary?
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + adto1.getCustomerId() + "/addresses/" + adto1.getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto1.getCustomerId().toString())
                .param("addressId", adto1.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressDto adto2 = convertTo(result, AddressDto.class);
        assertThat(adto2.getPrimaryMail().equals("Y"));

        // Get the old primary address and check that it's not primary
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + adto1.getCustomerId() + "/addresses/" + oldPrimaryAddress)
                .header("Authorization", "Bearer " + token)
                .param("customerId", adto1.getCustomerId().toString())
                .param("addressId", adto1.getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressDto adto3 = convertTo(result, AddressDto.class);
        assertThat(adto3.getPrimaryMail().equals("N"));


    }

    @Test
    @Rollback
    public void testChangePrimaryAddressOnAddressUpdate() throws Exception {

        String token = getAccessToken();
        MvcResult result = null;
        Message message = null;
        Long customerId = Long.valueOf(97771);

        // Perform search and get ids for addresses
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + customerId + "/addresses")
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressSearchPageDto page = convertTo(result, AddressSearchPageDto.class);
        assertThat(page.getResults().get(0).getPrimaryMail().equals("Y"));
        assertThat(page.getResults().get(1).getPrimaryMail().equals("N"));
        Long oldPrimaryAddressId = page.getResults().get(0).getAddressId();

        // Send an update for 2nd address in list
        AddressUpdateDto aud = new AddressUpdateDto();
        aud.setAddressId(page.getResults().get(1).getAddressId());
        aud.setCustomerId(customerId);
        aud.setAddressLine1(page.getResults().get(1).getAddressLine1());
        aud.setZipCodeId(page.getResults().get(1).getZipCodeId());
        aud.setCityId(page.getResults().get(1).getCityId());
        aud.setPrimaryMail("Y");

        result = mockMVC.perform(put("/api/v1/customers/contacts/"+ customerId + "/addresses/" + page.getResults().get(1).getAddressId())
                .content(getJson(aud))
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .param("addressId", page.getResults().get(1).getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        result = mockMVC.perform(get("/api/v1/customers/contacts/" + customerId + "/addresses/" + page.getResults().get(1).getAddressId())
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .param("addressId", page.getResults().get(1).getAddressId().toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        // Is updated address now primary?
        AddressDto adto0 = convertTo(result, AddressDto.class);
        assertThat(adto0.getPrimaryMail().equals("Y"));

        // Get the old primary address to check that it's no longer primary
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + customerId + "/addresses/" + oldPrimaryAddressId)
                .header("Authorization", "Bearer " + token)
                .param("customerId", customerId.toString())
                .param("addressId", oldPrimaryAddressId.toString())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        AddressDto adto1 = convertTo(result, AddressDto.class);
        assertThat(adto1.getPrimaryMail().equals("N"));

    }

    @Test
    @Rollback
    public void bugfix_GetCustomerContactOwnershipUpdates_DTFP_1107() throws Exception {

        String token = getAccessToken();
        long start, end;
        float sec;
        MvcResult result = null;
        Message message = null;
        String buyerRole = "?ownershipUpdateRole=" + OwnershipUpdateRole.BUY;
        String sellerRole = "?ownershipUpdateRole=" + OwnershipUpdateRole.SEL;

        String contactId = "214819";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + contactId + "/ownership-updates" + buyerRole)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CustomerOwnershipUpdatePageDto test1 = convertTo(result, CustomerOwnershipUpdatePageDto.class);
        assertThat(test1.getResults()).hasSize(1);
        assertThat(test1.getResults().get(0).getOwnerUpdateId()).isEqualTo(204289);
        assertThat(test1.getResults().get(0).getUpdateType()).isEqualTo("DOR OWNERSHIP UPDATE");
        assertThat(test1.getResults().get(0).getDateReceived()).isEqualTo("2020-01-24");
        assertThat(test1.getResults().get(0).getDateProcessed()).isNull();
        assertThat(test1.getResults().get(0).getDateTerminated()).isEqualTo("2020-08-24");
        assertThat(test1.getResults().get(0).getContractForDeed()).isEqualTo("NO");

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/contacts/" + contactId + "/ownership-updates" + sellerRole)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F;
        System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        CustomerOwnershipUpdatePageDto test2 = convertTo(result, CustomerOwnershipUpdatePageDto.class);
        assertThat(test2.getResults()).hasSize(1);
        assertThat(test2.getResults().get(0).getOwnerUpdateId()).isEqualTo(107253);
        assertThat(test2.getResults().get(0).getUpdateType()).isEqualTo("DOR OWNERSHIP UPDATE");
        assertThat(test2.getResults().get(0).getDateReceived()).isEqualTo("2012-12-28");
        assertThat(test2.getResults().get(0).getDateProcessed()).isEqualTo("2013-05-03");
        assertThat(test2.getResults().get(0).getDateTerminated()).isNull();
        assertThat(test2.getResults().get(0).getContractForDeed()).isEqualTo("NO");

    }

    @Test
    @Rollback
    public void testGetBuyerSellerOwnershipUpdatesForContactVarious() throws Exception {

        String token = getAccessToken();
        long start, end;
        float sec;
        MvcResult result = null;
        Message message = null;
        String buyerRole = "&ownershipUpdateRole=" + OwnershipUpdateRole.BUY;
        String sellerRole = "&ownershipUpdateRole=" + OwnershipUpdateRole.SEL;

        String sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATEID";
        //String contactId = "46339";
        String contactId = "204289";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/ownership-updates" + sort + buyerRole)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        BuyerSellerOwnershipUpdatesForContactPageDto test1 = convertTo(result, BuyerSellerOwnershipUpdatesForContactPageDto.class);
        assertThat(test1.getResults()).hasSize(0);

        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/ownership-updates" + sort + sellerRole)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        BuyerSellerOwnershipUpdatesForContactPageDto test2 = convertTo(result, BuyerSellerOwnershipUpdatesForContactPageDto.class);
        assertThat(test2.getResults()).hasSize(2);
        assertThat(test2.getResults().get(0).getOwnershipUpdateId().equals(10037));
        assertThat(test2.getResults().get(0).getOwnershipUpdateTypeVal().equals("OWNERSHIP UPDATE"));
        assertThat(test2.getResults().get(0).getWaterRightCount().equals(2));
        assertThat(test2.getResults().get(1).getOwnershipUpdateId().equals(82811));
        assertThat(test2.getResults().get(1).getOwnershipUpdateTypeVal().equals("DOR OWNERSHIP UPDATE"));
        assertThat(test2.getResults().get(1).getWaterRightCount().equals(1));

        sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATETYPEVALUE";
        contactId = "330189";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/ownership-updates" + sort + buyerRole)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        BuyerSellerOwnershipUpdatesForContactPageDto test3 = convertTo(result, BuyerSellerOwnershipUpdatesForContactPageDto.class);
        assertThat(test3.getResults()).hasSize(9);
        assertThat(test3.getResults().get(0).getOwnershipUpdateId().equals(113125));
        assertThat(test3.getResults().get(0).getOwnershipUpdateType().equals("DOR OWNERSHIP UPDATE"));
        assertThat(test3.getResults().get(0).getWaterRightCount().equals(1));

        sort = "?sortDirection=ASC&sortColumn=OWNERSHIPUPDATETYPEVALUE";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/ownership-updates" + sort + sellerRole)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        BuyerSellerOwnershipUpdatesForContactPageDto test3a = convertTo(result, BuyerSellerOwnershipUpdatesForContactPageDto.class);
        assertThat(test3a.getResults()).hasSize(1);
        assertThat(test3a.getResults().get(0).getOwnershipUpdateId().equals(115091));
        assertThat(test3a.getResults().get(0).getOwnershipUpdateType().equals("DOR OWNERSHIP UPDATE"));
        assertThat(test3a.getResults().get(0).getWaterRightCount().equals(1));

        sort = "?sortDirection=DESC&sortColumn=OWNERSHIPUPDATETYPEVALUE";
        contactId = "145863";
        start = System.currentTimeMillis();
        result = mockMVC.perform(get("/api/v1/customers/" + contactId + "/ownership-updates" + sort + sellerRole)
                .header("Authorization", "Bearer " + token)
                .param("contactId", contactId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        end = System.currentTimeMillis();
        sec = (end - start) / 1000F; System.out.println("**** ELAPSED TIME: " + sec + " seconds ****");
        BuyerSellerOwnershipUpdatesForContactPageDto test4 = convertTo(result, BuyerSellerOwnershipUpdatesForContactPageDto.class);
        assertThat(test4.getResults()).hasSize(2);
        assertThat(test4.getResults().get(0).getOwnershipUpdateId().equals(25219));
        assertThat(test4.getResults().get(0).getOwnershipUpdateTypeVal().equals("OWNERSHIP UPDATE"));
        assertThat(test4.getResults().get(0).getWaterRightCount().equals(2));
        assertThat(test4.getResults().get(1).getOwnershipUpdateId().equals(26523));
        assertThat(test4.getResults().get(1).getOwnershipUpdateTypeVal().equals("CORRECTION"));
        assertThat(test4.getResults().get(1).getWaterRightCount().equals(1));

    }

    private String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

}
