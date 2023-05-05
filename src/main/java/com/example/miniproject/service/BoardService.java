package com.example.miniproject.service;

import com.example.miniproject.config.security.UserDetailsImp;
import com.example.miniproject.dto.BoardRequestDto;
import com.example.miniproject.dto.BoardResponseDto;
import com.example.miniproject.dto.FilterRequestDto;
import com.example.miniproject.dto.MsgAndHttpStatusDto;
import com.example.miniproject.entity.Board;
import com.example.miniproject.repository.BoardRepository;
import com.example.miniproject.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    @Value("${com.example.upload.path}")
    private String uploadPath; // (로컬 테스트 위한 uploadPath)

    private final BoardRepository boardRepository;

//    private final S3Uploader s3Uploader;

    @Transactional
    public ResponseEntity<?> createBoard(BoardRequestDto boardRequestDto, UserDetailsImp userDetailsImp) throws IOException {

        if (boardRequestDto.getImage() != null) {
            MultipartFile imgFile = boardRequestDto.getImage();
            String originalName = imgFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            Path savePath = Paths.get(uploadPath, uuid + "_" + originalName);

            imgFile.transferTo(savePath);

            String imgPath = savePath.toString();

            Board board = new Board(boardRequestDto, imgPath);
            board.setUser(userDetailsImp.getUser());
            boardRepository.save(board);

            return ResponseEntity.ok(new BoardResponseDto(board));
        }

        /*
        S3에 이미지 저장
        if (boardRequestDto.getImage() != null) {
            String imgPath = s3Uploader.upload(boardRequestDTO.getImage());
            Board board = new Board(boardRequestDto, imgPath);
            boardRepository.save(board);

            BoardResponseDto boardResponseDto = new BoardResponseDto(board);
            return ResponseEntity.ok(boardResponseDto);
        }
         */

        return ResponseEntity.badRequest().body(new MsgAndHttpStatusDto("이미지 파일을 업로드해주세요.", HttpStatus.BAD_REQUEST.value()));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<BoardResponseDto>> getBoarsWithFilter(FilterRequestDto filterRequestDto) {
        List<Board> boardList = null;

        if (filterRequestDto.getLocation() == null && filterRequestDto.getStar() == null & filterRequestDto.getKeyword() == null) { // keyword : x, location : x, star : x
            boardList = boardRepository.findAllBySeasonOrderByCreatedAtDesc(filterRequestDto.getSeason());
        } else { // 2. 그 외 필터 조건들 있을 경우 조건 모두 반영
            if (filterRequestDto.getKeyword() != null) {
                if (filterRequestDto.getLocation() == null && filterRequestDto.getStar() == null) { // keyword : o, location : x, star : x
                    boardList = boardRepository.findAllBySeasonAndContainingKeywordOrderByCreatedAtDesc(filterRequestDto.getSeason(), filterRequestDto.getKeyword());
                } else if (filterRequestDto.getLocation() != null && filterRequestDto.getStar() == null) { // keyword : o, location : o, star : x
                    boardList = boardRepository.findAllBySeasonAndLocationAndContainingKeywordOrderByCreatedAtDesc(filterRequestDto.getSeason(), filterRequestDto.getLocation(), filterRequestDto.getKeyword());
                } else if (filterRequestDto.getLocation() == null && filterRequestDto.getStar() != null) { // // keyword : o, location : x, star : o
                    if (filterRequestDto.getStar().equals("asc")) {
                        boardList = boardRepository.findAllBySeasonAndContainingKeywordOrderByStar(filterRequestDto.getSeason(), filterRequestDto.getKeyword());
                    } else {
                        boardList = boardRepository.findAllBySeasonAndContainingKeywordOrderByStarDesc(filterRequestDto.getSeason(), filterRequestDto.getKeyword());
                    }
                } else { // keyword : o, location : o, star : o
                    if (filterRequestDto.getStar().equals("asc")) {
                        boardList = boardRepository.findAllBySeasonAndLocationAndContainingKeywordOrderByStar(filterRequestDto.getSeason(), filterRequestDto.getLocation(), filterRequestDto.getKeyword());
                    } else {
                        boardList = boardRepository.findAllBySeasonAndLocationAndContainingKeywordOrderByStarDesc(filterRequestDto.getSeason(), filterRequestDto.getLocation(), filterRequestDto.getKeyword());
                    }
                }
            } else {
                if (filterRequestDto.getLocation() != null && filterRequestDto.getStar() == null) { // keyword : x, location : o, star : x
                    boardList = boardRepository.findAllBySeasonAndLocationOrderByCreatedAtDesc(filterRequestDto.getSeason(), filterRequestDto.getLocation());
                } else if (filterRequestDto.getLocation() == null && filterRequestDto.getStar() != null) { // keyword : x, location : x, star : o
                    if (filterRequestDto.getStar().equals("asc")) {
                        boardList = boardRepository.findAllBySeasonOrderByStar(filterRequestDto.getSeason());
                    } else {
                        boardList = boardRepository.findAllBySeasonOrderByStarDesc(filterRequestDto.getSeason());
                    }
                } else { // keyword : x, location : o, star : o
                    if (filterRequestDto.getStar().equals("asc")) {
                        boardList = boardRepository.findAllBySeasonAndLocationOrderByStar(filterRequestDto.getSeason(), filterRequestDto.getLocation());
                    } else {
                        boardList = boardRepository.findAllBySeasonAndLocationOrderByStarDesc(filterRequestDto.getSeason(), filterRequestDto.getLocation());
                    }
                }
            }
        }
        List<BoardResponseDto> boardResponseDtoList = boardList.stream().map(BoardResponseDto::new).toList();
        return ResponseEntity.ok(boardResponseDtoList);
    }

    @Transactional
    public ResponseEntity<MsgAndHttpStatusDto> deleteBoard(Long boardId, UserDetailsImp userDetails) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 게시글입니다.")
        );

        if (!board.getUser().getUserId().equals(userDetails.getUser().getUserId())) {
            return ResponseEntity.badRequest().body(new MsgAndHttpStatusDto("본인이 작성한 글만 삭제 가능합니다.", HttpStatus.FORBIDDEN.value()));
        }

        boardRepository.delete(board);
        return ResponseEntity.ok(new MsgAndHttpStatusDto("삭제 완료!", HttpStatus.OK.value()));

        /*
        S3 에서 이미지 파일 삭제
        String imgPath = board.getImage();
        if(s3Uploader.delete(imgPath)) {
            return ResponseEntity.ok(new MsgAndHttpStatusDto("삭제 완료!"), HttpStatus.OK.value());
        } else {
            return ResponseEntity.bad_request().body(new MsgAndHttpStatusDto("삭제 실패!", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
         */
    }
}
