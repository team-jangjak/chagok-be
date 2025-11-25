package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.CheckMethodDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckMethodDetailRepository extends JpaRepository<CheckMethodDetail, Long> {
     List<CheckMethodDetail> findByCheckMethodIdOrderByMethodOrderAsc(Long checkMethodId);
     void deleteAllByCheckMethodId(Long checkMethodId);
}
