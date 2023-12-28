package com.studycafe.modules.tag.service;

import com.studycafe.modules.tag.domain.Tag;
import com.studycafe.modules.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String title) {
        return tagRepository.findByTitle(title).orElseGet(() -> tagRepository.save(Tag.builder().title(title).build()));
    }
}
