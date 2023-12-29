package com.studycafe.modules.account.repository;

import com.querydsl.core.types.Predicate;
import com.studycafe.modules.account.domain.QAccount;
import com.studycafe.modules.tag.domain.Tag;
import com.studycafe.modules.zone.domain.Zone;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
