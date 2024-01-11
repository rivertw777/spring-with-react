package backend.spring.sns.service.impl.dev;

import static backend.spring.exception.member.constants.MemberExceptionMessages.MEMBER_ID_NOT_FOUND;

import backend.spring.exception.member.MemberNotFoundException;
import backend.spring.member.model.entity.Member;
import backend.spring.member.repository.MemberRepository;
import backend.spring.sns.dto.request.PostUploadRequest;
import backend.spring.sns.model.entity.Post;
import backend.spring.sns.repository.CommentRepository;
import backend.spring.sns.repository.PostRepository;
import backend.spring.sns.service.impl.SnsServiceImpl;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("dev")
@Service
public class SnsServiceDev extends SnsServiceImpl {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public SnsServiceDev(PostRepository postRepository,
                          CommentRepository commentRepository,
                          MemberRepository memberRepository) {
        super(postRepository, commentRepository, memberRepository);
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @Value("${photo.save.dir}")
    private String uploadPath;
    @Value("${photo.access.url}")
    private String accessUrl;

    // 게시물 등록
    @Override
    public void registerPost(Long memberId, PostUploadRequest uploadParam) throws IOException {
        // 로그인 중인 회원 조회
        Member member = findMember(memberId);

        // 사진 경로 반환
        String photoUrl = savePhotos(uploadParam.photo());

        // 게시물 저장
        Post post = Post.builder()
                .author(member)
                .photoUrl(photoUrl)
                .caption(uploadParam.caption())
                .location(uploadParam.location())
                .build();
        postRepository.save(post);
    }

    // 사진 저장 및 경로 반환
    private String savePhotos(MultipartFile photo) throws IOException {
        // 사진 이름 생성
        String projectPath = System.getProperty("user.dir") + uploadPath;
        String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
        // 사진 파일 저장
        File saveFile = new File(projectPath, fileName);
        photo.transferTo(saveFile);
        // post 모델에 담길 경로
        String photoUrl = accessUrl + fileName;
        return photoUrl;
    }

    // 회원 반환
    private Member findMember(Long memberId){
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException(MEMBER_ID_NOT_FOUND.getMessage()));
        return member;
    }

}