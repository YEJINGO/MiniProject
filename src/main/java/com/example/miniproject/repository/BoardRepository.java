package com.example.miniproject.repository;

import com.example.miniproject.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllBySeasonOrderByCreatedAtDesc(String season);

    @Query("select b from Board b where b.season=:season and (b.title like concat('%',:keyword,'%') or b.placename like concat('%',:keyword,'%') or b.content like concat('%',:keyword,'%')) order by b.createdAt desc")
    List<Board> findAllBySeasonAndContainingKeywordOrderByCreatedAtDesc(String season, String keyword);

    @Query("select b from Board b where b.season=:season and b.location=:location and (b.title like concat('%',:keyword,'%') or b.placename like concat('%',:keyword,'%') or b.content like concat('%',:keyword,'%')) order by b.createdAt desc")
    List<Board> findAllBySeasonAndLocationAndContainingKeywordOrderByCreatedAtDesc(String season, String location, String keyword);

    @Query("select b from Board b where b.season=:season and (b.title like concat('%',:keyword,'%') or b.placename like concat('%',:keyword,'%') or b.content like concat('%',:keyword,'%')) order by b.star")
    List<Board> findAllBySeasonAndContainingKeywordOrderByStar(String season, String keyword);

    @Query("select b from Board b where b.season=:season and (b.title like concat('%',:keyword,'%') or b.placename like concat('%',:keyword,'%') or b.content like concat('%',:keyword,'%')) order by b.star desc")
    List<Board> findAllBySeasonAndContainingKeywordOrderByStarDesc(String season, String keyword);

    @Query("select b from Board b where b.season=:season and b.location=:location and (b.title like concat('%',:keyword,'%') or b.placename like concat('%',:keyword,'%') or b.content like concat('%',:keyword,'%')) order by b.star")
    List<Board> findAllBySeasonAndLocationAndContainingKeywordOrderByStar(String season, String location, String keyword);

    @Query("select b from Board b where b.season=:season and b.location=:location and (b.title like concat('%',:keyword,'%') or b.placename like concat('%',:keyword,'%') or b.content like concat('%',:keyword,'%')) order by b.star desc")
    List<Board> findAllBySeasonAndLocationAndContainingKeywordOrderByStarDesc(String season, String location, String keyword);

    List<Board> findAllBySeasonAndLocationOrderByCreatedAtDesc(String season, String location);

    List<Board> findAllBySeasonOrderByStar(String season);

    List<Board> findAllBySeasonOrderByStarDesc(String season);

    List<Board> findAllBySeasonAndLocationOrderByStar(String season, String location);

    List<Board> findAllBySeasonAndLocationOrderByStarDesc(String season, String location);
}
