package com.studyolle.modules.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
