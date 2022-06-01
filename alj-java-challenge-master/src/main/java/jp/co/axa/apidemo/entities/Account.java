package jp.co.axa.apidemo.entities;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 *  Account entity is 'API account'.
 *  Only registered account can retrieve/add/update/delete employee data.
 *  User has admin flag indicating that admin account can manage account list as well.
 */
@Entity
@Table(name = "account")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

  private static final long serialVersionUID = -5419864267567225800L;

  // ID
  @ApiModelProperty(value = "ID", required = false, position = 1, example = "10", hidden=true)
  @Id
  @Getter
  @Setter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  // Login Name (name)
  @ApiModelProperty(value = "Login Name", required = true, position = 2, example = "axa")
  @Getter
  @Setter
  @Column(name = "name", nullable = false, length = 128)
  private String name;

  // Password
  @ApiModelProperty(value = "Password", required = true, position = 3, example = "YourPassword")
  @Getter
  @Setter
  @Column(name = "password", nullable = false, length = 255)
  private String password;

  // admin_flag
  @ApiModelProperty(value = "Administrator Flag. If true, account can manage (view/add/delete etc) API accounts list", required = true, position = 4, example = "true")
  @Getter
  @Setter
  @Column(name = "admin_flag", nullable = false)
  private Boolean admin;

  public static Account of(String name, String password, boolean admin) {
    return Account.builder().name(name).password(password).admin(admin).build();
  }

  @Override
  public String toString() {
    return String.format("ID=%s, Name=%s, Admin=%s", id, name, admin);
  }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (admin == null) {
			if (other.admin != null)
				return false;
		} else if (!admin.equals(other.admin))
			return false;
		return true;
	}    

}
