package com.studyolle.tag;

import com.studyolle.domain.Tag;
import org.springframework.core.metrics.StartupStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

@Transactional(readOnly = true)
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTitle(String title);
}
