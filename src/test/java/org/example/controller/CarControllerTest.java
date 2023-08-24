package org.example.controller;

import org.example.TestDataProvider;
import org.example.config.TestConfig;
import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.entity.car.Model;
import org.example.entity.user.User;
import org.example.service.CarService;
import org.example.service.ImageService;
import org.example.utils.UsersUtil;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testng.Assert.assertFalse;

@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
public class CarControllerTest extends AbstractTestNGSpringContextTests {

    private MockMvc mvc;

    @Mock
    private CarService carService;
    @Mock
    private ImageService imageService;
    @Mock
    private UsersUtil usersUtil;

    private TestDataProvider testDataProvider;

    private Car newCar;
    private User currentUserLessor;

    @BeforeMethod
    public void setUp() {

        testDataProvider = new TestDataProvider();

        if (carService == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(carService, imageService, usersUtil);
        }

        mvc = MockMvcBuilders
                .standaloneSetup(new CarController(carService, imageService, usersUtil))
                .build();

        var newModel = new Model(4L, testDataProvider.getCarBrands().get(0), "test new model", 1999, 99, new ArrayList<>());
        newCar = new Car(4L, newModel, testDataProvider.getLessors().get(0), new ArrayList<>(), new ArrayList<>());

        currentUserLessor = testDataProvider.getLessors().get(0).getUser();

        when(carService.getAll()).thenReturn(testDataProvider.getCars());
        when(carService.getById(-1L)).thenReturn(null);
        when(carService.create(newCar)).thenReturn(newCar);
        when(carService.update(-1L, null)).thenThrow(RuntimeException.class);
        when(carService.getAllCarBrandNames()).thenReturn(testDataProvider.getCarBrands().stream().map(CarBrand::getName).toList());

        for (var car : testDataProvider.getCars()) {
            when(carService.getById(car.getId())).thenReturn(car);
            when(carService.update(car.getId(), car)).thenReturn(car);
            when(imageService.getAllByCar(car)).thenReturn(car.getImages());
        }
    }

    @Test
    public void testGetAll() throws Exception {

        mvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/cars"))
                .andExpect(model().attribute("cars", testDataProvider.getCars()));

        verify(carService, times(1)).getAll();
        verify(usersUtil, times(1)).getCurrentUser();
    }

    @Test
    public void testGetById() throws Exception {

        var cars = testDataProvider.getCars();
        assertFalse(cars.isEmpty());

        for (var car : cars) {

            mvc.perform(get("/cars/{id}", car.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cars/car"))
                    .andExpect(model().attribute("car", car))
                    .andExpect(model().attribute("carImages", car.getImages()));

            verify(carService, times(1)).getById(car.getId());
            verify(imageService, times(1)).getAllByCar(car);
        }

        verify(usersUtil, times(cars.size())).getCurrentUser();

        mvc.perform(get("/cars/{id}", -1L))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/cars"));
    }

    @Test
    public void testCreateForm() throws Exception {

        // unauthorized
        mvc.perform(get("/cars/new"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/auth/login"));

        verify(usersUtil, times(1)).getCurrentUser();
        verify(carService, never()).getAllCarBrandNames();

        // authorized as lesser
        when(usersUtil.getCurrentUser()).thenReturn(currentUserLessor);
        mvc.perform(get("/cars/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/new"))
                .andExpect(model().attribute("car", new Car()))
                .andExpect(model().attribute("brands", testDataProvider.getCarBrands().stream().map(CarBrand::getName).toList()));

        verify(usersUtil, times(2)).getCurrentUser();
        verify(carService, times(1)).getAllCarBrandNames();
    }

    @Test
    public void testCreate() throws Exception {

        // unauthorized
        mvc.perform(post("/cars")
                        .param("brand", newCar.getModel().getCarBrand().getName())
                        .param("model", newCar.getModel().getModelName())
                        .param("horsePowers", String.valueOf(newCar.getModel().getHorsePowers()))
                        .param("year", String.valueOf(newCar.getModel().getYear())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/auth/login"));

        verify(usersUtil, times(1)).getCurrentUser();
        verify(carService, never()).create(any(Car.class));

        // authorized as lessor
        when(usersUtil.getCurrentUser()).thenReturn(currentUserLessor);

        // error
        mvc.perform(post("/cars")
                        .param("brand", "")
                        .param("model", "")
                        .param("horsePowers", "")
                        .param("year", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/new"));

        // is ok
        mvc.perform(post("/cars")
                        .param("brand", newCar.getModel().getCarBrand().getName())
                        .param("model", newCar.getModel().getModelName())
                        .param("horsePowers", String.valueOf(newCar.getModel().getHorsePowers()))
                        .param("year", String.valueOf(newCar.getModel().getYear())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/users/my-page"));

        verify(usersUtil, times(3)).getCurrentUser();
        verify(carService, times(1)).create(any(Car.class));
        verify(imageService, times(1)).addImagesToCar(any(Car.class), any());
    }

    @Test
    public void testUpdateForm() throws Exception {

        // unauthorized
        mvc.perform(get("/cars/{id}/edit", -1L))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/auth/login"));

        verify(usersUtil, times(1)).getCurrentUser();
        verify(carService, never()).getAllCarBrandNames();

        // authorized as lesser
        when(usersUtil.getCurrentUser()).thenReturn(currentUserLessor);

        var cars = currentUserLessor.getLessor().getCars();
        assertFalse(cars.isEmpty());

        for (var car : cars) {

            mvc.perform(get("/cars/{id}/edit", car.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cars/edit"))
                    .andExpect(model().attribute("car", car))
                    .andExpect(model().attribute("carImages", car.getImages()))
                    .andExpect(model().attribute("brands", testDataProvider.getCarBrands().stream().map(CarBrand::getName).toList()));

            verify(carService, times(1)).getById(car.getId());
            verify(imageService, times(1)).getAllByCar(car);
        }

        verify(usersUtil, times(cars.size() + 1)).getCurrentUser();
        verify(carService, times(cars.size())).getAllCarBrandNames();
    }

    @Test
    public void testUpdate() throws Exception {

        // unauthorized
        mvc.perform(patch("/cars/{id}", -1L))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/auth/login"));

        verify(usersUtil, times(1)).getCurrentUser();
        verify(carService, never()).update(anyLong(), any(Car.class));

        // authorized as lessor
        when(usersUtil.getCurrentUser()).thenReturn(currentUserLessor);

        var cars = currentUserLessor.getLessor().getCars();
        assertFalse(cars.isEmpty());

        for (var car : cars) {

            mvc.perform(patch("/cars/{id}", car.getId())
                        .param("brand", car.getModel().getCarBrand().getName())
                        .param("model", car.getModel().getModelName())
                        .param("horsePowers", String.valueOf(car.getModel().getHorsePowers()))
                        .param("year", String.valueOf(car.getModel().getYear())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/cars/" + car.getId()));

            verify(carService, times(1)).getById(car.getId());
            verify(carService, times(1)).update(car.getId(), car);
        }

        verify(usersUtil, times(cars.size() + 1)).getCurrentUser();
        verify(imageService, times(cars.size())).addImagesToCar(any(Car.class), any());
    }
}