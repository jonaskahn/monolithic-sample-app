package io.github.tuyendev.mbs.common.repository.rdb;

import io.github.tuyendev.mbs.common.entity.rdb.Authority;
import io.github.tuyendev.mbs.common.entity.rdb.Feature;
import io.github.tuyendev.mbs.common.utils.AppContextUtils;
import one.util.streamex.StreamEx;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface FeatureRepository extends CrudRepository<Feature, Long> {

    /**
     * Manually audit relationship due Spring Data Jdbc cannot automatically apply auditing for @MappedCollection
     *
     * @param feature
     * @return
     */
    default Feature saveOrUpdate(Feature feature) {
        Set<Authority> authorities = feature.getAuthorities();
        if (Objects.nonNull(authorities)) {
            authorities.forEach(this::auditAuthority);
        }
        return save(feature);
    }

    private void auditAuthority(Authority authority) {
        if (Objects.isNull(authority.getCreatedBy())) {
            authority.setCreatedBy(AppContextUtils.getCurrentLoginUserId());
        }
        authority.setLastModifiedBy(AppContextUtils.getCurrentLoginUserId());
        LocalDateTime now = LocalDateTime.now();
        if (Objects.isNull(authority.getCreatedDate())) {
            authority.setCreatedDate(now);
        }
        authority.setLastModifiedDate(now);
    }

    default Collection<Feature> saveOrUpdate(Collection<Feature> features) {
        return StreamEx.of(features)
                .map(this::saveOrUpdate)
                .collect(Collectors.toSet());
    }

    boolean existsByName(String name);

    Optional<Feature> findFeatureByName(String name);
}
