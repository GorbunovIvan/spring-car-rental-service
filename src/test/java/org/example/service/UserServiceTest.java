package org.example.service;

import org.example.TestDataProvider;
import org.example.config.TestConfig;
import org.example.repository.UserRepository;
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
import static org.testng.Assert.assertNull;

@ContextConfiguration(classes = TestConfig.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private TestDataProvider testDataProvider;

    @BeforeMethod
    public void setUp() {

        this.testDataProvider = new TestDataProvider();

        if (userRepository == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(userRepository);
        }

        when(userRepository.findByIdEagerly(-1L)).thenReturn(Optional.empty());

        for (var user : testDataProvider.getUsers()) {
            when(userRepository.findByIdEagerly(user.getId())).thenReturn(Optional.of(user));
        }
    }

    @Test
    public void testGetByIdEagerly() {

        for (var user : testDataProvider.getUsers()) {
            var userResult = userService.getByIdEagerly(user.getId());
            assertEquals(userResult, user);
            verify(userRepository, times(1)).findByIdEagerly(user.getId());
        }

        assertNull(userService.getByIdEagerly(-1L));
    }
}