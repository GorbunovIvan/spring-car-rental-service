package org.example.controller.converters;

import org.example.TestDataProvider;
import org.example.config.TestConfig;
import org.example.entity.car.Car;
import org.example.entity.car.Model;
import org.example.service.CarService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@ContextConfiguration(classes = TestConfig.class)
public class CarConverterTest {

    @InjectMocks
    private CarConverter carConverter;

    @Mock
    private CarService carService;

    private TestDataProvider testDataProvider;

    private Car newCar;

    @BeforeMethod
    public void setUp() {

        testDataProvider = new TestDataProvider();

        if (carService == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(carService);
        }

        var newModel = new Model(4L, testDataProvider.getCarBrands().get(0), "test new model", 1999, 99, new ArrayList<>());
        newCar = new Car(4L, newModel, testDataProvider.getLessors().get(0), new ArrayList<>(), new ArrayList<>());
        when(carService.getByFields(newModel.getCarBrand().getName(), newModel.getModelName(), newModel.getYear(), newCar.getLessor().getName())).thenReturn(newCar);

        for (var car : testDataProvider.getCars()) {
            when(carService.getByFields(car.getModel().getCarBrand().getName(), car.getModel().getModelName(), car.getModel().getYear(), car.getLessor().getName())).thenReturn(car);
        }
    }

    @Test
    public void testConvert() {

        for (var car : testDataProvider.getCars()) {
            String source = car.getFullName();
            assertEquals(carConverter.convert(source), car);
            verify(carService, times(1)).getByFields(car.getModel().getCarBrand().getName(), car.getModel().getModelName(), car.getModel().getYear(), car.getLessor().getName());
        }

        String source = newCar.getFullName();
        assertEquals(carConverter.convert(source), newCar);
        verify(carService, times(1)).getByFields(newCar.getModel().getCarBrand().getName(), newCar.getModel().getModelName(), newCar.getModel().getYear(), newCar.getLessor().getName());
    }
}