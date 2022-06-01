package jp.co.axa.apidemo.security;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 *  Basic Token class that provides the response of Token
 */
@AllArgsConstructor
@NoArgsConstructor
public class DemoToken implements Serializable {

  private static final long serialVersionUID = -1652848531569874558L;

  @Getter
  @Setter
  private String token;

}
