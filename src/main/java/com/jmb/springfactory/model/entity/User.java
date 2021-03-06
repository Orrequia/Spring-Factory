package com.jmb.springfactory.model.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table
public class User extends BaseEntity{
  
  private static final String VALIDATION_EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$";

  @NotNull
  @Size(min = 9, max = 9)
  private String nif;

  @NotNull
  private String name;

  @NotNull
  private String surname;

  @NotNull
  @Size(min = 9, max = 9)
  private String phoneNumber;

  @Pattern(regexp = VALIDATION_EMAIL_PATTERN, flags = Flag.CASE_INSENSITIVE)
  private String email;
  
  @ManyToOne(fetch = FetchType.LAZY)
  private Rol rol;
  
  @ManyToOne(fetch = FetchType.LAZY)
  private WorkGroup group;
}
