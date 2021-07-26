package com.studyolle.tag;

import com.studyolle.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String tagTitle) {
        return tagRepository.findByTitle(tagTitle)
                .orElseGet(() -> tagRepository.save(
                        Tag.builder()
                                .title(tagTitle)
                                .build()
                        )
                );
    }
}
