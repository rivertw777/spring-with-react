package backend.spring.member.controller;

import backend.spring.member.model.dto.MemberSignupDto;
import backend.spring.member.model.dto.MemberUpdateDto;
import backend.spring.member.model.entity.Member;
import backend.spring.member.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class MemberController {

    @Autowired
    private final MemberService userService;

    // 회원 가입
    @PostMapping("")
    public ResponseEntity<?> signUp(@Valid @RequestBody MemberSignupDto signupParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            userService.registerUser(signupParam);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 회원 단일 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Member> getUserById(@PathVariable Long userId) {
        Optional<Member> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 회원 전체 조회
    @GetMapping("")
    public ResponseEntity<List<Member>> getAllUsers() {
        List<Member> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 회원 정보 수정
    @PutMapping("/{userId}")
    public ResponseEntity<?> modifyUser(@PathVariable Long userId, @Valid @RequestBody MemberUpdateDto updateParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        //userService.modifyUser(userId, updateParam);
        return ResponseEntity.ok().build();
    }

    // 회원 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> removeUser(@PathVariable Long userId) {
        userService.removeUser(userId);
        return ResponseEntity.ok().build();
    }

}

