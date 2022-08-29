package io.github.tuyendev.mbs.common.security;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public interface SecuredUser extends Serializable {

	Long getId();

    String getUsername();

    String getPreferredUsername();

    String getPassword();

    String getEmail();

    String getFamilyName();

    String getMiddleName();

    String getGivenName();

    String getName();

    String getPhoneNumber();

    Integer getGender();

    LocalDate getBirthdate();

    Integer getEnabled();

    Integer getLocked();

    Set<String> getAuthorityNames();
}
