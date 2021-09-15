package es.udc.rs.telco.test.model.telcoservice;

import es.udc.rs.telco.model.telcoservice.TelcoService;
import es.udc.rs.telco.model.telcoservice.TelcoServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TelcoServiceTest {

    private static TelcoService telcoService = null;

    @BeforeAll
    public static void init() {
        telcoService = TelcoServiceFactory.getService();

    }

    @Test
    public void testAddCustomerAndFindCustomer()  {
        assertTrue(telcoService != null);
    }

}
