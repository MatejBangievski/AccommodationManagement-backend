package com.example.emtlab.repository;

import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.projections.AccommodationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Query(value = "SELECT category as category, count(*) as count FROM accommodation GROUP BY category", nativeQuery = true)
    List<AccommodationProjection> takeCategoryAndCountByProjection();

}
