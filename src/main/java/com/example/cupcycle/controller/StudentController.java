package com.example.cupcycle.controller;

import com.example.cupcycle.entity.Student;
import com.example.cupcycle.service.ApiResponse;
import com.example.cupcycle.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginStudent(@RequestParam String email, @RequestParam String password) {
        Optional<Student> student = studentService.findStudentByEmail(email);

        if (student.isPresent() && student.get().getPassword().equals(password)) {
            ApiResponse<String> response = new ApiResponse<>(true, 1000, "로그인 성공");
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<String> response = new ApiResponse<>(false, 4004, "이메일 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.status(404).body(response);
        }
    }

    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerStudent(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {

        if (studentService.isEmailExists(email)) {
            ApiResponse<String> response = new ApiResponse<>(false, 4001, "이미 등록된 이메일입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            studentService.registerStudent(name, email, password);
            ApiResponse<String> response = new ApiResponse<>(true, 1000, "회원가입이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(false, 4005, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 마이페이지 조회 API
    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyPage(@RequestParam String email) {
        Optional<Student> student = studentService.findStudentByEmail(email);

        if (student.isPresent()) {
            Student s = student.get();

            Map<String, Object> myPageData = new HashMap<>();
            myPageData.put("studentId",s.getStudentId());
            myPageData.put("name", s.getName());
            myPageData.put("totalRewards", s.getReward());
            myPageData.put("cupCount", s.getCupCount());
            myPageData.put("carbonReduction", s.getCarbonReduction());

            ApiResponse<Map<String, Object>> response = new ApiResponse<>(true, 1000, "마이페이지 조회 성공", myPageData);
            return ResponseEntity.ok(response);
        } else {
            ApiResponse<Map<String, Object>> response = new ApiResponse<>(false, 4006, "학생 정보를 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(response);
        }
    }

}
