package io.github.tuyendev.mbs.common.entity.rdb;

import io.github.tuyendev.mbs.common.CommonConstants;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Table(value = CommonConstants.EntityName.FEATURE)
@Builder
public class Feature extends AbstractJdbcEntity<Long> {

    private String name;

    private Integer type;

    private String description;

    @MappedCollection(idColumn = "feature_id")
    private Set<Authority> authorities;

    public Feature() {
    }

    public Feature(String name, Integer type, String description, Set<Authority> authorities) {
        this.name = name;
        this.type = Objects.requireNonNullElse(type, CommonConstants.FeatureType.APP);
        this.description = description;
        this.authorities = authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return Objects.equals(id, feature.id) && Objects.equals(name, feature.name) && Objects.equals(type, feature.type) && Objects.equals(description, feature.description) && Objects.equals(authorities, feature.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, description, authorities);
    }
}
