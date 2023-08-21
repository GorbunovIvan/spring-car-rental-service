package org.example.controller.converters;

import org.example.TestDataProvider;
import org.example.config.SpringTestConfig;
import org.example.entity.car.CarBrand;
import org.example.service.CarService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(classes = SpringTestConfig.class)
public class CarBrandConverterTest {

    @InjectMocks
    private CarBrandConverter carBrandConverter;

    @Mock
    private CarService carService;

    private TestDataProvider testDataProvider;

    private CarBrand newCarBrand;

    @BeforeMethod
    public void setUp() {

        testDataProvider = new TestDataProvider();

        if (carService == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(carService);
        }

        newCarBrand = new CarBrand("new test car-brand");
        when(carService.getCarBrandByName(newCarBrand.getName())).thenReturn(newCarBrand);

        for (var carBrand : testDataProvider.getCarBrands()) {
            when(carService.getCarBrandByName(carBrand.getName())).thenReturn(carBrand);
        }
    }

    @Test
    public void testConvert() {

        for (var carBrand : testDataProvider.getCarBrands()) {
            String source = carBrand.getName();
            assertEquals(carBrandConverter.convert(source), carBrand);
            verify(carService, times(1)).getCarBrandByName(carBrand.getName());
        }

        String source = newCarBrand.getName();
        assertEquals(carBrandConverter.convert(source), newCarBrand);
        verify(carService, times(1)).getCarBrandByName(newCarBrand.getName());
    }
}