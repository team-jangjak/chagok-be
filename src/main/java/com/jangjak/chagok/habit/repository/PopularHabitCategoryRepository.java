package com.jangjak.chagok.habit.repository;

import com.jangjak.chagok.habit.entity.PopularHabitCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularHabitCategoryRepository extends JpaRepository<PopularHabitCategory, Long> {
//    @Query("""
//                SELECT new com.jangjak.chagok.habit.dto.value.PopularCategoryDto(
//                    p.categoryId,
//                    c.name
//                )
//                FROM PopularHabitCategory p
//                JOIN HabitCategory c ON p.categoryId = c.id
//                ORDER BY p.usageCount DESC
//            """)
//    List<PopularCategoryDto> findAllWithCategoryName();
}
