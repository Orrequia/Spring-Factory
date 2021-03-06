package com.jmb.springfactory.model.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RolDto extends BaseDto {

  private static final long serialVersionUID = 4210798617822128844L;

  @NotNull
  private String name;
  private List<PermisionDto> permissions;
}
