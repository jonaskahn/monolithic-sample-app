package io.github.tuyendev.mbs.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class CopyUtils {
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(field -> Objects.isNull(wrappedSource.getPropertyValue(field)))
                .toArray(String[]::new);
    }

    private static String[] getEmptyPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(field -> wrappedSource.getPropertyType(field) == String.class && StringUtils.isEmpty((String) wrappedSource.getPropertyValue(field)))
                .toArray(String[]::new);
    }

    public static void copyProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target);
    }

    public static void copyPropertiesIgnoredNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static void copyPropertiesIgnoredEmpty(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getEmptyPropertyNames(src));
    }

    public static void copyPropertiesIgnoredNullOrEmpty(Object src, Object target) {
        String[] ignoredFields = Stream.of(getEmptyPropertyNames(src), getNullPropertyNames(src))
                .flatMap(Stream::of)
                .distinct()
                .toArray(String[]::new);
        BeanUtils.copyProperties(src, target, ignoredFields);
    }
}
