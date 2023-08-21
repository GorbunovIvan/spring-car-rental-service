package org.example.controller.converters;

import org.example.TestDataProvider;
import org.example.config.TestConfig;
import org.example.entity.deals.Address;
import org.example.service.ProductCardService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(classes = TestConfig.class)
public class AddressConverterTest {

    @InjectMocks
    private AddressConverter addressConverter;

    @Mock
    private ProductCardService productCardService;

    private TestDataProvider testDataProvider;

    private Address newAddress;

    @BeforeMethod
    public void setUp() {

        testDataProvider = new TestDataProvider();

        if (productCardService == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(productCardService);
        }

        newAddress = new Address("new country", "new town", "new street", "99");
        when(productCardService.getOrCreateAddressByProperties(newAddress.getCountry(), newAddress.getTown(), newAddress.getStreet(), newAddress.getBuildingNumber())).thenReturn(newAddress);

        for (var address : testDataProvider.getAddresses()) {
            when(productCardService.getOrCreateAddressByProperties(address.getCountry(), address.getTown(), address.getStreet(), address.getBuildingNumber())).thenReturn(address);
        }
    }

    @Test
    public void testConvert() {

        for (var address : testDataProvider.getAddresses()) {
            String source = String.format("%s, %s, %s, %s", address.getCountry(), address.getTown(), address.getStreet(), address.getBuildingNumber());
            assertEquals(addressConverter.convert(source), address);
            verify(productCardService, times(1)).getOrCreateAddressByProperties(address.getCountry(), address.getTown(), address.getStreet(), address.getBuildingNumber());
        }

        String source = String.format("%s, %s, %s, %s", newAddress.getCountry(), newAddress.getTown(), newAddress.getStreet(), newAddress.getBuildingNumber());
        assertEquals(addressConverter.convert(source), newAddress);
        verify(productCardService, times(1)).getOrCreateAddressByProperties(newAddress.getCountry(), newAddress.getTown(), newAddress.getStreet(), newAddress.getBuildingNumber());
    }
}