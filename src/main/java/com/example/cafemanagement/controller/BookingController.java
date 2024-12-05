package com.example.cafemanagement.controller;

import com.example.cafemanagement.dto.BookingDto;
import com.example.cafemanagement.dto.BookingSaveRequestDto;
import com.example.cafemanagement.repository.UserRepository;
import com.example.cafemanagement.service.BookingService;
import com.example.cafemanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Tag(name = "예약 관리", description = "예약 생성, 취소, 조회 API")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 생성 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 요청 데이터")
    })
    public ResponseEntity<String> createBooking(@RequestBody BookingSaveRequestDto dto, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String username = authentication.getName();
        // 사용자 ID를 username을 통해 조회 (사용자 서비스 필요)
        Long userId = userRepository.findByUsername(username).get().getId();

        dto.setUserId(userId); // 예약 DTO에 사용자 ID 설정
        Long bookingId = bookingService.createBooking(dto);
        return ResponseEntity.ok("예약이 성공적으로 생성되었습니다. ID: " + bookingId);
    }


    @DeleteMapping("/{bookingId}")
    @Operation(summary = "예약 취소", description = "특정 예약을 취소합니다.")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("예약이 취소되었습니다.");
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자 예약 조회", description = "특정 사용자의 예약 목록을 조회합니다.")
    public ResponseEntity<List<BookingDto>> findBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.findBookingsByUser(userId));
    }

    @PutMapping("/{bookingId}/status")
    @Operation(summary = "예약 상태 변경", description = "특정 예약의 상태를 업데이트합니다.")
    public ResponseEntity<String> updatePickupStatus(@PathVariable Long bookingId, @RequestParam String status) {
        bookingService.updatePickupStatus(bookingId, status);
        return ResponseEntity.ok("예약 상태가 업데이트되었습니다.");
    }
}
