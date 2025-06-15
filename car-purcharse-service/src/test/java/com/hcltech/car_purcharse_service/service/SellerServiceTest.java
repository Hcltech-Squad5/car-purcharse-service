package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dao.service.SellerDaoService;
import com.hcltech.car_purcharse_service.dao.service.UserDaoService;
import com.hcltech.car_purcharse_service.dto.SellerDto;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerDaoService sellerDaoService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserDaoService userDaoService;

    @InjectMocks
    private SellerService sellerService;

    private SellerDto sellerDto;
    private Seller seller;
    private User user; // This represents the User associated with the seller in the DB

    @BeforeEach
    void setUp() {
        sellerDto = new SellerDto();
        sellerDto.setId(1);
        sellerDto.setEmail("test@example.com");
        sellerDto.setPassword("password123");
        sellerDto.setName("Test Seller");
        sellerDto.setContact("1234567890"); // Added contact as per SellerDto

        seller = new Seller();
        seller.setId(1);
        seller.setEmail("test@example.com");
        seller.setName("Test Seller");
        seller.setContact("1234567890"); // Added contact

        user = new User();
        user.setId(101); // Assuming a user ID
        user.setUserName("test@example.com");
        user.setPassword("encodedPassword123"); // Password is usually encoded in the User model
        user.setRoles("SELLER");
    }

    @Test
    @DisplayName("Test saveSeller - Success")
    void testSaveSeller_Success() {
        // Prepare the SellerDto that will be returned by the service, with password nullified
        SellerDto expectedSavedSellerDto = new SellerDto();
        expectedSavedSellerDto.setId(sellerDto.getId());
        expectedSavedSellerDto.setEmail(sellerDto.getEmail());
        expectedSavedSellerDto.setName(sellerDto.getName());
        expectedSavedSellerDto.setContact(sellerDto.getContact());
        expectedSavedSellerDto.setPassword(null); // As per SellerService logic

        // Mocking behavior
        when(modelMapper.map(sellerDto, Seller.class)).thenReturn(seller);
        when(sellerDaoService.saveSeller(seller)).thenReturn(seller);
        // Mock the mapping from Seller entity back to SellerDto for the return value
        when(modelMapper.map(seller, SellerDto.class)).thenReturn(expectedSavedSellerDto);

        // Call the service method
        SellerDto result = sellerService.saveSeller(sellerDto);

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(sellerDto.getEmail(), result.getEmail());
        assertEquals(sellerDto.getName(), result.getName());
        assertNull(result.getPassword(), "Password should be nullified in the returned DTO");
        assertEquals(expectedSavedSellerDto, result); // Check the entire DTO content

        verify(userDaoService, times(1)).createUser(sellerDto.getEmail(), sellerDto.getPassword(), "SELLER");
        verify(sellerDaoService, times(1)).saveSeller(seller);
        verify(modelMapper, times(1)).map(sellerDto, Seller.class);
        verify(modelMapper, times(1)).map(seller, SellerDto.class);
    }

    @Test
    @DisplayName("Test findSellerById - Success")
    void testFindSellerById_Success() {
        // Prepare the SellerDto expected to be returned
        SellerDto expectedSellerDto = new SellerDto();
        expectedSellerDto.setId(seller.getId());
        expectedSellerDto.setEmail(seller.getEmail());
        expectedSellerDto.setName(seller.getName());
        expectedSellerDto.setContact(seller.getContact());
        expectedSellerDto.setPassword(null); // Assuming password is nullified when fetched/returned

        // Mocking behavior
        when(sellerDaoService.findSellerById(1)).thenReturn(Optional.of(seller));
        when(modelMapper.map(seller, SellerDto.class)).thenReturn(expectedSellerDto);

        // Call the service method
        SellerDto result = sellerService.findSellerById(1);

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(expectedSellerDto.getId(), result.getId());
        assertEquals(expectedSellerDto.getEmail(), result.getEmail());
        assertEquals(expectedSellerDto, result); // Check the entire DTO content
        verify(sellerDaoService, times(1)).findSellerById(1);
        verify(modelMapper, times(1)).map(seller, SellerDto.class);
    }

    @Test
    @DisplayName("Test findSellerById - Not Found")
    void testFindSellerById_NotFound() {
        // Mocking behavior
        when(sellerDaoService.findSellerById(anyInt())).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        assertThrows(UsernameNotFoundException.class, () -> sellerService.findSellerById(99));

        // Verify interactions
        verify(sellerDaoService, times(1)).findSellerById(99);
        verify(modelMapper, never()).map(any(Seller.class), any(Class.class));
    }

    @Test
    @DisplayName("Test findAllSeller - Success")
    void testFindAllSeller_Success() {
        // Prepare test data
        Seller seller2 = new Seller();
        seller2.setId(2);
        seller2.setEmail("test2@example.com");
        seller2.setName("Test Seller 2");
        seller2.setContact("9876543210");

        SellerDto sellerDto2 = new SellerDto();
        sellerDto2.setId(2);
        sellerDto2.setEmail("test2@example.com");
        sellerDto2.setName("Test Seller 2");
        sellerDto2.setContact("9876543210");
        sellerDto2.setPassword(null); // Expected for returned DTOs

        List<Seller> sellers = Arrays.asList(seller, seller2); // 'seller' from setUp
        List<SellerDto> expectedSellerDtos = Arrays.asList(
                new SellerDto(seller.getId(), seller.getName(), seller.getEmail(), seller.getContact(), null), // Password null for returned DTO
                sellerDto2
        );

        // Mocking behavior
        when(sellerDaoService.findAllSeller()).thenReturn(sellers);
        when(modelMapper.map(seller, SellerDto.class)).thenReturn(expectedSellerDtos.get(0));
        when(modelMapper.map(seller2, SellerDto.class)).thenReturn(expectedSellerDtos.get(1));

        // Call the service method
        List<SellerDto> result = sellerService.findAllSeller();

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedSellerDtos.get(0).getEmail(), result.get(0).getEmail());
        assertEquals(expectedSellerDtos.get(1).getEmail(), result.get(1).getEmail());
        assertEquals(expectedSellerDtos, result); // Check the entire list content

        verify(sellerDaoService, times(1)).findAllSeller();
        verify(modelMapper, times(sellers.size())).map(any(Seller.class), eq(SellerDto.class));
    }


    @Test
    @DisplayName("Test updateSeller - Success")
    void testUpdateSeller_Success() {
        // DTO as input for the update method
        SellerDto updatedSellerDtoInput = new SellerDto();
        updatedSellerDtoInput.setId(1);
        updatedSellerDtoInput.setEmail("updated@example.com");
        updatedSellerDtoInput.setPassword("newpassword"); // Plain text password from client
        updatedSellerDtoInput.setName("Updated Seller");
        updatedSellerDtoInput.setContact("1122334455");

        // Seller entity that would be returned by sellerDaoService.updateSeller()
        Seller updatedSellerEntity = new Seller();
        updatedSellerEntity.setId(1);
        updatedSellerEntity.setEmail("updated@example.com");
        updatedSellerEntity.setName("Updated Seller");
        updatedSellerEntity.setContact("1122334455");


        // User object that userDaoService.getByUserName would return for the *existing* seller
        // This 'user' object is from setUp, but let's make a specific instance if needed for clarity
        User existingUserForUpdate = new User();
        existingUserForUpdate.setId(user.getId());
        existingUserForUpdate.setUserName(seller.getEmail()); // Original email
        existingUserForUpdate.setPassword(user.getPassword()); // Original encoded password
        existingUserForUpdate.setRoles(user.getRoles());


        // User object that userDaoService.updateUser would *return* after updating
        // The password here should be encoded, as updateUser method in UserDaoService encodes it.
        User updatedUserResult = new User();
        updatedUserResult.setId(existingUserForUpdate.getId());
        updatedUserResult.setUserName(updatedSellerDtoInput.getEmail()); // New username
        updatedUserResult.setPassword("some_encoded_new_password_placeholder"); // Encoded by PasswordEncoder in UserDaoService
        updatedUserResult.setRoles(existingUserForUpdate.getRoles());


        // DTO that modelMapper.map(updatedSellerEntity, SellerDto.class) should return
        // Password should be nullified for security in the returned DTO
        SellerDto expectedReturnedSellerDto = new SellerDto();
        expectedReturnedSellerDto.setId(updatedSellerDtoInput.getId());
        expectedReturnedSellerDto.setEmail(updatedSellerDtoInput.getEmail());
        expectedReturnedSellerDto.setPassword(null); // Password should be null for returned DTO
        expectedReturnedSellerDto.setName(updatedSellerDtoInput.getName());
        expectedReturnedSellerDto.setContact(updatedSellerDtoInput.getContact());


        // --- Mocking behavior ---
        // 1. SellerDaoService finds the existing seller
        when(sellerDaoService.findSellerById(1)).thenReturn(Optional.of(seller));

        // 2. UserDaoService gets the existing user by the seller's original email
        when(userDaoService.getByUserName(seller.getEmail())).thenReturn(existingUserForUpdate);

        // 3. UserDaoService updates the user (THE CORRECTED MOCK)
        // We expect it to be called with the existing user object, the new email, and the plain text password
        // It should return the 'updatedUserResult' mock object.
        when(userDaoService.updateUser(eq(existingUserForUpdate), eq(updatedSellerDtoInput.getEmail()), eq(updatedSellerDtoInput.getPassword())))
                .thenReturn(updatedUserResult); // Corrected: return a User object


        // 4. ModelMapper maps the incoming SellerDto to a Seller entity for sellerDaoService
        when(modelMapper.map(updatedSellerDtoInput, Seller.class)).thenReturn(updatedSellerEntity);

        // 5. SellerDaoService updates the seller entity and returns it
        when(sellerDaoService.updateSeller(updatedSellerEntity)).thenReturn(updatedSellerEntity);

        // 6. ModelMapper maps the updated Seller entity back to a SellerDto for the service's return value
        when(modelMapper.map(updatedSellerEntity, SellerDto.class)).thenReturn(expectedReturnedSellerDto);


        // --- Call the service method ---
        SellerDto result = sellerService.updateSeller(updatedSellerDtoInput, 1);

        // --- Verify interactions and assertions ---
        assertNotNull(result);
        assertEquals(expectedReturnedSellerDto.getEmail(), result.getEmail());
        assertEquals(expectedReturnedSellerDto.getName(), result.getName());
        assertNull(result.getPassword(), "Password should be null in the returned DTO after update");
        assertEquals(expectedReturnedSellerDto, result); // Verify entire returned DTO

        // Verify that the correct methods were called with the correct arguments
        verify(sellerDaoService, times(1)).findSellerById(1);
        verify(userDaoService, times(1)).getByUserName(seller.getEmail());
        verify(userDaoService, times(1)).updateUser(eq(existingUserForUpdate), eq(updatedSellerDtoInput.getEmail()), eq(updatedSellerDtoInput.getPassword()));
        verify(modelMapper, times(1)).map(updatedSellerDtoInput, Seller.class);
        verify(sellerDaoService, times(1)).updateSeller(updatedSellerEntity);
        verify(modelMapper, times(1)).map(updatedSellerEntity, SellerDto.class);
    }

    @Test
    @DisplayName("Test updateSeller - Seller Not Found")
    void testUpdateSeller_SellerNotFound() {
        // Mocking behavior
        when(sellerDaoService.findSellerById(anyInt())).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        assertThrows(UsernameNotFoundException.class, () -> sellerService.updateSeller(sellerDto, 99));

        // Verify interactions
        verify(sellerDaoService, times(1)).findSellerById(99);
        verify(userDaoService, never()).updateUser(any(User.class), anyString(), anyString());
        verify(modelMapper, never()).map(any(SellerDto.class), any(Class.class));
        verify(sellerDaoService, never()).updateSeller(any(Seller.class));
    }

    @Test
    @DisplayName("Test deleteSeller - Success")
    void testDeleteSeller_Success() {
        // Mocking behavior
        when(sellerDaoService.findSellerById(1)).thenReturn(Optional.of(seller));
        doNothing().when(userDaoService).deleteByUserName(seller.getEmail());
        doNothing().when(sellerDaoService).deleteSellerById(1);

        // Call the service method
        sellerService.deleteSeller(1);

        // Verify interactions
        verify(sellerDaoService, times(1)).findSellerById(1);
        verify(userDaoService, times(1)).deleteByUserName(seller.getEmail());
        verify(sellerDaoService, times(1)).deleteSellerById(1);
    }

    @Test
    @DisplayName("Test deleteSeller - Seller Not Found")
    void testDeleteSeller_SellerNotFound() {
        // Mocking behavior
        when(sellerDaoService.findSellerById(anyInt())).thenReturn(Optional.empty());

        // Call the service method and expect an exception
        assertThrows(UsernameNotFoundException.class, () -> sellerService.deleteSeller(99));

        // Verify interactions
        verify(sellerDaoService, times(1)).findSellerById(99);
        verify(userDaoService, never()).deleteByUserName(anyString());
        verify(sellerDaoService, never()).deleteSellerById(anyInt());
    }
}