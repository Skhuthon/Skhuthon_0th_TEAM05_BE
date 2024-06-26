package org.skhuton.skhudebug.bughunt.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.skhuton.skhudebug.bughunt.domain.Bughunt;
import org.skhuton.skhudebug.bughunt.dto.request.BughuntLocationReqDto;
import org.skhuton.skhudebug.bughunt.dto.request.BughuntSaveReqDto;
import org.skhuton.skhudebug.bughunt.dto.response.BughuntInfoResDto;
import org.skhuton.skhudebug.bughunt.dto.response.BughuntListResDto;
import org.skhuton.skhudebug.bughunt.repository.BughuntRepository;
import org.skhuton.skhudebug.exception.ErrorCode;
import org.skhuton.skhudebug.exception.model.NotFoundException;
import org.skhuton.skhudebug.match.domain.HuntReqManagement;
import org.skhuton.skhudebug.match.repository.HuntMatchRepository;
import org.skhuton.skhudebug.member.domain.User;
import org.skhuton.skhudebug.member.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BughuntService {
    private final BughuntRepository bughuntRepository;
    private final UserRepository userRepository;
    private final HuntMatchRepository huntMatchRepository;

    @Transactional
    public void save(BughuntSaveReqDto bughuntSaveReqDto) {
        User user = userRepository.findByLoginId(bughuntSaveReqDto.loginId()).orElseThrow(
                ()-> new NotFoundException(
                        ErrorCode.USER_NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND.getMessage()
                ));
        Bughunt bughunt = Bughunt.builder()
                .user(user)
                .latitude(bughuntSaveReqDto.latitude())
                .longitude(bughuntSaveReqDto.longitude())
                .bugNum(bughuntSaveReqDto.bugNum())
                .bugSize(bughuntSaveReqDto.bugSize())
                .bugType(bughuntSaveReqDto.bugType())
                .radius(bughuntSaveReqDto.radius())
                .build();
        bughunt.setCreatedAt(LocalDateTime.now());
        bughuntRepository.save(bughunt);

        //요청 아이디 값 가져와서 저장하기.
        Bughunt getvalue = bughuntRepository.findByUser(user);
        HuntReqManagement huntReqManagement = HuntReqManagement.builder()
                .requestId(getvalue.getId())
                .senderId(user.getLoginId())
                .receiveId(null)
                .complete(false)
                .build();
        huntMatchRepository.save(huntReqManagement);
    }

    public BughuntListResDto findAll(){
        List<Bughunt> bughunts = bughuntRepository.findAll();
        List<BughuntInfoResDto> bughuntInfoResDtoList = bughunts.stream()
                .map(BughuntInfoResDto::from)
                .toList();
        return BughuntListResDto.from(bughuntInfoResDtoList);
    }

/*    public BughuntInfoResDto findById(Long id){
        Bughunt bughunt = bughuntRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("버그 헌트 요청 id 조회 불가: " + id));
        return BughuntInfoResDto.from(bughunt);
    }*/

    public BughuntInfoResDto findByLoginId(String loginId) {    // bughunt id 조회 -> loginId(사용자당 하나의 구인 올릴 수 있음)
        Bughunt bughunt = bughuntRepository.findByUserLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("해당 아이디 조회 불가: " + loginId));
        return BughuntInfoResDto.from(bughunt);
    }

    public BughuntListResDto findByRadius(BigDecimal latitude, BigDecimal longitude, int radius) {

        // 반경: 미터 -> 각도
        BigDecimal radiusInMeters = new BigDecimal(radius);
        BigDecimal oneDegreeInMeters = new BigDecimal(111000);  // 위도 1도는 약 111,000m

        // 위도 범위 변환
        BigDecimal rangeLat = radiusInMeters.divide(oneDegreeInMeters, MathContext.DECIMAL128); //반경을 미터 단위로 나눔

        // 위도를 고려한 경도 범위 변환
        BigDecimal cosLat = BigDecimal.valueOf(Math.cos(Math.toRadians(latitude.doubleValue())));   //cosin 조정, 적도에서 멀어질수록 경도 사이의 거리가 줄어듦
        BigDecimal rangeLng = radiusInMeters.divide(oneDegreeInMeters.multiply(cosLat), MathContext.DECIMAL128);

        // 거리 계산(경계 상자 생성)
        BigDecimal minLat = latitude.subtract(rangeLat);
        BigDecimal maxLat = latitude.add(rangeLat);
        BigDecimal minLng = longitude.subtract(rangeLng);
        BigDecimal maxLng = longitude.add(rangeLng);

        List<Bughunt> bughunts = bughuntRepository.findByLatitudeBetweenAndLongitudeBetween(minLat, maxLat, minLng, maxLng);
        List<BughuntInfoResDto> bughuntInfoResDtoList = bughunts.stream()
                .map(BughuntInfoResDto::from)
                .toList();
        return BughuntListResDto.from(bughuntInfoResDtoList);
    }
}
