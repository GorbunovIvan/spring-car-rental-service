package org.example.service;

import org.example.TestDataProvider;
import org.example.config.SpringTestConfig;
import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.repository.CarRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@ContextConfiguration(classes = SpringTestConfig.class)
public class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    private TestDataProvider testDataProvider;

    @BeforeMethod
    public void setUp() {

        this.testDataProvider = new TestDataProvider();

        if (carRepository == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(carRepository);
        }

        when(carRepository.findAll()).thenReturn(testDataProvider.getCars());
        when(carRepository.findAllByLessor(null)).thenReturn(Collections.emptyList());
        when(carRepository.findModelByMainFields("", "", 0)).thenReturn(Optional.empty());
        when(carRepository.findAllCarBrands()).thenReturn(testDataProvider.getCarBrands());
        when(carRepository.findCarBrandByName("")).thenReturn(Optional.empty());

        for (var lessor : testDataProvider.getLessors()) {
            when(carRepository.findAllByLessor(lessor)).thenReturn(lessor.getCars());
        }

        for (var model : testDataProvider.getModels()) {
            when(carRepository.findAllByModel(model)).thenReturn(model.getCars());
            when(carRepository.findModelByMainFields(model.getCarBrand().getName(), model.getModelName(), model.getYear())).thenReturn(Optional.of(model));
        }

        for (var carBrand : testDataProvider.getCarBrands()) {
            when(carRepository.findCarBrandByName(carBrand.getName())).thenReturn(Optional.of(carBrand));
        }
    }

    @Test
    public void testGetAll() {
        assertEquals(carService.getAll(), testDataProvider.getCars());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllByLessor() {

        for (var lessor : testDataProvider.getLessors()) {
            assertEquals(carService.getAllByLessor(lessor), lessor.getCars());
            verify(carRepository, times(1)).findAllByLessor(lessor);
        }

        assertTrue(carService.getAllByLessor(null).isEmpty());
    }

    @Test
    public void testGetAllNamesByLessor() {

        for (var lessor : testDataProvider.getLessors()) {
            var namesExpected = lessor.getCars().stream().map(Car::getFullName).toList();
            assertEquals(carService.getAllNamesByLessor(lessor), namesExpected);
            verify(carRepository, times(1)).findAllByLessor(lessor);
        }

        assertTrue(carService.getAllNamesByLessor(null).isEmpty());
    }

    @Test
    public void testGetByFields() {

        for (var car : testDataProvider.getCars()) {
            var model = car.getModel();
            var carResult = carService.getByFields(model.getCarBrand().getName(), model.getModelName(), model.getYear(), car.getLessor().getName());
            assertEquals(carResult, car);
            verify(carRepository, times(1)).findModelByMainFields(model.getCarBrand().getName(), model.getModelName(), model.getYear());
        }

        assertNull(carService.getByFields("", "", 0, ""));
    }

    @Test
    public void testGetCarBrandByName() {

        for (var carBrand : testDataProvider.getCarBrands()) {
            assertEquals(carService.getCarBrandByName(carBrand.getName()), carBrand);
            verify(carRepository, times(1)).findCarBrandByName(carBrand.getName());
        }

        assertNull(carService.getCarBrandByName(""));
    }

    @Test
    public void testGetAllCarBrands() {
        assertEquals(carService.getAllCarBrands(), testDataProvider.getCarBrands());
        verify(carRepository, times(1)).findAllCarBrands();
    }

    @Test
    public void testGetAllCarBrandNames() {
        assertEquals(carService.getAllCarBrandNames(), testDataProvider.getCarBrands().stream().map(CarBrand::getName).toList());
        verify(carRepository, times(1)).findAllCarBrands();
    }
}