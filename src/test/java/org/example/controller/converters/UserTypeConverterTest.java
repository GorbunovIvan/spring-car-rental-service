package org.example.controller.converters;

import org.example.config.TestConfig;
import org.example.entity.user.UserType;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@ContextConfiguration(classes = TestConfig.class)
public class UserTypeConverterTest {

    @InjectMocks
    private UserTypeConverter userTypeConverter;

    @BeforeMethod
    public void setUp() {
        if (userTypeConverter == null) {
            MockitoAnnotations.openMocks(this);
        }
    }

    @Test
    public void testConvert() {
        for (var userType : UserType.values()) {
            String source = userType.getName();
            assertEquals(userTypeConverter.convert(source), userType);
        }
    }
}