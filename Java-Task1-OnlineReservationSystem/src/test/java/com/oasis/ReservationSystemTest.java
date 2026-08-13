package com.oasis;

import com.oasis.config.DatabaseConnection;
import com.oasis.model.Reservation;
import com.oasis.model.Train;
import com.oasis.model.User;
import com.oasis.service.AuthenticationService;
import com.oasis.service.ReservationService;
import com.oasis.util.ValidationUtil;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReservationSystemTest {

    private static AuthenticationService authService;
    private static ReservationService reservationService;
    
    private static User adminUser;
    private static User normalUser;
    private static User tanujUser;
    
    private static String normalUserPnr;
    private static String tanujUserPnr;

    @BeforeAll
    static void setUp() {
        authService = new AuthenticationService();
        reservationService = new ReservationService();
        assertTrue(DatabaseConnection.testConnection(), "Database connection must be valid");

        adminUser = authService.login("admin", "admin123");
        normalUser = authService.login("user", "user123");
        tanujUser = authService.login("tanuj", "tanuj123");

        assertNotNull(adminUser, "Admin login failed");
        assertNotNull(normalUser, "Normal user login failed");
        assertNotNull(tanujUser, "Tanuj user login failed");

        assertEquals("ADMIN", adminUser.getRole());
        assertEquals("USER", normalUser.getRole());
        assertEquals("USER", tanujUser.getRole());
    }

    @Test
    @Order(1)
    @DisplayName("TEST 1: Invalid username/password should fail authentication")
    void testInvalidLogin() {
        User user = authService.login("invalid_user", "wrong_pass");
        assertNull(user, "User authentication should fail for invalid credentials");
    }

    @Test
    @Order(2)
    @DisplayName("TEST 2: Dynamic train list loading from MySQL trains table")
    void testGetAllTrainsFromDatabase() {
        List<Train> trains = reservationService.getAllTrains();
        assertNotNull(trains, "Train list from database should not be null");
        assertTrue(trains.size() > 0, "Train list should contain trains inserted by schema.sql");
    }

    @Test
    @Order(3)
    @DisplayName("TEST 3: Book ticket for normalUser ('user') and verify user_id linkage")
    void testBookTicketNormalUser() {
        String futureDate = LocalDate.now().plusDays(20).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Reservation res = reservationService.bookTicket(
                "Passenger User",
                "12301",
                "First AC",
                futureDate,
                "Howrah Junction (HWH)",
                "New Delhi (NDLS)",
                normalUser
        );

        assertNotNull(res, "Reservation should not be null");
        assertNotNull(res.getPnr(), "PNR should be generated");
        assertEquals(normalUser.getId(), res.getUserId(), "Reservation must link to logged-in user's database ID");
        normalUserPnr = res.getPnr();
    }

    @Test
    @Order(4)
    @DisplayName("TEST 4: Book ticket for tanujUser ('tanuj')")
    void testBookTicketTanujUser() {
        String futureDate = LocalDate.now().plusDays(25).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Reservation res = reservationService.bookTicket(
                "Tanuj Pratap Singh",
                "12951",
                "Executive Chair",
                futureDate,
                "Mumbai Central (MMCT)",
                "New Delhi (NDLS)",
                tanujUser
        );

        assertNotNull(res, "Reservation should not be null");
        assertEquals(tanujUser.getId(), res.getUserId(), "Reservation must link to tanujUser's ID");
        tanujUserPnr = res.getPnr();
    }

    @Test
    @Order(5)
    @DisplayName("TEST 5: Role-based list scoping - normalUser sees ONLY own bookings")
    void testViewReservationsUserScoping() {
        List<Reservation> userRes = reservationService.getReservationsForUser(normalUser);
        assertNotNull(userRes);
        assertTrue(userRes.size() > 0, "normalUser should see own reservations");

        for (Reservation r : userRes) {
            assertEquals(normalUser.getId(), r.getUserId(), "normalUser must NEVER see reservations of other users");
        }
    }

    @Test
    @Order(6)
    @DisplayName("TEST 6: Role-based list scoping - ADMIN sees ALL bookings across users")
    void testViewReservationsAdminScoping() {
        List<Reservation> adminRes = reservationService.getReservationsForUser(adminUser);
        assertNotNull(adminRes);
        assertTrue(adminRes.size() >= 2, "ADMIN should see all reservations across all users");

        boolean containsNormalUserPnr = adminRes.stream().anyMatch(r -> r.getPnr().equals(normalUserPnr));
        boolean containsTanujUserPnr = adminRes.stream().anyMatch(r -> r.getPnr().equals(tanujUserPnr));

        assertTrue(containsNormalUserPnr, "ADMIN must see normalUser's reservation");
        assertTrue(containsTanujUserPnr, "ADMIN must see tanujUser's reservation");
    }

    @Test
    @Order(7)
    @DisplayName("TEST 7: PNR Fetch Ownership - USER cannot fetch another user's PNR")
    void testFetchBookingOwnershipEnforcement() {
        // normalUser trying to fetch tanujUser's PNR
        Reservation forbiddenRes = reservationService.getReservationByPnr(tanujUserPnr, normalUser);
        assertNull(forbiddenRes, "normalUser MUST NOT be able to fetch tanujUser's booking PNR");

        // normalUser fetching own PNR
        Reservation ownRes = reservationService.getReservationByPnr(normalUserPnr, normalUser);
        assertNotNull(ownRes, "normalUser should successfully fetch own PNR");

        // ADMIN fetching tanujUser's PNR
        Reservation adminFetchRes = reservationService.getReservationByPnr(tanujUserPnr, adminUser);
        assertNotNull(adminFetchRes, "ADMIN should be able to fetch any user's PNR");
    }

    @Test
    @Order(8)
    @DisplayName("TEST 8: Cancellation Ownership - USER cannot cancel another user's PNR")
    void testCancelBookingOwnershipEnforcement() {
        // normalUser attempting to cancel tanujUser's PNR
        boolean unauthorizedCancel = reservationService.cancelReservation(tanujUserPnr, normalUser);
        assertFalse(unauthorizedCancel, "normalUser MUST NOT be allowed to cancel another user's booking");

        // Verify tanujUser's PNR still exists in MySQL
        Reservation checkRes = reservationService.getReservationByPnr(tanujUserPnr, tanujUser);
        assertNotNull(checkRes, "tanujUser's booking must remain intact in database");

        // normalUser cancelling own PNR
        boolean ownCancel = reservationService.cancelReservation(normalUserPnr, normalUser);
        assertTrue(ownCancel, "normalUser should successfully cancel own booking");
    }

    @Test
    @Order(9)
    @DisplayName("TEST 9: ADMIN can cancel any user's PNR")
    void testAdminCancellation() {
        boolean adminCancel = reservationService.cancelReservation(tanujUserPnr, adminUser);
        assertTrue(adminCancel, "ADMIN should be able to cancel any user's booking");

        Reservation verifyDeleted = reservationService.getReservationByPnr(tanujUserPnr, adminUser);
        assertNull(verifyDeleted, "Cancelled booking should no longer exist in database");
    }
}
