package project.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.characterImg.entity.CharacterImg;
import project.domain.characterImg.repository.CharacterImgRepository;
import project.domain.member.dto.JoinFormDto;
import project.domain.member.dto.MyMemberInfoDto;
import project.domain.member.entity.Authority;
import project.domain.member.entity.Member;
import project.domain.member.entity.enumerate.Role;
import project.domain.member.repository.MemberRepository;
import project.domain.security.jwt.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CharacterImgRepository characterImgRepository;

    @Transactional
    public void joinMember(JoinFormDto joinFormDto){

        // 가입되어 있지 않은 회원이면,
        // 권한 정보 만들고
        Authority authority = Authority.builder()
                .authorityName(Role.GUEST.getKey())
                .build();
        CharacterImg characterImg = characterImgRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("1번 캐릭터가 없습니다."));

        Member member = Member.builder()
                .name(joinFormDto.getName())
                .email(joinFormDto.getEmail())
                .password(bCryptPasswordEncoder.encode(joinFormDto.getPasswordOrigin()))
                .authorities(Collections.singleton(authority))
                .characterImg(characterImg)
                .activated(true)
                .createdDate(LocalDateTime.now())
                .build();

        memberRepository.save(member);
    }

    // 유저,권한 정보를 가져오는 메소드
    @Transactional(readOnly = true)
    public Optional<Member> getMemberWithAuthorities(String username) {
        return memberRepository.findOneWithAuthoritiesByEmail(username);
    }
    // 현재 securityContext에 저장된 username의 정보만 가져오는 메소드
    @Transactional(readOnly = true)
    public MyMemberInfoDto getMyMemberWithAuthorities() {
        Member member = SecurityUtil.getCurrentUsername()
                .flatMap(memberRepository::findOneWithAuthoritiesByEmail).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이름입니다."));

        MyMemberInfoDto myMemberInfoDto = MyMemberInfoDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .authorities(member.getAuthorities())
                .createdDate(member.getCreatedDate())
                .build();

        return myMemberInfoDto;
    }
}
