package org.example.service;

import org.example.TestDataProvider;
import org.example.config.TestConfig;
import org.example.entity.deals.Address;
import org.example.repository.ProductCardRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(classes = TestConfig.class)
public class ProductCardServiceTest {

    @InjectMocks
    private ProductCardService productCardService;

    @Mock
    private ProductCardRepository productCardRepository;

    private TestDataProvider testDataProvider;

    private Address newAddress;

    @BeforeMethod
    public void setUp() {

        this.testDataProvider = new TestDataProvider();

        if (productCardRepository == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(productCardRepository);
        }

        newAddress = new Address(3L, "new country", "new town", "new street", "99");

        when(productCardRepository.findAll()).thenReturn(testDataProvider.getProductCards());
        when(productCardRepository.findAddressByFields(newAddress.getCountry(), newAddress.getTown(), newAddress.getStreet(), newAddress.getBuildingNumber())).thenReturn(Optional.empty());

        for (var productCard : testDataProvider.getProductCards()) {
            when(productCardRepository.merge(productCard)).thenReturn(productCard);
        }

        for (var address : testDataProvider.getAddresses()) {
            when(productCardRepository.findAddressByFields(address.getCountry(), address.getTown(), address.getStreet(), address.getBuildingNumber())).thenReturn(Optional.of(address));
        }
    }

    @Test
    public void testGetAllAvailable() {

        var productCardsExpected = testDataProvider.getProductCards().stream()
                .filter(p -> !p.isLeased())
                .toList();

        assertEquals(productCardService.getAllAvailable(), productCardsExpected);
        verify(productCardRepository, times(1)).findAll();
    }

    @Test
    public void testGetOrCreateAddressByProperties() {

        for (var address : testDataProvider.getAddresses()) {
            assertEquals(productCardService.getOrCreateAddressByProperties(address.getCountry(), address.getTown(), address.getStreet(), address.getBuildingNumber()), address);
            verify(productCardRepository, times(1)).findAddressByFields(address.getCountry(), address.getTown(), address.getStreet(), address.getBuildingNumber());
        }

        assertEquals(productCardService.getOrCreateAddressByProperties(newAddress.getCountry(), newAddress.getTown(), newAddress.getStreet(), newAddress.getBuildingNumber()), newAddress);
        verify(productCardRepository, times(1)).findAddressByFields(newAddress.getCountry(), newAddress.getTown(), newAddress.getStreet(), newAddress.getBuildingNumber());
    }

    @Test
    public void testCreate() {
        for (var productCard : testDataProvider.getProductCards()) {
            when(productCardService.create(productCard)).thenReturn(productCard);
        }
    }
}