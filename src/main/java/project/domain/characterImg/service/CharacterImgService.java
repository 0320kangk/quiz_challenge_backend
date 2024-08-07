package project.domain.characterImg.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.characterImg.dto.CharacterImgDto;
import project.domain.characterImg.entity.CharacterImg;
import project.domain.characterImg.repository.CharacterImgRepository;
import project.domain.member.entity.Member;
import project.domain.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CharacterImgService {

    private final MemberRepository memberRepository;
    private final CharacterImgRepository characterImgRepository;
    @Value("${character-img-path}")
    private String characterImgPath;
    public CharacterImg getDefaultCharacterImg() {
        return characterImgRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("1번 캐릭터가 없습니다."));
    }
    @Transactional
    public void changeCharacterImg(String memberEmail, String characterImgName) {
        Member member = memberRepository.findOneByEmail(memberEmail).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        CharacterImg characterImg = characterImgRepository.findOneByImgName(characterImgName).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지이름입니다."));
        member.setCharacterImg(characterImg);
        memberRepository.save(member);
    }

    public String getMemberCharacterImgFullPath(String memberEmail){
        String imgName = memberRepository.findOneCharacterImgNameByEmail(memberEmail).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        return characterImgPath +  imgName;
    }

    public List<CharacterImgDto> getAllCharacter(){
        List<CharacterImg> allCharacterImg = characterImgRepository.findAll();
        List<CharacterImgDto> characterImgDtos = allCharacterImg.stream()
                .map((characterImg) -> {
                    return CharacterImgDto.builder()
                            .name(characterImg.getImgName())
                            .build();
                })
                .toList();
        return characterImgDtos;
    }
    public String getCharacterImgFullPathByImgName(String imgName){
        if(characterImgRepository.findOneByImgName(imgName).isPresent()){
            return characterImgPath + imgName;
        }
        throw new IllegalArgumentException("존재하지 않는 캐릭터입니다.");
    }

    @Transactional
    public void updateMemberCharacterImg(String email, String selectedCharacterImg){
        Member member = memberRepository.findOneByEmail(email).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 email입니다."));
        Optional<CharacterImg> oneByImgName = characterImgRepository.findOneByImgName(selectedCharacterImg);
        if(oneByImgName.isPresent()){
            member.setCharacterImg(oneByImgName.get());
        } else {
            throw new IllegalArgumentException("존재하지 않는 캐릭터 이미지 이름입니다.");
        }
    }
}
