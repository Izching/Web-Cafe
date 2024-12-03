package com.example.cafemanagement.controller;

import com.example.cafemanagement.dto.CafeDto;
import com.example.cafemanagement.dto.CategoryDto;
import com.example.cafemanagement.service.CafeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MainController {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    private final CafeService cafeService;

    @Autowired
    public MainController(CafeService cafeService) {
        this.cafeService = cafeService;
    }

    // 메인 페이지 렌더링
    @GetMapping("/")
    public String showMainPage(Model model) throws JsonProcessingException {
        List<CafeDto> cafes = cafeService.getAllCafes();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {
            model.addAttribute("username", auth.getName());
        }

        String cafesJson = objectMapper.writeValueAsString(cafes);
        model.addAttribute("cafesJson", cafesJson);
        // 모델에 카페 리스트 추가
        model.addAttribute("cafes", cafes);
        return "main"; // Thymeleaf 템플릿 이름
    }

    // 카페 검색 처리
    @PostMapping("/search-cafe")
    @ResponseBody
    public List<CafeDto> searchCafe(@RequestBody SearchRequest searchRequest) {
        String query = searchRequest.getQuery();
        return cafeService.searchCafesByName(query);
    }

    // 즐겨찾기 페이지
    @GetMapping("/favorites")
    public String favoriteCafe(Model model, Authentication authentication) {
        // 인증되지 않은 사용자를 로그인 페이지로 리다이렉트
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 인증된 사용자만 즐겨찾기 정보를 추가하고 페이지를 렌더링
        List<CafeDto> cafes = cafeService.getAllCafes();
        model.addAttribute("cafes", cafes);
        return "favorites";
    }


    @GetMapping("/find-store")
    public String renderFindStorePage(Model model, Authentication authentication) {
        // 인증되지 않은 사용자를 로그인 페이지로 리다이렉트
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 인증된 사용자만 카테고리 정보를 추가하고 페이지를 렌더링
        List<CategoryDto> categories = cafeService.getAllCategories();
        model.addAttribute("categories", categories);
        return "find-store";
    }

    //친구 페이지
    @GetMapping("/friends")
    public String renderFriendPage(Authentication authentication) {
        // 인증되지 않은 사용자를 로그인 페이지로 리다이렉트
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 인증된 사용자만 즐겨찾기 정보를 추가하고 페이지를 렌더링
        return "friend";
    }

    //사용자 검색 페이지
    @GetMapping("/user-search")
    public String renderUserSerarchPage(Authentication authentication) {
        // 인증되지 않은 사용자를 로그인 페이지로 리다이렉트
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // 인증된 사용자만 즐겨찾기 정보를 추가하고 페이지를 렌더링
        return "user-search";
    }


    // SearchRequest DTO 클래스
    public static class SearchRequest {
        private String query;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }
}
