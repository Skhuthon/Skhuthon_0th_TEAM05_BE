package org.skhuton.skhudebug.bughunt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.skhuton.skhudebug.bughunt.dto.response.BughuntListResDto;
import org.skhuton.skhudebug.common.dto.BaseResponse;
import org.skhuton.skhudebug.exception.SuccessCode;
import org.skhuton.skhudebug.bughunt.dto.request.BughuntSaveReqDto;
import org.skhuton.skhudebug.bughunt.service.BughuntService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bughunt")
@Tag(name = "Bughunt API", description = "버그 헌트 요청 및 추적")
public class BughuntController {
    private final BughuntService bughuntService;

    @Operation(summary = "버그 헌트 요청 생성", description = "사용자 위치 및 벌레 세부정보가 포함된 버그헌터 찾기 요청 생성하기")
    @PostMapping
    public BaseResponse<String> bughuntCreate(@RequestBody BughuntSaveReqDto bughuntSaveReqDto) {
        bughuntService.save(bughuntSaveReqDto);
        return BaseResponse.success(SuccessCode.BUGHUNT_CREATE_SUCCESS);
    }

    @Operation(summary = "버그 헌트 요청 조회", description = "헌터 위치 범위 내에 있는 헌트 요청들 조회하기")
    @GetMapping
    public BaseResponse<BughuntListResDto> bughuntFindAll(){
        BughuntListResDto bughuntListResDto= bughuntService.findAll();
        return BaseResponse.success(SuccessCode.GET_SUCCESS, bughuntListResDto);
    }
}
