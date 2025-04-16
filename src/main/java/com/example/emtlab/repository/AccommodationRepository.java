package com.example.emtlab.repository;

import com.example.emtlab.model.domain.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    /*
    If you want to find the most visited accommodation
         private int visitCount; - inside Accommodation

    This gets the accommodation with the highest visitCount

    *Optional<Accommodation> findTopByOrderByVisitCountDesc();

     */
}
