package backend.spring.member.service.impl;

import backend.spring.member.model.dto.request.MemberSignupRequest;
import backend.spring.member.model.entity.Member;
import backend.spring.member.model.entity.Role;
import backend.spring.member.repository.MemberRepository;
import backend.spring.member.service.MemberService;
import backend.spring.member.service.filter.MemberFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    @Value("${avatar.url}")
    private String serverUrl;

    @Autowired
    private final MemberRepository memberRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final MemberFilter memberFilter;

    // 회원 가입
    @Override
    public void registerUser(MemberSignupRequest signupParam) {

        // 이미 존재하는 사용자 이름이라면 예외 발생
        validateDuplicateUser(signupParam.name());

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(signupParam.password());
        // 일반 권한
        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);

        // 회원 저장
        Member member = Member.builder()
                .name(signupParam.name())
                .password(encodedPassword)
                .roles(roles)
                .build();
        memberRepository.save(member);

        // 아바타 이미지 경로 저장
        String avatarUrl = generateAvatarUrl(member.getMemberId());
        member.setAvatarUrl(avatarUrl);
    }

    private void validateDuplicateUser(String username){
        Optional<Member> findUser = memberRepository.findByName(username);
        if (findUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다: " + username);
        }
    }

    // 추천 회원 리스트 반환
    @Override
    public List<Member> getSuggestions(Member member) {
        // 모든 회원 반환
        List<Member> members = memberRepository.findAll();

        // 필터링 거쳐서 추천 이용자 리스트 반환
        List<Member> suggestions = memberFilter.toSuggestions(members, member);
        return suggestions;
    }

    // 아바타 url 생성 (임시)
    private String generateAvatarUrl(Long userId) {
        String avatarUrl = serverUrl + userId + ".png";
        return avatarUrl;
    }

    @Override
    public void followMember(Member member, String suggestionMemberName) {
        //  추천 회원 조회
        Member suggestionMember = findSuggestionMember(suggestionMemberName);

        // 팔로잉 목록에 추가
        member.getFollowingSet().add(suggestionMember);
        memberRepository.save(member);
    }

    @Override
    public void unfollowMember(Member member, String suggestionMemberName) {
        //  추천 회원 조회
        Member suggestionMember = findSuggestionMember(suggestionMemberName);

        // 팔로잉 목록에서 제거
        member.getFollowingSet().remove(suggestionMember);
        memberRepository.save(member);
    }

    // 이름으로 찾아서 반환
    private Member findSuggestionMember(String suggestionMemberName){
        Member suggestionMember = memberRepository.findByName(suggestionMemberName)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 이름을 가진 회원이 없습니다."));
        return suggestionMember;
    }

    // 모든 회원 조회
    @Override
    public List<Member> getAllUsers() {
        return memberRepository.findAll();
    }

}