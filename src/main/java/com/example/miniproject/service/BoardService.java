package com.example.miniproject.service;

import com.example.miniproject.config.security.UserDetailsImp;
import com.example.miniproject.dto.BoardRequestDto;
import com.example.miniproject.dto.BoardResponseDto;
import com.example.miniproject.dto.MsgAndHttpStatusDto;
import com.example.miniproject.entity.Board;
import com.example.miniproject.repository.BoardRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    @Value("${com.example.upload.path}")
    private String uploadPath; // (로컬 테스트 위한 uploadPath)

    private final BoardRepository boardRepository;

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

        return ResponseEntity.badRequest().body(new MsgAndHttpStatusDto("이미지 파일을 업로드해주세요.", HttpStatus.BAD_REQUEST.value()));
    }
}
