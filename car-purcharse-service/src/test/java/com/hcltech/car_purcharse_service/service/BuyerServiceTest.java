package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dto.BuyerDto;
import com.hcltech.car_purcharse_service.model.Buyer;
import com.hcltech.car_purcharse_service.model.User;
import com.hcltech.car_purcharse_service.repository.BuyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BuyerServiceTest {

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BuyerService buyerService;

    private Buyer buyer;
    private BuyerDto buyerDto;
    private BuyerDto buyerDtoNoPassword;

    @BeforeEach
    void setUp() {
        buyer = new Buyer();
        buyer.setId(1);
        buyer.setFirstName("Test");
        buyer.setLastName("Buyer");
        buyer.setEmail("test.buyer@example.com");
        buyer.setPhoneNumber("9876543210");

        buyerDto = new BuyerDto();
        buyerDto.setId(1);
        buyerDto.setFirstName("Test");
        buyerDto.setLastName("Buyer");
        buyerDto.setEmail("test.buyer@example.com");
        buyerDto.setPhoneNumber("9876543210");
        buyerDto.setPassword("securePassword123");

        buyerDtoNoPassword = new BuyerDto(1, "Test", "Buyer", "test.buyer@example.com", "9876543210");

        lenient().when(modelMapper.map(any(BuyerDto.class), eq(Buyer.class))).thenReturn(buyer);

        lenient().doAnswer(invocation -> {
            Buyer sourceBuyer = invocation.getArgument(0);
            Class<BuyerDto> destinationType = invocation.getArgument(1);
            BuyerDto resultDto = new BuyerDto();
            resultDto.setId(sourceBuyer.getId());
            resultDto.setFirstName(sourceBuyer.getFirstName());
            resultDto.setLastName(sourceBuyer.getLastName());
            resultDto.setEmail(sourceBuyer.getEmail());
            resultDto.setPhoneNumber(sourceBuyer.getPhoneNumber());
            resultDto.setPassword(null);
            return resultDto;
        }).when(modelMapper).map(any(Buyer.class), eq(BuyerDto.class));

        lenient().doAnswer(invocation -> {
            BuyerDto source = invocation.getArgument(0);
            Buyer destination = invocation.getArgument(1);
            destination.setFirstName(source.getFirstName());
            destination.setLastName(source.getLastName());
            destination.setEmail(source.getEmail());
            destination.setPhoneNumber(source.getPhoneNumber());
            return null;
        }).when(modelMapper).map(any(BuyerDto.class), any(Buyer.class));
    }

    @Test
    @DisplayName("Should create a buyer successfully")
    void shouldCreateBuyerSuccessfully() {
        User createdUser = new User();
        createdUser.setId(101);
        createdUser.setUserName(buyerDto.getEmail());
        createdUser.setRoles("BUYER");

        when(userService.create(any(User.class))).thenReturn(createdUser);
        when(buyerRepository.save(any(Buyer.class))).thenReturn(buyer);

        BuyerDto createdBuyerDto = buyerService.createBuyer(buyerDto);

        assertThat(createdBuyerDto).isNotNull();
        assertThat(createdBuyerDto.getId()).isEqualTo(1);
        assertThat(createdBuyerDto.getFirstName()).isEqualTo("Test");
        assertThat(createdBuyerDto.getEmail()).isEqualTo("test.buyer@example.com");
        assertThat(createdBuyerDto.getPassword()).isNull();

        verify(userService, times(1)).create(any(User.class));
        verify(buyerRepository, times(1)).save(any(Buyer.class));
        verify(modelMapper, times(1)).map(eq(buyerDto), eq(Buyer.class));
        verify(modelMapper, times(1)).map(eq(buyer), eq(BuyerDto.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating buyer without password")
    void shouldThrowExceptionWhenCreatingBuyerWithoutPassword() {
        buyerDto.setPassword(null);
        assertThrows(IllegalArgumentException.class, () -> buyerService.createBuyer(buyerDto));
        verify(userService, never()).create(any(User.class));
        verify(buyerRepository, never()).save(any(Buyer.class));
        verify(modelMapper, never()).map(any(BuyerDto.class), eq(Buyer.class));
    }

    @Test
    @DisplayName("Should retrieve buyer by ID successfully")
    void shouldGetBuyerByIdSuccessfully() {
        when(buyerRepository.findById(1)).thenReturn(Optional.of(buyer));

        BuyerDto foundDto = buyerService.getBuyerById(1);

        assertThat(foundDto).isNotNull();
        assertThat(foundDto.getId()).isEqualTo(1);
        assertThat(foundDto.getFirstName()).isEqualTo("Test");
        assertThat(foundDto.getEmail()).isEqualTo("test.buyer@example.com");
        assertThat(foundDto.getPassword()).isNull();

        verify(buyerRepository, times(1)).findById(1);
        verify(modelMapper, times(1)).map(eq(buyer), eq(BuyerDto.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when buyer not found by ID")
    void shouldThrowExceptionWhenBuyerNotFoundById() {
        when(buyerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> buyerService.getBuyerById(99));
        verify(buyerRepository, times(1)).findById(99);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("Should retrieve all buyers")
    void shouldGetAllBuyers() {
        Buyer buyer2 = new Buyer();
        buyer2.setId(2);
        buyer2.setFirstName("Another");
        buyer2.setLastName("One");
        buyer2.setEmail("another@example.com");
        buyer2.setPhoneNumber("1231231234");

        when(buyerRepository.findAll()).thenReturn(Arrays.asList(buyer, buyer2));

        List<BuyerDto> buyerDtos = buyerService.getAllBuyers();

        assertThat(buyerDtos).isNotNull();
        assertThat(buyerDtos).hasSize(2);
        assertThat(buyerDtos).extracting(BuyerDto::getFirstName).containsExactlyInAnyOrder("Test", "Another");
        assertThat(buyerDtos).extracting(BuyerDto::getPassword).containsOnlyNulls();

        verify(buyerRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(eq(buyer), eq(BuyerDto.class));
        verify(modelMapper, times(1)).map(eq(buyer2), eq(BuyerDto.class));
    }

    @Test
    @DisplayName("Should update a buyer successfully")
    void shouldUpdateBuyerSuccessfully() {
        BuyerDto updatedBuyerDto = new BuyerDto();
        updatedBuyerDto.setFirstName("UpdatedTest");
        updatedBuyerDto.setLastName("UpdatedBuyer");
        updatedBuyerDto.setEmail("updated.test@example.com");
        updatedBuyerDto.setPhoneNumber("5554443333");

        when(buyerRepository.findById(1)).thenReturn(Optional.of(buyer));
        when(buyerRepository.save(any(Buyer.class))).thenReturn(buyer);

        BuyerDto resultDto = buyerService.updateBuyer(1, updatedBuyerDto);

        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(1);
        assertThat(resultDto.getFirstName()).isEqualTo("UpdatedTest");
        assertThat(resultDto.getEmail()).isEqualTo("updated.test@example.com");
        assertThat(resultDto.getPassword()).isNull();

        verify(buyerRepository, times(1)).findById(1);
        verify(buyerRepository, times(1)).save(argThat(b ->
                b.getFirstName().equals("UpdatedTest") &&
                        b.getEmail().equals("updated.test@example.com")
        ));
        verify(modelMapper, times(1)).map(eq(updatedBuyerDto), eq(buyer));
        verify(modelMapper, times(1)).map(eq(buyer), eq(BuyerDto.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when updating non-existent buyer")
    void shouldThrowExceptionWhenUpdatingNonExistentBuyer() {
        when(buyerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> buyerService.updateBuyer(99, buyerDto));
        verify(buyerRepository, times(1)).findById(99);
        verify(buyerRepository, never()).save(any(Buyer.class));
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("Should delete a buyer successfully")
    void shouldDeleteBuyerSuccessfully() {
        when(buyerRepository.existsById(1)).thenReturn(true);
        doNothing().when(buyerRepository).deleteById(1);

        buyerService.deleteBuyer(1);

        verify(buyerRepository, times(1)).existsById(1);
        verify(buyerRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should retrieve user credentials (placeholder)")
    void shouldGetUserCredentials() {
        when(buyerRepository.findById(1)).thenReturn(Optional.of(buyer));

        String expectedPassword = "PlaceholderSecurePassword123!";
        Map<String, String> actualCredentials = buyerService.getUserCredentials(1);

        assertThat(actualCredentials).isNotNull();
        assertThat(actualCredentials).containsKey("password");
        assertThat(actualCredentials.get("password")).isEqualTo(expectedPassword);
        verify(buyerRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw RuntimeException when getting credentials for non-existent buyer")
    void shouldThrowExceptionWhenGettingCredentialsForNonExistentBuyer() {
        when(buyerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> buyerService.getUserCredentials(99));
        verify(buyerRepository, times(1)).findById(99);
    }
}